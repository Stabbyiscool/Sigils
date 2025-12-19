package fun.stabosa.sigil.sigil.types;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.item.Item;

public interface SigilType {
    BiConsumer<ServerWorld, BlockPos> create(Map<Item, Integer> inputItems, Map<String, Object> config);
}
