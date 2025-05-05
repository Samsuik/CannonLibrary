package me.samsuik.cannonlib.entity.helpers;

import me.samsuik.cannonlib.block.Block;
import me.samsuik.cannonlib.block.Blocks;
import me.samsuik.cannonlib.physics.Rotation;
import me.samsuik.cannonlib.physics.shape.AABB;
import me.samsuik.cannonlib.physics.shape.BlockShape;
import me.samsuik.cannonlib.physics.shape.Shapes;
import me.samsuik.cannonlib.physics.vec3.Vec3d;
import me.samsuik.cannonlib.physics.vec3.Vec3i;

public final class Alignment {
    public static double guiderY(final Vec3i blockPos, final Block block) {
        return guiderY(blockPos, block, 0.98f);
    }

    public static double guiderY(final Vec3i blockPos, final Block block, final float entitySize) {
        final Vec3d belowGuider = blockPos.down().toVec3d().center();
        return align(Rotation.UP, belowGuider, block, entitySize);
    }

    public static double align(final Rotation rotation, final Vec3d position) {
        return align(rotation, position, Blocks.BEDROCK, 0.98f);
    }

    public static double align(final Rotation rotation, final Vec3d position, final Block block) {
        return align(rotation, position, block, 0.98f);
    }

    public static double align(
            final Rotation rotation,
            final Vec3d position,
            final Block block,
            final float entitySize
    ) {
        final Vec3d alignedPosition = alignPosition(rotation, position, block, entitySize);
        return alignedPosition.mul(rotation.getDirection().abs()).addAll();
    }

    public static Vec3d alignPosition(final Rotation rotation, final Vec3d position) {
        return alignPosition(rotation, position, Blocks.BEDROCK, 0.98f);
    }

    public static Vec3d alignPosition(final Rotation rotation, final Vec3d position, final Block block) {
        return alignPosition(rotation, position, block, 0.98f);
    }

    public static Vec3d alignPosition(
            final Rotation rotation,
            final Vec3d position,
            final Block block,
            final float entitySize
    ) {
        final Vec3d direction = rotation.getDirection();
        final Vec3d centerPosition = position.center(rotation);

        final AABB entityBB = Shapes.entityBoundingBox(centerPosition, entitySize);
        final Vec3i alignTo = centerPosition.add(direction).toVec3i();
        final BlockShape shape = block.shape().move(alignTo);

        final double movement = direction.scale(1.0).addAll();
        double moveX = 0.0;
        double moveY = 0.0;
        double moveZ = 0.0;

        switch (rotation.getAxis()) {
            case X -> moveX = shape.collideX(entityBB, movement);
            case Y -> moveY = shape.collideY(entityBB, movement);
            case Z -> moveZ = shape.collideZ(entityBB, movement);
        }

        if (moveX + moveY + moveZ != movement) {
            return centerPosition.add(moveX, moveY, moveZ);
        } else {
            return position;
        }
    }
}
