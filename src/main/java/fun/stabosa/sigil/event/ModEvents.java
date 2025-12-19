package fun.stabosa.sigil.event;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.fabricmc.loader.api.FabricLoader;

public class ModEvents {

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            boolean isjeiloaded = FabricLoader.getInstance().isModLoaded("jei");
            boolean isemiloaded = FabricLoader.getInstance().isModLoaded("emi");

            if (isjeiloaded && !isemiloaded) {
                Text warning = Text.literal("Sigil: JEI is installed! Transmute recipes will not display. ")
                        .formatted(Formatting.RED)
                        .append(
                            Text.literal("Click here to get EMI (basically JEI but better)!")
                                .setStyle(Style.EMPTY.withClickEvent(
                                    new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.curseforge.com/minecraft/mc-mods/emi")
                                ))
                        );

                handler.player.sendMessage(warning, false);
            }
        });
    }
}
