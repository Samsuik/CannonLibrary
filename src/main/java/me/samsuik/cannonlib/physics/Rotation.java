package me.samsuik.cannonlib.physics;

import me.samsuik.cannonlib.physics.vec3.Vec3d;

public enum Rotation {
    NORTH(0, RotationAxis.Z),
    WEST(90, RotationAxis.X),
    SOUTH(180, RotationAxis.Z),
    EAST(270, RotationAxis.X),
    UP(0, RotationAxis.Y),
    DOWN(180, RotationAxis.Y);

    private final int degrees;
    private final RotationAxis axis;

    Rotation(final int degrees, final RotationAxis axis) {
        this.degrees = degrees;
        this.axis = axis;
    }

    public int getDegrees() {
        return this.degrees;
    }

    public RotationAxis getAxis() {
        return this.axis;
    }

    public Rotation getOpposite() {
        return switch (this) {
            case NORTH -> SOUTH;
            case SOUTH -> NORTH;
            case WEST  -> EAST;
            case EAST  -> WEST;
            case DOWN  -> UP;
            case UP    -> DOWN;
        };
    }

    public Vec3d getDirection() {
        return switch (this) {
            case NORTH -> new Vec3d(0.0, 0.0, -1.0);
            case WEST  -> new Vec3d(-1.0, 0.0, 0.0);
            case SOUTH -> new Vec3d(0.0, 0.0, 1.0);
            case EAST  -> new Vec3d(1.0, 0.0, 0.0);
            case DOWN  -> new Vec3d(0.0, -1.0, 0.0);
            case UP    -> new Vec3d(0.0, 1.0, 0.0);
        };
    }

    public enum RotationAxis {
        X, Y, Z;

        public final boolean isX() {
            return this == X;
        }

        public final boolean isY() {
            return this == Y;
        }

        public final boolean isZ() {
            return this == Z;
        }

        public final boolean isHorizontal() {
            return this != Y;
        }
    }
}
