// to do
// actually implement the syringe

package fun.stabosa.sigil;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fun.stabosa.sigil.block.ModBlocks;
import fun.stabosa.sigil.block.SigilBlock;
import fun.stabosa.sigil.event.ModEvents;
import fun.stabosa.sigil.item.ModItems;
import fun.stabosa.sigil.sound.ModSounds;
import fun.stabosa.sigil.sigil.SigilReloadController;
public class Sigil implements ModInitializer {
    public static final String MOD_ID = "sigil";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModBlocks.register();
        ModSounds.register();
        ModItems.register();
        ModEvents.register();
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(SigilReloadController.RELOAD_LISTENER);

        Registry.register(
            Registries.ITEM_GROUP,
            new Identifier(MOD_ID, "sigil_tab"),
            FabricItemGroup.builder()
                .displayName(Text.translatable("itemgroup.sigil"))
                .icon(() -> new ItemStack(ModBlocks.SIGIL))
                .entries((displayContext, entries) -> {
                    entries.add(ModItems.BLOOD_LUST);
                    entries.add(ModItems.EYEBALL);
                    entries.add(ModItems.FLESH);
                    entries.add(ModItems.LEAD);
					entries.add(ModItems.SUGAR_COOKIE);
                    entries.add(ModBlocks.SIGIL);
                })
                .build()
        );

        LOGGER.info("sigils inited!");
    }

}