package fun.stabosa.sigil.block;

import fun.stabosa.sigil.sigil.SigilExecutor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SigilBlock extends Block {

    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 1, 16);
    private static final Map<BlockPos, Integer> ACTIVE_SIGILS = new HashMap<>();
    private static final Map<BlockPos, DelayedSpell> DELAYED_SIGILS = new HashMap<>();

    private record DelayedSpell(SigilExecutor.SpellEntry spell, int ticks) {}

    public SigilBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        if (!world.isClient) {
            ServerWorld serverWorld = (ServerWorld) world;
            serverWorld.scheduleBlockTick(pos, this, 2);

            serverWorld.spawnParticles(
                    new DustParticleEffect(new Vector3f(0.87f, 0.87f, 0.85f), 1.0f),
                    pos.getX() + 0.5,
                    pos.getY(),
                    pos.getZ() + 0.5,
                    5,
                    0.4,
                    0.0,
                    0.4,
                    0.0
            );
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        ACTIVE_SIGILS.computeIfPresent(pos, (p, ticks) -> ticks > 0 ? ticks - 1 : null);

        DELAYED_SIGILS.computeIfPresent(pos, (p, delayed) -> {
            if (delayed.ticks() > 0) return new DelayedSpell(delayed.spell(), delayed.ticks() - 1);
            executeSpell(world, pos, delayed.spell());
            return null;
        });

        List<ItemEntity> items = world.getEntitiesByClass(ItemEntity.class, new Box(pos).expand(1.5), item -> true);
        if (!items.isEmpty()) handleItems(world, pos, items);

        world.scheduleBlockTick(pos, this, 2);
    }

    private void handleItems(ServerWorld world, BlockPos pos, List<ItemEntity> items) {
        if (ACTIVE_SIGILS.containsKey(pos) || DELAYED_SIGILS.containsKey(pos)) return;

        boolean hasAmethyst = items.stream().anyMatch(e -> e.getStack().isOf(Items.AMETHYST_SHARD));
        if (!hasAmethyst) return;

        Map<Item, Integer> nearbyCounts = new HashMap<>();
        for (ItemEntity e : items) {
            if (!e.getStack().isOf(Items.AMETHYST_SHARD)) {
                nearbyCounts.merge(e.getStack().getItem(), e.getStack().getCount(), Integer::sum);
            }
        }

        for (SigilExecutor.SpellEntry spell : SigilExecutor.SPELL_EFFECTS) {
            Map<Item, Integer> required = spell.inputs();
            boolean matches = required.entrySet().stream()
                    .allMatch(req -> nearbyCounts.getOrDefault(req.getKey(), 0) >= req.getValue());

            if (matches) {
                DELAYED_SIGILS.put(pos, new DelayedSpell(spell, 10));
                ACTIVE_SIGILS.put(pos, 10);
                return;
            }
        }
    }

    private void executeSpell(ServerWorld world, BlockPos pos, SigilExecutor.SpellEntry spell) {
        world.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.BLOCKS, 1.2f, 1.3f);
        spawnRisingMagic(world, pos);
        spell.effect().accept(world, pos);
    }

    private void spawnRisingMagic(ServerWorld world, BlockPos pos) {
        Random random = world.getRandom();
        world.spawnParticles(ParticleTypes.FLASH, pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5, 1, 0, 0, 0, 0);
        for (int i = 0; i < 20; i++) {
            double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.5;
            double y = pos.getY() + 0.5;
            double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.5;
            double vx = (random.nextDouble() - 0.5) * 0.1;
            double vy = 0.3 + random.nextDouble() * 0.2;
            double vz = (random.nextDouble() - 0.5) * 0.1;
            world.spawnParticles(ParticleTypes.END_ROD, x, y, z, 1, vx, vy, vz, 0);
        }
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos below = pos.down();
        BlockState belowState = world.getBlockState(below);
        return Block.sideCoversSmallSquare(world, below, net.minecraft.util.math.Direction.UP) || belowState.isOf(Blocks.HOPPER);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
}
