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
    private static boolean moreVariation = false;
    private static boolean consistent = true;

    public static void setMoreVariation(final boolean moreVariation) {
        Explosion.moreVariation = moreVariation;
    }

    public static void setConsistent(final boolean consistent) {
        Explosion.consistent = consistent;
    }

    private static float getVariation(final int flags) {
        if (consistent || (flags & ExplosionFlags.CONSISTENT_RADIUS) != 0) {
            return 0.7f;
        }

        final float variation = ThreadLocalRandom.current().nextFloat();
        if (moreVariation && variation > 0.33f) {
            return ThreadLocalRandom.current().nextBoolean() ? 1.0f : 0.0f;
        }

        return variation;
    }

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

            final double posX = entry.getKey().x() + 0.5;
            final double posY = entry.getKey().y() + 0.5;
            final double posZ = entry.getKey().z() + 0.5;
            final double diffX = posX - explosionPosition.x();
            final double diffY = posY - explosionPosition.y();
            final double diffZ = posZ - explosionPosition.z();

            final double distance = Math.sqrt(diffX*diffX + diffY*diffY + diffZ*diffZ);
            if (distance > maxRange) {
                continue;
            }

            final double stepX = (diffX / distance) * 0.3f;
            final double stepY = (diffY / distance) * 0.3f;
            final double stepZ = (diffZ / distance) * 0.3f;
            final float variation = getVariation(flags);

            float strength = 4.0f * (0.7f + variation * 0.7f);
            double stepPosX = explosionPosition.x();
            double stepPosY = explosionPosition.y();
            double stepPosZ = explosionPosition.z();

            do {
                final Vec3i blockPos = Vec3i.from(stepPosX, stepPosY, stepPosZ);
                final Block block = blocks.get(blockPos);

                if (block != null) {
                    strength -= block.blastResistance();
                    if (strength > 0.0) {
                        blocksToExplode.add(blockPos);
                    }
                }

                strength -= 0.22500001f;
                stepPosX += stepX;
                stepPosY += stepY;
                stepPosZ += stepZ;
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
        final double diffX = position.x() - explosionPosition.x();
        final double diffY = position.y() - explosionPosition.y();
        final double diffZ = position.z() - explosionPosition.z();
        final double distance = Math.sqrt(diffX*diffX + diffY*diffY + diffZ*diffZ);

        if (distance == 0.0 || distance > 8.0) {
            return Vec3d.zero();
        }

        final float blockDensity;
        if (obstruction) {
            blockDensity = obstructionCache.getDensity(position, explosionPosition, world);
        } else {
            blockDensity = 1.0f;
        }

        final double exposure = (1.0 - distance / 8.0) * blockDensity;
        final double impactX = (diffX / distance) * exposure;
        final double impactY = (diffY / distance) * exposure;
        final double impactZ = (diffZ / distance) * exposure;

        return new Vec3d(impactX, impactY, impactZ);
    }
}
