package fun.stabosa.sigil;

import fun.stabosa.sigil.block.ModBlocks;
import fun.stabosa.sigil.item.ModItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class SigilClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SIGIL, RenderLayer.getCutout());

        ModelPredicateProviderRegistry.register(
                ModItems.BLOOD_LUST,
                new Identifier("sigil", "cooldown"),
                (stack, world, entity, seed) -> {
                    if (entity instanceof PlayerEntity player) {
                        return player.getItemCooldownManager().isCoolingDown(stack.getItem())
                                ? 1.0f : 0.0f;
                    }
                    return 0.0f;
                }
        );
    }
}
