package fun.stabosa.sigil.sigil;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class SigilExecutor {

    public static final List<SpellEntry> SPELL_EFFECTS = new ArrayList<>();

    public static void clear() {
        SPELL_EFFECTS.clear();
    }
    public static record SpellEntry(
            Map<Item, Integer> inputs,
            BiConsumer<ServerWorld, BlockPos> effect,
            Map<String, Object> config,
            String type
    ) {}
}
