package fun.stabosa.sigil.sigil.types;

import fun.stabosa.sigil.sigil.logic.consume;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;

import java.util.Map;
import java.util.function.BiConsumer;

public class Command implements SigilType {

    @Override
    public BiConsumer<ServerWorld, BlockPos> create(Map<Item, Integer> inputItems, Map<String, Object> data) {
        String rawcommand = "";
        Object cmdObj = data.get("command");
        
        if (cmdObj instanceof String s) {
            rawcommand = s;
        }

        final String commandTd = rawcommand;

        return (world, pos) -> {
            int sets = consume.run(world, pos, inputItems, true);
            if (sets <= 0 || commandTd.isEmpty()) return;

            MinecraftServer server = world.getServer();
            if (server == null) return;

            ServerCommandSource source = new ServerCommandSource(
                    server,
                    pos.toCenterPos(), 
                    Vec2f.ZERO,
                    world,
                    4,
                    "Sigil",
                    Text.literal("Sigil"),
                    server,
                    null
            );

            for (int i = 0; i < sets; i++) {
                server.getCommandManager().executeWithPrefix(source, commandTd);
            }
        };
    }
}