package me.samsuik.cannonlib.block.interaction;

import me.samsuik.cannonlib.World;
import me.samsuik.cannonlib.block.Block;
import me.samsuik.cannonlib.block.Blocks;
import me.samsuik.cannonlib.physics.Rotation;
import me.samsuik.cannonlib.physics.vec3.Vec3i;

public final class Lava implements Interaction {
    @Override
    public int onBlockUpdate(final World world, final Vec3i position, final Block block) {
        return 5;
    }

    @Override
    public void onTick(final World world, final Vec3i position, final Block block) {
        if (block == Blocks.WATER) {
            boolean drain = true;
            for (final Rotation rotation : Rotation.values()) {
                if (rotation == Rotation.DOWN) {
                    continue;
                }

                final Vec3i adjacentPosition = position.move(rotation);
                final Block adjacentBlock = world.getBlockAt(adjacentPosition);

                if (isWaterOrSource(adjacentBlock)) {
                    drain = false;
                }
            }

            if (drain) {
                world.setBlock(position, Blocks.AIR);
                return;
            }
        }

        final Vec3i blockPosBelow = position.down();
        final Block blockBelow = world.getBlockAt(blockPosBelow);
        
        if (blockBelow == null || blockBelow.replace() && !isWaterOrSource(blockBelow)) {
            world.setBlock(blockPosBelow, blockBelow == Blocks.LAVA ? Blocks.STONE : Blocks.WATER);
        } else if (block == Blocks.WATER_SOURCE || blockBelow.replace() && !isWaterOrSource(blockBelow)) {
            for (final Rotation rotation : Rotation.values()) {
                if (rotation.getAxis().isY()) {
                    continue;
                }

                final Vec3i adjacentPosition = position.move(rotation);
                final Block adjacentBlock = world.getBlockAt(adjacentPosition);
                if (adjacentBlock == null || adjacentBlock.replace() && !isWaterOrSource(adjacentBlock)) {
                    world.setBlock(adjacentPosition, Blocks.WATER);
                }
            }
        }
    }

    private static boolean isWaterOrSource(final Block block) {
        return block == Blocks.WATER || block == Blocks.WATER_SOURCE;
    }
}
