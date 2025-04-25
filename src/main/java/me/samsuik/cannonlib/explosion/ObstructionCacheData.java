package me.samsuik.cannonlib.explosion;

import me.samsuik.cannonlib.physics.shape.AABB;
import me.samsuik.cannonlib.physics.vec3.Vec3d;

public final class ObstructionCacheData {
    private AABB source;
    private AABB known;
    private AABB entity;
    private final float density;
    private final boolean complete;

    public ObstructionCacheData(
            final Vec3d entityPosition,
            final Vec3d explosionPosition,
            final AABB entityBB,
            final float density
    ) {
        this.source = new AABB(explosionPosition, explosionPosition);
        this.known = new AABB(entityPosition, entityPosition);
        this.entity = entityBB;
        this.density = density;
        this.complete = Math.abs(density - 0.5f) == 0.5f;
    }

    public float density() {
        return this.density;
    }

    public boolean complete() {
        return this.complete;
    }

    public boolean hasPosition(final Vec3d explosion, final AABB entity) {
        return this.isExplosionPosition(explosion) && this.entity.containsBBInclusive(entity);
    }

    public boolean isKnownPosition(final Vec3d point) {
        return this.entity.containsVecInclusive(point);
    }

    public boolean isExplosionPosition(final Vec3d explosion) {
        return this.source.containsVecInclusive(explosion);
    }

    public void expand(final Vec3d entityPosition, final Vec3d explosionPosition, final AABB entityBB) {
        this.source = this.source.expandTo(explosionPosition);
        this.known  = this.known.expandTo(entityPosition);
        this.entity = this.entity.expandTo(entityBB);
    }
}
