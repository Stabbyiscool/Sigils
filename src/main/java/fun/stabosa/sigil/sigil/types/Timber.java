package fun.stabosa.sigil.sigil.types;

import fun.stabosa.sigil.sigil.logic.consume;
import net.minecraft.block.BlockState;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.particle.DustParticleEffect;
import org.joml.Vector3f;
import net.minecraft.item.Item;

import java.util.*;
import java.util.function.BiConsumer;

public class Timber implements SigilType {

    @Override
    public BiConsumer<ServerWorld, BlockPos> create(Map<Item, Integer> inputItems, Map<String, Object> config) {
        int range = config.getOrDefault("range", 5) instanceof Number n ? ((Number) n).intValue() : 5;

        return (world, pos) -> {
            if (consume.run(world, pos, inputItems, false) <= 0) return;

            BlockPos nearestLog = null;
            double nearestDist = Double.MAX_VALUE;

            for (BlockPos target : BlockPos.iterateOutwards(pos, range, range, range)) {
                BlockState state = world.getBlockState(target);
                if (state.isIn(BlockTags.LOGS)) {
                    double dist = pos.getSquaredDistance(target);
                    if (dist < nearestDist) {
                        nearestDist = dist;
                        nearestLog = target.toImmutable();
                    }
                }
            }

            if (nearestLog == null) return;

            Set<BlockPos> visited = new HashSet<>();
            Deque<BlockPos> stack = new ArrayDeque<>();
            stack.push(nearestLog);

            while (!stack.isEmpty()) {
                BlockPos current = stack.pop();
                if (!visited.add(current)) continue;

                BlockState state = world.getBlockState(current);
                if (state.isIn(BlockTags.LOGS)) {
                    world.breakBlock(current, true);
                    for (Direction dir : Direction.values()) {
                        BlockPos neighbor = current.offset(dir);
                        if (!visited.contains(neighbor) && world.getBlockState(neighbor).isIn(BlockTags.LOGS)) {
                            stack.push(neighbor);
                        }
                    }
                }
            }

            world.spawnParticles(
                    new DustParticleEffect(new Vector3f(0.87f, 0.87f, 0.85f), 1.0f),
                    pos.getX() + 0.5,
                    pos.getY(),
                    pos.getZ() + 0.5,
                    5,
                    0.4,
                    0.0,
                    0.4,
                    0.0
            );
        };
    }
}
