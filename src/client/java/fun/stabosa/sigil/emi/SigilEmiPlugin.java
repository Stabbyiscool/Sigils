package fun.stabosa.sigil.emi;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import fun.stabosa.sigil.sigil.SigilExecutor;
import fun.stabosa.sigil.block.ModBlocks;

import java.util.LinkedHashMap;
import java.util.Map;

public class SigilEmiPlugin implements EmiPlugin {

    public static final Identifier CATEGORY_ID =
            new Identifier("sigil", "transmute");

    public static final EmiRecipeCategory TRANSMUTE_CATEGORY =
            new EmiRecipeCategory(CATEGORY_ID, EmiStack.of(new ItemStack(ModBlocks.SIGIL)));

    private static final int AMETHYST_COST = 1;

    @Override
    public void register(EmiRegistry registry) {

        registry.addCategory(TRANSMUTE_CATEGORY);

        for (SigilExecutor.SpellEntry entry : SigilExecutor.SPELL_EFFECTS) {

            if (!"transmute".equals(entry.type()))
                continue;

            Map<Item, Integer> inputMap = new LinkedHashMap<>();

            inputMap.put(Items.AMETHYST_SHARD, AMETHYST_COST);

            entry.inputs().forEach((item, count) ->
                    inputMap.merge((Item) item, (Integer) count, Integer::sum)
            );

            Object outputObj = entry.config().get("output");

            if (outputObj instanceof String outId) {
                Item outItem = Registries.ITEM.get(new Identifier(outId));
                registry.addRecipe(
                        new TransmuteEmiRecipe(inputMap, new ItemStack(outItem))
                );
            }

            else if (outputObj instanceof Map<?, ?> map) {
                for (Map.Entry<?, ?> e : map.entrySet()) {
                    String idStr = e.getKey().toString();
                    int count = ((Number) e.getValue()).intValue();

                    Item outItem = Registries.ITEM.get(new Identifier(idStr));

                    registry.addRecipe(
                            new TransmuteEmiRecipe(
                                    inputMap,
                                    new ItemStack(outItem, count)
                            )
                    );
                }
            }
        }
    }
}
