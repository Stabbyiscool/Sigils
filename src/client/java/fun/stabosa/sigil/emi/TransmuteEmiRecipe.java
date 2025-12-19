package fun.stabosa.sigil.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emi.emi.api.widget.SlotWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fun.stabosa.sigil.block.ModBlocks;

public class TransmuteEmiRecipe implements EmiRecipe {

    private final Map<Item, Integer> inputs;
    private final ItemStack output;

    public TransmuteEmiRecipe(Map<Item, Integer> inputs, ItemStack output) {
        this.inputs = inputs;
        this.output = output;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return SigilEmiPlugin.TRANSMUTE_CATEGORY;
    }

    @Override
    public Identifier getId() {
        return new Identifier("sigil", "transmute/" + output.getItem().toString().replace("minecraft:", "") + "_" + inputs.hashCode());
    }

    @Override
    public List<EmiIngredient> getInputs() {
        List<EmiIngredient> list = new ArrayList<>();
        inputs.forEach((item, count) -> list.add(EmiStack.of(new ItemStack(item, count))));
        return list;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(EmiStack.of(output));
    }

    @Override
    public int getDisplayWidth() {
        return 120;
    }

    @Override
    public int getDisplayHeight() {
        return 50;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        int xStart = 10;
        int yStart = 10;
        int columns = 3;
        int slotSize = 18;
        int inputCount = inputs.size();
        int gridWidth = columns * slotSize;
        int gridHeight = 2 * slotSize;

        int extraRows = (int) Math.ceil(inputCount / (double) columns);
        int verticalOffset = (gridHeight - extraRows * slotSize) / 2;

        int count = 0;
        for (Map.Entry<Item, Integer> e : inputs.entrySet()) {
            int row = count / columns;
            int col = count % columns;
            int slotX = xStart + col * slotSize;
            int slotY = yStart + row * slotSize + verticalOffset;
            widgets.addSlot(EmiStack.of(new ItemStack(e.getKey(), e.getValue())), slotX, slotY);
            count++;
        }

        int chalkX = xStart + gridWidth + 10;
        int chalkY = yStart + (gridHeight - 16) / 2;

        widgets.addDrawable(chalkX, chalkY, 16, 16, (drawContext, mouseX, mouseY, delta) -> {
            ItemStack chalkStack = new ItemStack(ModBlocks.SIGIL);
            drawContext.drawItem(chalkStack, 0, 0);
        });

        widgets.addSlot(EmiStack.of(output), chalkX + 24, chalkY)
                .recipeContext(this);
    }

}
