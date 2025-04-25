package me.samsuik.cannonlib.explosion;

import me.samsuik.cannonlib.physics.vec3.Vec3d;
import me.samsuik.cannonlib.physics.vec3.Vec3i;

public record ObstructionCacheKey(Vec3i entityBlockPos, Vec3i explosionBlockPos) {
    public static ObstructionCacheKey fromExplosionPosition(final Vec3d position, final Vec3d explosionPos) {
        return new ObstructionCacheKey(position.toVec3i(), explosionPos.toVec3i());
    }
}
