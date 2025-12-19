package fun.stabosa.sigil.sound;

import fun.stabosa.sigil.Sigil;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static final Identifier SIGIL_WRITE_ID = id("block.chalk.write");
    public static final SoundEvent SIGIL_WRITE = SoundEvent.of(SIGIL_WRITE_ID);

    public static final Identifier BLOODY_SLASH_ID = id("item.bloodlust.slash");
    public static final SoundEvent BLOODY_SLASH = SoundEvent.of(BLOODY_SLASH_ID);

    public static final BlockSoundGroup SIGIL_WRITES = new BlockSoundGroup(
        1.0f, 1.0f,
        SoundEvents.BLOCK_CALCITE_PLACE,
        SIGIL_WRITE,
        SoundEvents.BLOCK_CALCITE_PLACE,
        SIGIL_WRITE,
        SoundEvents.BLOCK_CALCITE_PLACE
    );

    public static void register() {
        register(SIGIL_WRITE_ID, SIGIL_WRITE);
        register(BLOODY_SLASH_ID, BLOODY_SLASH);
    }

    private static void register(Identifier id, SoundEvent event) {
        Registry.register(Registries.SOUND_EVENT, id, event);
    }

    private static Identifier id(String name) {
        return new Identifier(Sigil.MOD_ID, name);
    }
}
