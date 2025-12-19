package fun.stabosa.sigil.sigil.types;

import fun.stabosa.sigil.sigil.logic.consume;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class Transmute implements SigilType {

    @Override
    public BiConsumer<ServerWorld, BlockPos> create(Map<Item, Integer> inputItems, Map<String, Object> config) {
        Map<Item, Integer> outputs = new HashMap<>();

        Object outputObj = config.get("output");
        if (outputObj instanceof Map<?, ?> outputMap) {
            for (Map.Entry<?, ?> e : outputMap.entrySet()) {
                String idStr = e.getKey().toString();
                int count = ((Number) e.getValue()).intValue();
                Item item = Registries.ITEM.get(new Identifier(idStr));
                outputs.put(item, count);
            }
        } else if (outputObj instanceof String singleId) {
            Item item = Registries.ITEM.get(new Identifier(singleId));
            outputs.put(item, 1);
        }

        return (world, pos) -> {
            int sets = consume.run(world, pos, inputItems, true);
            if (sets <= 0 || outputs.isEmpty()) return;

            for (Map.Entry<Item, Integer> entry : outputs.entrySet()) {
                Item item = entry.getKey();
                int total = entry.getValue() * sets;
                int max = item.getMaxCount();

                while (total > 0) {
                    int stackCount = Math.min(max, total);
                    ItemStack stack = new ItemStack(item, stackCount);
                    ItemEntity drop = new ItemEntity(world,
                            pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5,
                            stack);
                    drop.setPickupDelay(10);
                    world.spawnEntity(drop);
                    total -= stackCount;
                }
            }

            world.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE,
                    SoundCategory.BLOCKS, 1.0f, 1.2f);
        };
    }
}
