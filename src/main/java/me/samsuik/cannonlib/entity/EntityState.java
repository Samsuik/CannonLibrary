package me.samsuik.cannonlib.entity;

import me.samsuik.cannonlib.physics.vec3.Vec3d;

public record EntityState(Vec3d position, Vec3d movement) {
    public static EntityState none() {
        return new EntityState(Vec3d.zero(), Vec3d.zero());
    }

    public void apply(final Entity entity) {
        entity.position = this.position;
        entity.momentum = this.movement;
    }
}
