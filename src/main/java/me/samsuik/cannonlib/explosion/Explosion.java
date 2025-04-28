package me.samsuik.cannonlib.explosion;

import me.samsuik.cannonlib.world.World;
import me.samsuik.cannonlib.block.Block;
import me.samsuik.cannonlib.physics.vec3.Vec3d;
import me.samsuik.cannonlib.entity.Entity;
import me.samsuik.cannonlib.physics.vec3.Vec3i;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public final class Explosion {
    public static Vec3d explosionPosition(final Vec3d position) {
        return position.add(0.0, 0.98f * 0.0625, 0.0);
    }

    private static Set<Vec3i> calculateExplodedPositions(final World world, final Vec3d explosionPosition, final int flags) {
        final Set<Vec3i> blocksToExplode = new HashSet<>();
        final double maxRange = 4.0f * 1.12f + 0.625f;
        final Map<Vec3i, Block> blocks = world.getBlocks();

        for (final Map.Entry<Vec3i, Block> entry : blocks.entrySet()) {
            final Block explodeBlock = entry.getValue();
            if (explodeBlock.blastResistance() > 24.0) {
                continue;
            }

            final Vec3d position = entry.getKey().toVec3d().add(0.5, 0.5, 0.5);
            final Vec3d difference = position.sub(explosionPosition);
            final double magnitude = difference.magnitude();

            if (magnitude > maxRange) {
                continue;
            }

            final Vec3d step = difference.div(magnitude).scale(0.3f);
            final float variation;
            if ((flags & ExplosionFlags.CONSISTENT_RADIUS) != 0) {
                variation = 0.7f;
            } else {
                variation = ThreadLocalRandom.current().nextFloat();
            }

            float strength = 4.0f * (0.7f + variation * 0.7f);
            Vec3d stepPosition = explosionPosition;
            do {
                final Vec3i blockPos = stepPosition.toVec3i();
                final Block block = blocks.get(blockPos);

                if (block != null) {
                    strength -= block.blastResistance();
                    if (strength > 0.0) {
                        blocksToExplode.add(blockPos);
                    }
                }

                strength -= 0.22500001f;
                stepPosition = stepPosition.add(step);
            } while (strength > 0.0f);
        }

        return blocksToExplode;
    }

    public static Set<Vec3i> explode(final Entity entity, final Obstruction obstruction, final int count, final int flags) {
        final World world = entity.getWorld();
        final Vec3d explosionPosition = Explosion.explosionPosition(entity.position);
        final Set<Vec3i> blocksToExplode;

        if ((flags & ExplosionFlags.DESTROY_BLOCKS) != 0) {
            blocksToExplode = Explosion.calculateExplodedPositions(world, explosionPosition, flags);
        } else {
            blocksToExplode = Set.of();
        }

        // Affect entities
        final boolean upToCount = (flags & ExplosionFlags.ENTITIES_UP_TO_COUNT) != 0;
        final int limit = upToCount ? (count * ExplosionFlags.readData(flags, 1)) : Integer.MAX_VALUE;
        final boolean singleImpact = (flags & ExplosionFlags.SINGLE_IMPACT) != 0;
        Vec3d reuseImpact = null;

        int entityIndex = 0;
        for (final Entity otherEntity : world.getEntityList()) {
            if (otherEntity != entity) {
                if (entityIndex++ >= limit) {
                    break;
                }

                final Vec3d position = otherEntity.position;
                final Vec3d impact;
                if (singleImpact && reuseImpact != null) {
                    impact = reuseImpact;
                } else {
                    impact = Explosion.impact(position, explosionPosition, world, obstruction, flags);
                    reuseImpact = impact;
                }
                otherEntity.momentum = otherEntity.momentum.add(impact);
            }
        }

        return blocksToExplode;
    }

    public static Vec3d impact(
            final Vec3d position,
            final Vec3d explosionPosition,
            final World world,
            final Obstruction obstructionCache,
            final int flags
    ) {
        final Vec3d difference = position.sub(explosionPosition);
        final double magnitude = difference.magnitude();

        if (magnitude == 0.0 || magnitude > 8.0) {
            return Vec3d.zero();
        }

        final float blockDensity;
        if ((flags & ExplosionFlags.OBSTRUCTION) != 0) {
            blockDensity = obstructionCache.getDensity(position, explosionPosition, world);
        } else {
            blockDensity = 1.0f;
        }

        // This is required to preserve floating point issues
        final double exposure = (1.0 - magnitude / 8.0) * blockDensity;
        final Vec3d normalised = difference.div(magnitude);
        return normalised.scale(exposure);
    }
}
