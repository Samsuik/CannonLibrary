package me.samsuik.cannonlib.explosion;

import me.samsuik.cannonlib.World;
import me.samsuik.cannonlib.block.Block;
import me.samsuik.cannonlib.physics.shape.AABB;
import me.samsuik.cannonlib.physics.shape.Shapes;
import me.samsuik.cannonlib.physics.vec3.Vec3d;
import me.samsuik.cannonlib.physics.vec3.Vec3i;

import java.util.HashMap;
import java.util.Map;

import static me.samsuik.cannonlib.physics.MathUtil.frac;

public final class Obstruction {
    private final Map<ObstructionCacheKey, ObstructionCacheData> densityDataMap = new HashMap<>();

    public float getDensity(final Vec3d position, final Vec3d explosionPosition, final World world) {
        final AABB entityBB = Shapes.entityBoundingBox(position, 0.98f);
        final ObstructionCacheKey key = ObstructionCacheKey.fromExplosionPosition(position, explosionPosition);
        final ObstructionCacheData data = this.densityDataMap.get(key);

        if (data != null && data.hasPosition(explosionPosition, entityBB)) {
            return data.density();
        }

        final boolean knownSource = data != null && data.complete() && data.isExplosionPosition(explosionPosition);
        final float obstruction = getSeenFraction(explosionPosition, entityBB, world, knownSource ? data : null);

        if (data == null || !data.complete()) {
            this.densityDataMap.put(key, new ObstructionCacheData(position, explosionPosition, entityBB, obstruction));
        } else if (data.density() == obstruction) {
            data.expand(position, explosionPosition, entityBB);
        }

        return obstruction;
    }

    public void invalidate() {
        this.densityDataMap.clear();
    }

    /**
     * Copied from Moonrise
     *
     * @author Spottedleaf
     */
    private float getSeenFraction(
            final Vec3d source,
            final AABB entityBB,
            final World world,
            final ObstructionCacheData data
    ) {
        final double diffX = entityBB.maxX - entityBB.minX;
        final double diffY = entityBB.maxY - entityBB.minY;
        final double diffZ = entityBB.maxZ - entityBB.minZ;

        final double incX = 1.0 / (diffX * 2.0 + 1.0);
        final double incY = 1.0 / (diffY * 2.0 + 1.0);
        final double incZ = 1.0 / (diffZ * 2.0 + 1.0);

        if (incX < 0.0 || incY < 0.0 || incZ < 0.0) {
            return 0.0f;
        }

        final double offX = (1.0 - Math.floor(1.0 / incX) * incX) * 0.5 + entityBB.minX;
        final double offY = entityBB.minY;
        final double offZ = (1.0 - Math.floor(1.0 / incZ) * incZ) * 0.5 + entityBB.minZ;

        int totalRays = 0;
        int missedRays = 0;

        for (double dx = 0.0; dx <= 1.0; dx += incX) {
            final double fromX = Math.fma(dx, diffX, offX);
            for (double dy = 0.0; dy <= 1.0; dy += incY) {
                final double fromY = Math.fma(dy, diffY, offY);
                for (double dz = 0.0; dz <= 1.0; dz += incZ) {
                    ++totalRays;

                    final Vec3d from = new Vec3d(
                            fromX,
                            fromY,
                            Math.fma(dz, diffZ, offZ)
                    );

                    if (data != null && data.isKnownPosition(from)) {
                        missedRays += (int) data.density();
                    } else if (!this.clipsAnything(from, source, world)) {
                        ++missedRays;
                    }
                }
            }
        }

        return (float)missedRays / (float)totalRays;
    }

    /**
     * Copied from Moonrise
     *
     * @author Spottedleaf
     */
    private boolean clipsAnything(final Vec3d from, final Vec3d to, final World world) {
        final double adjX = 1.0e-7 * (from.x() - to.x());
        final double adjY = 1.0e-7 * (from.y() - to.y());
        final double adjZ = 1.0e-7 * (from.z() - to.z());

        if (adjX == 0.0 && adjY == 0.0 && adjZ == 0.0) {
            return false;
        }

        final double toXAdj = to.x() - adjX;
        final double toYAdj = to.y() - adjY;
        final double toZAdj = to.z() - adjZ;
        final double fromXAdj = from.x() + adjX;
        final double fromYAdj = from.y() + adjY;
        final double fromZAdj = from.z() + adjZ;

        int currX = (int) Math.floor(fromXAdj);
        int currY = (int) Math.floor(fromYAdj);
        int currZ = (int) Math.floor(fromZAdj);

        final double diffX = toXAdj - fromXAdj;
        final double diffY = toYAdj - fromYAdj;
        final double diffZ = toZAdj - fromZAdj;

        final double dxDouble = Math.signum(diffX);
        final double dyDouble = Math.signum(diffY);
        final double dzDouble = Math.signum(diffZ);

        final int dx = (int)dxDouble;
        final int dy = (int)dyDouble;
        final int dz = (int)dzDouble;

        final double normalizedDiffX = diffX == 0.0 ? Double.MAX_VALUE : dxDouble / diffX;
        final double normalizedDiffY = diffY == 0.0 ? Double.MAX_VALUE : dyDouble / diffY;
        final double normalizedDiffZ = diffZ == 0.0 ? Double.MAX_VALUE : dzDouble / diffZ;

        double normalizedCurrX = normalizedDiffX * (diffX > 0.0 ? (1.0 - frac(fromXAdj)) : frac(fromXAdj));
        double normalizedCurrY = normalizedDiffY * (diffY > 0.0 ? (1.0 - frac(fromYAdj)) : frac(fromYAdj));
        double normalizedCurrZ = normalizedDiffZ * (diffZ > 0.0 ? (1.0 - frac(fromZAdj)) : frac(fromZAdj));

        for (;;) {
            final Vec3i blockPos = new Vec3i(currX, currY, currZ);
            final Block block = world.getBlockAt(blockPos);

            if (block != null && block.shape().clipBlock(from, to, blockPos)) {
                return true;
            }

            if (normalizedCurrX > 1.0 && normalizedCurrY > 1.0 && normalizedCurrZ > 1.0) {
                return false;
            }

            // inc the smallest normalized coordinate

            if (normalizedCurrX < normalizedCurrY) {
                if (normalizedCurrX < normalizedCurrZ) {
                    currX += dx;
                    normalizedCurrX += normalizedDiffX;
                } else {
                    // x < y && x >= z <--> z < y && z <= x
                    currZ += dz;
                    normalizedCurrZ += normalizedDiffZ;
                }
            } else if (normalizedCurrY < normalizedCurrZ) {
                // y <= x && y < z
                currY += dy;
                normalizedCurrY += normalizedDiffY;
            } else {
                // y <= x && z <= y <--> z <= y && z <= x
                currZ += dz;
                normalizedCurrZ += normalizedDiffZ;
            }
        }
    }
}
