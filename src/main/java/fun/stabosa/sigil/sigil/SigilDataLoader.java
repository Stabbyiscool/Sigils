package fun.stabosa.sigil.sigil;

import com.google.gson.*;
import fun.stabosa.sigil.Sigil;
import fun.stabosa.sigil.sigil.types.SigilType;
import fun.stabosa.sigil.sigil.types.SigilTypeRegistry;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class SigilDataLoader {

    private static Identifier id(String full) {
        if (!full.contains(":")) return new Identifier("minecraft", full);
        return new Identifier(full);
    }

    public static void loadAll(MinecraftServer server, ResourceManager manager) {
        SigilExecutor.clear();

        try {
            Map<Identifier, net.minecraft.resource.Resource> resources =
                    manager.findResources("sigil", p -> p.getPath().endsWith(".json"));

            Sigil.LOGGER.info("[Sigil] Found {} sigil files.", resources.size());

            for (var entry : resources.entrySet()) {
                Identifier id = entry.getKey();

                try (InputStreamReader reader = new InputStreamReader(entry.getValue().getInputStream(), StandardCharsets.UTF_8)) {

                    JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                    if (!root.has("type") || !root.has("items")) {
                        Sigil.LOGGER.warn("[Sigil] Skipping {}, missing fields!", id);
                        continue;
                    }

                    String typeName = root.get("type").getAsString();
                    SigilType type = SigilTypeRegistry.get(typeName);

                    if (type == null) {
                        Sigil.LOGGER.warn("[Sigil] Unknown sigil type {} in {}", typeName, id);
                        continue;
                    }

                    Map<Item, Integer> inputItems = new HashMap<>();
                    for (String key : root.getAsJsonObject("items").keySet()) {
                        inputItems.put(Registries.ITEM.get(id(key)), root.getAsJsonObject("items").get(key).getAsInt());
                    }

                    Map<String, Object> config = new HashMap<>();
                    root.entrySet().forEach(e -> {
                        if (!e.getKey().equals("type") && !e.getKey().equals("items")) {
                            if (e.getValue().isJsonPrimitive()) {
                                JsonPrimitive prim = e.getValue().getAsJsonPrimitive();
                                if (prim.isNumber()) config.put(e.getKey(), prim.getAsNumber());
                                else if (prim.isString()) config.put(e.getKey(), prim.getAsString());
                                else if (prim.isBoolean()) config.put(e.getKey(), prim.getAsBoolean());
                            } else if (e.getValue().isJsonObject()) {
                                Map<Item, Integer> out = new HashMap<>();
                                e.getValue().getAsJsonObject().entrySet().forEach(entry2 -> {
                                    out.put(Registries.ITEM.get(id(entry2.getKey())), entry2.getValue().getAsInt());
                                });
                                config.put(e.getKey(), out);
                            } else if (e.getValue().isJsonArray()) {
                                config.put(e.getKey(), e.getValue().getAsJsonArray());
                            }
                        }
                    });

                    var effect = type.create(inputItems, config);
                    SigilExecutor.SPELL_EFFECTS.add(new SigilExecutor.SpellEntry(inputItems, effect, config, typeName));

                } catch (Exception e) {
                    Sigil.LOGGER.error("[Sigil] Failed to load {}", id, e);
                }
            }

        } catch (Exception e) {
            Sigil.LOGGER.error("[Sigil] Failed to scan sigil files.", e);
        }
    }
}
