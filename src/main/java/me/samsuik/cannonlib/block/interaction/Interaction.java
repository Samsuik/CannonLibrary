package me.samsuik.cannonlib.block.interaction;

import me.samsuik.cannonlib.World;
import me.samsuik.cannonlib.block.Block;
import me.samsuik.cannonlib.entity.Entity;
import me.samsuik.cannonlib.physics.shape.Shape;
import me.samsuik.cannonlib.physics.vec3.Vec3i;

public interface Interaction {
    default int onBlockUpdate(final World world, final Vec3i position, final Block block) {
        return 0;
    }

    default void onTick(final World world, final Vec3i position, final Block block) {}

    default void touched(final Entity entity) {}

    default Shape collisionShape(final Shape shape, final Entity entity) {
        return shape;
    }
}
