package me.samsuik.cannonlib.block.interaction;

import me.samsuik.cannonlib.World;
import me.samsuik.cannonlib.block.Block;
import me.samsuik.cannonlib.physics.vec3.Vec3i;

public interface Interaction {
    int onBlockUpdate(final World world, final Vec3i position, final Block block);

    default void onTick(final World world, final Vec3i position, final Block block) {}
}
