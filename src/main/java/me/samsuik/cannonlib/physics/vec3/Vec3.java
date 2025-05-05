package me.samsuik.cannonlib.physics.vec3;

import me.samsuik.cannonlib.physics.Rotation;

public interface Vec3<T extends Vec3<?>> {
    T add(final T vec);

    T sub(final T vec);

    T mul(final T vec);

    T div(final T vec);

    T div(final double by);

    T scale(final double by);

    T min(final T vec);

    T max(final T vec);

    double getX();

    double getY();

    double getZ();

    default Vec3d floor() {
        return new Vec3d(
                Math.floor(this.getX()),
                Math.floor(this.getY()),
                Math.floor(this.getZ())
        );
    }

    default Vec3d ceil() {
        return new Vec3d(
                Math.ceil(this.getX()),
                Math.ceil(this.getY()),
                Math.ceil(this.getZ())
        );
    }

    default Vec3d abs() {
        return new Vec3d(
                Math.abs(this.getX()),
                Math.abs(this.getY()),
                Math.abs(this.getZ())
        );
    }

    default Vec3d center() {
        return this.floor().add(0.5, 0.0, 0.5);
    }

    default Vec3d center(final Rotation rotation) {
        return switch (rotation.getAxis()) {
            case X -> new Vec3d(Math.floor(this.getX()) + 0.5, this.getY(), this.getZ());
            case Y -> new Vec3d(this.getX(), Math.floor(this.getY()), this.getZ());
            case Z -> new Vec3d(this.getX(), this.getY(), Math.floor(this.getZ()) + 0.5);
        };
    }

    default T invert() {
        return this.scale(-1.0);
    }

    default T normalise() {
        return this.div(this.magnitude());
    }

    default double distanceTo(final Vec3<?> vec) {
        final double deltaX = this.getX() - vec.getX();
        final double deltaY = this.getY() - vec.getY();
        final double deltaZ = this.getZ() - vec.getZ();
        return Math.sqrt(deltaX*deltaX + deltaY*deltaY + deltaZ*deltaZ);
    }

    default double magnitude() {
        return Math.sqrt(this.magnitudeSquared());
    }

    default double magnitudeSquared() {
        final double sqrX = Math.pow(this.getX(), 2.0);
        final double sqrY = Math.pow(this.getY(), 2.0);
        final double sqrZ = Math.pow(this.getZ(), 2.0);
        return sqrX + sqrY + sqrZ;
    }

    default double addAll() { // what is the correct term here?
        return this.getX() + this.getY() + this.getZ();
    }

    default Vec3d fraction() {
        return this.toVec3d().sub(this.floor());
    }

    default Vec3d toVec3d() {
        return new Vec3d(this.getX(), this.getY(), this.getZ());
    }

    default Vec3i toVec3i() {
        int floorX = (int) Math.floor(this.getX());
        int floorY = (int) Math.floor(this.getY());
        int floorZ = (int) Math.floor(this.getZ());
        return new Vec3i(floorX, floorY, floorZ);
    }
}
