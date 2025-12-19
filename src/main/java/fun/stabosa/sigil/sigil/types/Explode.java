package fun.stabosa.sigil.sigil.types;

import fun.stabosa.sigil.sigil.logic.consume;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.item.Item;

import java.util.Map;
import java.util.function.BiConsumer;

public class Explode implements SigilType {

    @Override
    public BiConsumer<ServerWorld, BlockPos> create(Map<Item, Integer> inputItems, Map<String, Object> config) {
        float power = config.getOrDefault("power", 2.0f) instanceof Number n ? ((Number) n).floatValue() : 2.0f;

        return (world, pos) -> {
            if (consume.run(world, pos, inputItems, false) > 0) {
                world.createExplosion(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        power, false, World.ExplosionSourceType.BLOCK);
                world.playSound(null, pos, SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.BLOCKS, 1.0f, 0.8f);
            }
        };
    }
}
