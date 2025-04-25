package me.samsuik.cannonlib.physics.shape;

import me.samsuik.cannonlib.physics.Rotation;
import me.samsuik.cannonlib.physics.vec3.Vec3;
import me.samsuik.cannonlib.physics.vec3.Vec3d;
import me.samsuik.cannonlib.physics.vec3.Vec3i;

import java.util.List;

public interface Shape {
    Vec3d getSize();

    Vec3d getCenter();

    List<Vec3d> corners();

    Shape move(final Vec3<?> vec3);

    Shape rotateXZ(final int degrees);

    Shape flip(final Rotation.RotationAxis axis);

    boolean clip(final Vec3d from, final Vec3d to);

    boolean contains(final Vec3<?> vec3);

    double collideX(final AABB entityBB, final double movement);

    double collideY(final AABB entityBB, final double movement);

    double collideZ(final AABB entityBB, final double movement);

    default Vec3d minimum() {
        return this.corners().getFirst();
    }

    default Vec3d maximum() {
        return this.corners().getLast();
    }

    default AABB encompassingBoundingBox() {
        if (this instanceof AABB bb) {
            return bb;
        }
        return new AABB(this.minimum(), this.maximum());
    }

    default boolean clipBlock(final Vec3d from, final Vec3d to, final Vec3i blockPos) {
        if (this == Shapes.EMPTY_SHAPE) {
            return false;
        }

        final Vec3d diff = to.sub(from);
        if (diff.magnitudeSquared() < 1.0e-7) {
            return false;
        }

        final Vec3d behind = from.add(diff.scale(0.001));
        final Vec3d behindOffset = behind.sub(blockPos.toVec3d());
        return this.contains(behindOffset) || this.move(blockPos).clip(from, to);
    }
}
