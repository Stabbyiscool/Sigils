package fun.stabosa.sigil.block;

import fun.stabosa.sigil.Sigil;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block SIGIL = new SigilBlock(Block.Settings
        .create()
        .mapColor(MapColor.WHITE)
        .dropsNothing()
        .noCollision()
        .strength(0.3f)
        .nonOpaque()
    );

    public static void register() {
        register("chalk", SIGIL, new SigilBlockItem(SIGIL, new FabricItemSettings()));
    }

    private static void register(String name, Block block, Item blockItem) {
        Identifier id = new Identifier(Sigil.MOD_ID, name);
        Registry.register(Registries.BLOCK, id, block);
        if (blockItem != null) {
            Registry.register(Registries.ITEM, id, blockItem);
        }
    }
}
