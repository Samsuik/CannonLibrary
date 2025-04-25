package me.samsuik.cannonlib.physics.vec3;

public record Vec3i(int x, int y, int z) implements Vec3<Vec3i> {
    public static Vec3i zero() {
        return new Vec3i(0, 0, 0);
    }

    public static Vec3i xyz(final int n) {
        return new Vec3i(n, n, n);
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
        int asInt = (int) by;
        return this.div(asInt, asInt, asInt);
    }

    public Vec3i div(final int x, final int y, final int z) {
        return new Vec3i(this.x / x, this.y / y, this.z / z);
    }

    @Override
    public Vec3i scale(final double n) {
        final int a = (int) (1.0 / n);
        return this.div(a, a, a);
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
