package me.samsuik.cannonlib.block.interaction;

import me.samsuik.cannonlib.entity.Entity;
import me.samsuik.cannonlib.entity.EntityDataKeys;
import me.samsuik.cannonlib.physics.vec3.Vec3d;

public final class Cobweb implements Interaction {
    @Override
    public void touched(final Entity entity) {
        entity.putData(EntityDataKeys.STUCK_SPEED, new  Vec3d(0.25f, 0.05f, 0.25f));
    }
}
