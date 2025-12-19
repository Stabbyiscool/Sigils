package fun.stabosa.sigil.sigil.logic;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import java.util.*;

public class consume {
    public static int run(ServerWorld world, BlockPos pos, Map<Item, Integer> requiredItems, boolean bulkMode) {
        List<ItemEntity> entities = world.getEntitiesByClass(ItemEntity.class, new Box(pos).expand(1.5), e -> true);
        Map<Item, List<ItemEntity>> foundItems = new HashMap<>();
        List<ItemEntity> amethysts = new ArrayList<>();

        for (ItemEntity e : entities) {
            ItemStack stack = e.getStack();
            if (stack.isOf(Items.AMETHYST_SHARD)) amethysts.add(e);
            else if (requiredItems.containsKey(stack.getItem()))
                foundItems.computeIfAbsent(stack.getItem(), k -> new ArrayList<>()).add(e);
        }

        if (amethysts.isEmpty()) return 0;

        int possibleSets = Integer.MAX_VALUE;
        for (Map.Entry<Item, Integer> entry : requiredItems.entrySet()) {
            int available = foundItems.getOrDefault(entry.getKey(), List.of())
                    .stream().mapToInt(e -> e.getStack().getCount()).sum();
            possibleSets = Math.min(possibleSets, available / entry.getValue());
        }

        int totalAmethysts = amethysts.stream().mapToInt(e -> e.getStack().getCount()).sum();
        possibleSets = Math.min(possibleSets, totalAmethysts);

        if (possibleSets <= 0) return 0;
        int setsToConsume = bulkMode ? possibleSets : 1;
        int totalAmethystNeeded = setsToConsume;
        for (ItemEntity e : amethysts) {
            if (totalAmethystNeeded <= 0) break;
            ItemStack stack = e.getStack();
            int used = Math.min(stack.getCount(), totalAmethystNeeded);
            stack.decrement(used);
            totalAmethystNeeded -= used;
            if (stack.isEmpty()) e.discard();
        }
        for (Map.Entry<Item, Integer> entry : requiredItems.entrySet()) {
            int totalNeeded = entry.getValue() * setsToConsume;
            for (ItemEntity e : foundItems.getOrDefault(entry.getKey(), List.of())) {
                if (totalNeeded <= 0) break;
                ItemStack stack = e.getStack();
                int used = Math.min(stack.getCount(), totalNeeded);
                stack.decrement(used);
                totalNeeded -= used;
                if (stack.isEmpty()) e.discard();
            }
        }

        return setsToConsume;
    }
}
