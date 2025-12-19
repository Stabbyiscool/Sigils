package fun.stabosa.sigil.sigil;

import fun.stabosa.sigil.Sigil;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class SigilReloadController {

    public static final SimpleSynchronousResourceReloadListener RELOAD_LISTENER =
            new SimpleSynchronousResourceReloadListener() {

                @Override
                public Identifier getFabricId() {
                    return new Identifier(Sigil.MOD_ID, "sigil_reloader");
                }

                @Override
                public void reload(ResourceManager manager) {
                    Sigil.LOGGER.info("[Sigil] Reloading sigil files...");
                    SigilDataLoader.loadAll(null, manager);
                }
            };

    static {
        ServerLifecycleEvents.SERVER_STARTING.register(server ->
                SigilDataLoader.loadAll(server, server.getResourceManager())
        );
    }
}
