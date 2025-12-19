package fun.stabosa.sigil.item;

import fun.stabosa.sigil.Sigil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.text.Text;
import java.util.List;

public class ModItems {

    public static final Item FLESH = new Item(
        new FabricItemSettings().food(
            new net.minecraft.item.FoodComponent.Builder()
                .hunger(4)
                .saturationModifier(0.3f)
                .meat()
                .build()
        )
    );
        
    public static final Item SUGAR_COOKIE = new Item(
        new FabricItemSettings().food(
            new net.minecraft.item.FoodComponent.Builder()
                .hunger(3)
                .saturationModifier(1f)
                .statusEffect(
                    new net.minecraft.entity.effect.StatusEffectInstance(
                        net.minecraft.entity.effect.StatusEffects.REGENERATION, //idk if this is op or not
                        100,
                        1 
                    ),
                    1.0f
                )
                .build()
        )
    );


    public static final Item LEAD = new Item(new FabricItemSettings());
    // public static final Item BLOOD_SYRINGE = 
    //    new Item(new FabricItemSettings().maxCount(16));
    // public static final Item EMPTY_SYRINGE =
    //    new EmptySyringeItem(new FabricItemSettings().maxCount(16));
    public static final Item EYEBALL = new Item(new FabricItemSettings()) {
        @Environment(EnvType.CLIENT)
        @Override
        public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
            tooltip.add(Text.literal("Brown like her eyes...").formatted(Formatting.GRAY));
        }
    };

    public static final Item BLOOD_LUST = new BloodlustItem(
            ToolMaterials.DIAMOND,
            5,
            -2.4f,
            new FabricItemSettings().maxCount(1)
    );

    public static void register() {
        register("flesh", FLESH);
        register("lead", LEAD);
        register("sugar_cookie", SUGAR_COOKIE);
        register("bloodlust", BLOOD_LUST);
        register("eyeball", EYEBALL);
        //register("blood_syringe", BLOOD_SYRINGE);
        //register("empty_syringe", EMPTY_SYRINGE);
    }//wip :)

    private static void register(String name, Item item) {
        Identifier id = new Identifier(Sigil.MOD_ID, name);
        Registry.register(Registries.ITEM, id, item);
    }
}
