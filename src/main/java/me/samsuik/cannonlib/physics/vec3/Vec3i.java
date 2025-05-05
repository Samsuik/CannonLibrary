package me.samsuik.cannonlib.physics.vec3;

import me.samsuik.cannonlib.physics.Rotation;

public record Vec3i(int x, int y, int z) implements Vec3<Vec3i> {
    public static Vec3i zero() {
        return new Vec3i(0, 0, 0);
    }

    public static Vec3i xyz(final int n) {
        return new Vec3i(n, n, n);
    }

    public Vec3i setX(final int newX) {
        return new Vec3i(newX, this.y, this.z);
    }

    public Vec3i setY(final int newY) {
        return new Vec3i(this.x, newY, this.z);
    }

    public Vec3i setZ(final int newZ) {
        return new Vec3i(this.x, this.y, newZ);
    }

    @Override
    public Vec3i add(final Vec3i vec) {
        return this.add(vec.x, vec.y, vec.z);
    }

    public Vec3i add(final int x, final int y, final int z) {
        return new Vec3i(this.x + x, this.y + y, this.z + z);
    }

    @Override
    public Vec3i sub(final Vec3i vec) {
        return this.sub(vec.x, vec.y, vec.z);
    }

    public Vec3i sub(final int x, final int y, final int z) {
        return this.add(-x, -y, -z);
    }

    @Override
    public Vec3i mul(Vec3i vec) {
        return this.mul(vec.x, vec.y, vec.z);
    }

    public Vec3i mul(final int x, final int y, final int z) {
        return new Vec3i(this.x * x, this.y * y, this.z * z);
    }

    @Override
    public Vec3i div(final Vec3i vec) {
        return this.div(vec.x, vec.y, vec.z);
    }

    @Override
    public Vec3i div(final double by) {
        if (by >= 1.0) {
            final int asInt = (int) by;
            return this.div(asInt, asInt, asInt);
        } else {
            return this.scale(1.0 / by);
        }
    }

    public Vec3i div(final int x, final int y, final int z) {
        return new Vec3i(this.x / x, this.y / y, this.z / z);
    }

    @Override
    public Vec3i scale(final double n) {
        if (n >= 1.0) {
            final int asInt = (int) n;
            return this.mul(asInt, asInt, asInt);
        } else {
            return this.div(1.0 / n);
        }
    }

    @Override
    public Vec3i min(final Vec3i vec) {
        return new Vec3i(
                Math.min(this.x, vec.x),
                Math.min(this.y, vec.y),
                Math.min(this.z, vec.z)
        );
    }

    @Override
    public Vec3i max(final Vec3i vec) {
        return new Vec3i(
                Math.max(this.x, vec.x),
                Math.max(this.y, vec.y),
                Math.max(this.z, vec.z)
        );
    }

    public Vec3i down() {
        return this.move(Rotation.DOWN);
    }

    public Vec3i up() {
        return this.move(Rotation.UP);
    }

    public Vec3i north() {
        return this.move(Rotation.NORTH);
    }

    public Vec3i south() {
        return this.move(Rotation.SOUTH);
    }

    public Vec3i west() {
        return this.move(Rotation.WEST);
    }

    public Vec3i east() {
        return this.move(Rotation.EAST);
    }

    public Vec3i move(final Rotation rotation) {
        return move(rotation, 1);
    }

    public Vec3i move(final Rotation rotation, final int amount) {
        return this.add(rotation.getDirection().toVec3i().scale(amount));
    }

    @Override
    public double getX() {
        return this.x;
    }

    @Override
    public double getY() {
        return this.y;
    }

    @Override
    public double getZ() {
        return this.z;
    }

    @Override
    public Vec3i toVec3i() {
        return this;
    }
}
