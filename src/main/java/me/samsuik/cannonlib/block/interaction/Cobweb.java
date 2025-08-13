package me.samsuik.cannonlib.block.interaction;

import me.samsuik.cannonlib.entity.Entity;
import me.samsuik.cannonlib.entity.EntityDataKeys;
import me.samsuik.cannonlib.physics.vec3.Vec3d;

public final class Cobweb implements Interaction {
    @Override
    public void touched(final Entity entity) {
        entity.putData(EntityDataKeys.STUCK_SPEED, Vec3d.xyz(0.25f));
    }
}
