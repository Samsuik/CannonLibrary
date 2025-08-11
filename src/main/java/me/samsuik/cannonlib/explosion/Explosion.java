package me.samsuik.cannonlib.explosion;

import me.samsuik.cannonlib.World;
import me.samsuik.cannonlib.block.Block;
import me.samsuik.cannonlib.physics.vec3.Vec3d;
import me.samsuik.cannonlib.entity.Entity;
import me.samsuik.cannonlib.physics.vec3.Vec3i;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public final class Explosion {
    private static final double EXPLOSION_POSITION_OFFSET = 0.98f * 0.0625;

    public static Vec3d explosionPosition(final Vec3d position) {
        return position.add(0.0, EXPLOSION_POSITION_OFFSET, 0.0);
    }

    private static Set<Vec3i> calculateExplodedPositions(final World world, final Vec3d explosionPosition, final int flags) {
        final Set<Vec3i> blocksToExplode = new HashSet<>();
        final double maxRange = 4.0f * 1.12f + 0.625f;
        final Map<Vec3i, Block> blocks = world.getBlocks(explosionPosition.toVec3i(), (int) maxRange);

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

    public static Set<Vec3i> explode(
            final Entity entity, 
            final World world, 
            final Obstruction obstructionCache, 
            final int count, 
            final int flags
    ) {
        return explode(entity, explosionPosition(entity.position), world, obstructionCache, count, flags);
    }

    public static Set<Vec3i> explode(
            final Entity entity, 
            final Vec3d explosionPosition, 
            final World world, 
            final Obstruction obstructionCache, 
            final int count,
            final int flags
    ) {
        final Set<Vec3i> blocksToExplode;
        if ((flags & ExplosionFlags.DESTROY_BLOCKS) != 0) {
            blocksToExplode = Explosion.calculateExplodedPositions(world, explosionPosition, flags);
        } else {
            blocksToExplode = Set.of();
        }

        final boolean obstruction = (flags & ExplosionFlags.OBSTRUCTION) != 0;
        if ((flags & ExplosionFlags.ENTITIES_UP_TO_COUNT) != 0) {
            impactEntitiesUpToCount(entity, explosionPosition, world, obstructionCache, obstruction, count, flags);
        } else {
            impactEntities(entity, explosionPosition, world, obstructionCache, obstruction);
        }

        return blocksToExplode;
    }

    private static void impactEntities(
            final Entity entity,
            final Vec3d explosionPosition,
            final World world,
            final Obstruction obstructionCache,
            final boolean obstruction
    ) {
        for (final Entity otherEntity : world.getEntityList()) {
            if (otherEntity != entity) {
                final Vec3d impact = Explosion.impact(
                    otherEntity.position, explosionPosition, world, obstructionCache, obstruction
                );
                otherEntity.momentum = otherEntity.momentum.add(impact);
            }
        }
    }

    private static void impactEntitiesUpToCount(
            final Entity entity, 
            final Vec3d explosionPosition, 
            final World world, 
            final Obstruction obstructionCache, 
            final boolean obstruction,
            final int count,
            final int flags
    ) {
        final boolean singleImpact = (flags & ExplosionFlags.SINGLE_IMPACT) != 0;
        final int limit = count * ExplosionFlags.readData(flags, 1);

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
                    impact = Explosion.impact(position, explosionPosition, world, obstructionCache, obstruction);
                    if (singleImpact && impact.magnitudeSquared() > 0.0) {
                        reuseImpact = impact;
                    }
                }
                otherEntity.momentum = otherEntity.momentum.add(impact);
            }
        }
    }

    public static Vec3d impact(final Vec3d position, final Vec3d explosionPosition) {
        return impact(position, explosionPosition, null, null, false);
    }

    public static Vec3d impact(
            final Vec3d position,
            final Vec3d explosionPosition,
            final World world,
            final Obstruction obstructionCache,
            final boolean obstruction
    ) {
        final Vec3d difference = position.sub(explosionPosition);
        final double magnitude = difference.magnitude();

        if (magnitude == 0.0 || magnitude > 8.0) {
            return Vec3d.zero();
        }

        final float blockDensity;
        if (obstruction) {
            blockDensity = obstructionCache.getDensity(position, explosionPosition, world);
        } else {
            blockDensity = 1.0f;
        }

        final double exposure = (1.0 - magnitude / 8.0) * blockDensity;
        final Vec3d normalised = difference.div(magnitude);
        return normalised.scale(exposure);
    }
}
