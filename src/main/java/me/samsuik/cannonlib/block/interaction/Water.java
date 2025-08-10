package me.samsuik.cannonlib.block.interaction;

import me.samsuik.cannonlib.World;
import me.samsuik.cannonlib.block.Block;
import me.samsuik.cannonlib.block.Blocks;
import me.samsuik.cannonlib.physics.Rotation;
import me.samsuik.cannonlib.physics.vec3.Vec3i;

public final class Water implements FluidInteraction {
    @Override
    public int onBlockUpdate(final World world, final Vec3i position, final Block block) {
        return 5;
    }

    @Override
    public void onTick(final World world, final Vec3i position, final Block block) {
        if (block == Blocks.WATER && this.drain(world, position)) {
            return;
        }

        final Vec3i blockPosBelow = position.down();
        final Block blockBelow = world.getBlockAt(blockPosBelow);
        
        if (blockBelow == null || blockBelow.replace() && !blockBelow.has(this)) {
            world.setBlock(blockPosBelow, Blocks.WATER.interaction(this));
        } else if (block == Blocks.WATER_SOURCE || !blockBelow.replace()) {
            for (final Rotation rotation : Rotation.values()) {
                if (rotation.getAxis().isY()) {
                    continue;
                }

                final Vec3i adjacentPosition = position.move(rotation);
                final Block adjacentBlock = world.getBlockAt(adjacentPosition);
                if (adjacentBlock == null || adjacentBlock.replace() && !(blockBelow.interaction() instanceof FluidInteraction)) {
                    world.setBlock(adjacentPosition, Blocks.WATER.interaction(this));
                }
            }
        }
    }
}
