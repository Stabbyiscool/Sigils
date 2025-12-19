package fun.stabosa.sigil.block;

import fun.stabosa.sigil.sound.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SigilBlockItem extends BlockItem {

    public SigilBlockItem(Block block, Settings settings) {
        super(block, settings.maxDamage(20));
    }

    @Override
    public ActionResult place(ItemPlacementContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        ItemStack stack = context.getStack();
        ServerPlayerEntity player = context.getPlayer() instanceof ServerPlayerEntity sp ? sp : null;

        BlockState placementState = this.getPlacementState(context);
        if (placementState == null) return ActionResult.FAIL;
        if (!this.canPlace(context, placementState)) return ActionResult.FAIL;

        BlockState currentState = world.getBlockState(pos);

        if (!currentState.canReplace(context)) {
            pos = pos.offset(context.getSide());
            currentState = world.getBlockState(pos);
            if (!currentState.canReplace(context)) {
                return ActionResult.FAIL;
            }
        }

        if (!world.setBlockState(pos, placementState, 11)) return ActionResult.FAIL;

        if (!world.isClient) {
            placementState.getBlock().onPlaced(world, pos, placementState, player, stack);
            world.playSound(null, pos, ModSounds.SIGIL_WRITE, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }

        if (!world.isClient && player != null && !player.isCreative()) {
            stack.damage(1, player, p -> p.sendToolBreakStatus(context.getHand()));
        }

        return ActionResult.success(world.isClient);
    }

    @Override
    public boolean isDamageable() {
        return true;
    }
}
