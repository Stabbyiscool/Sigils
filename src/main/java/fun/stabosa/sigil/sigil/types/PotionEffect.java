package fun.stabosa.sigil.sigil.types;

import fun.stabosa.sigil.sigil.logic.consume;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.Map;
import java.util.function.BiConsumer;

public class PotionEffect implements SigilType {

    @Override
    public BiConsumer<ServerWorld, BlockPos> create(Map<Item, Integer> inputItems, Map<String, Object> json) {
        String effectId = json.get("effect").toString();
        int duration = ((Number) json.getOrDefault("duration", 200)).intValue();
        int amplifier = ((Number) json.getOrDefault("amplifier", 0)).intValue();
        double range = ((Number) json.getOrDefault("range", 8)).doubleValue();

        StatusEffect effect = Registries.STATUS_EFFECT.get(new Identifier(effectId));

        return (world, pos) -> {
            int sets = consume.run(world, pos, inputItems, true);
            if (sets <= 0) return;

            PlayerEntity closest = world.getClosestPlayer(
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    range,
                    false
            );

            if (closest == null) return;

            closest.addStatusEffect(new StatusEffectInstance(effect, duration * sets, amplifier));

            world.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE,
                    SoundCategory.BLOCKS, 1.0f, 0.8f);
        };
    }
}
