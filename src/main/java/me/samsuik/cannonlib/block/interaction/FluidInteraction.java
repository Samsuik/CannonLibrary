package me.samsuik.cannonlib.block.interaction;

import me.samsuik.cannonlib.World;
import me.samsuik.cannonlib.block.Block;
import me.samsuik.cannonlib.block.Blocks;
import me.samsuik.cannonlib.physics.Rotation;
import me.samsuik.cannonlib.physics.vec3.Vec3i;

public interface FluidInteraction extends Interaction {
    public default boolean drain(final World world, final Vec3i position) {
        for (final Rotation rotation : Rotation.values()) {
            if (rotation == Rotation.DOWN) {
                continue;
            }

            final Vec3i adjacentPosition = position.move(rotation);
            final Block adjacentBlock = world.getBlockAt(adjacentPosition);

            if (adjacentBlock.has(this)) {
                return false;
            }
        }

        world.setBlock(position, Blocks.AIR);
        return true;
    }
}
