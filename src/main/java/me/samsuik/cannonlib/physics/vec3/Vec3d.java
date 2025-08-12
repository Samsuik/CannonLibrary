package me.samsuik.cannonlib.physics.vec3;

public record Vec3d(double x, double y, double z) implements Vec3<Vec3d> {
    private static final Vec3d ZERO = new Vec3d(0.0, 0.0, 0.0);

    public static Vec3d zero() {
        return ZERO;
    }

    public static Vec3d xyz(final double n) {
        return new Vec3d(n, n, n);
    }

    public static Vec3d center(final int x, final int y, final int z) {
        return new Vec3d(x + 0.5, y, z + 0.5);
    }

    @Override
    public Vec3d add(final Vec3d vec) {
        return this.add(vec.x, vec.y, vec.z);
    }

    public Vec3d add(final double x, final double y, final double z) {
        return new Vec3d(this.x + x, this.y + y, this.z + z);
    }

    @Override
    public Vec3d sub(final Vec3d vec) {
        return this.sub(vec.x, vec.y, vec.z);
    }

    public Vec3d sub(final double x, final double y, final double z) {
        return this.add(-x, -y, -z);
    }

    @Override
    public Vec3d mul(final Vec3d vec) {
        return this.mul(vec.x, vec.y, vec.z);
    }

    public Vec3d mul(final double x, final double y, final double z) {
        return new Vec3d(this.x * x, this.y * y, this.z * z);
    }

    @Override
    public Vec3d div(final Vec3d vec) {
        return this.div(vec.x, vec.y, vec.z);
    }

    @Override
    public Vec3d div(final double n) {
        return this.div(n, n, n);
    }

    public Vec3d div(final double x, final double y, final double z) {
        return new Vec3d(
                this.x / x,
                this.y / y,
                this.z / z
        );
    }

    @Override
    public Vec3d scale(final double by) {
        return this.mul(by, by, by);
    }

    @Override
    public Vec3d min(final Vec3d vec) {
        return new Vec3d(
                Math.min(this.x, vec.x),
                Math.min(this.y, vec.y),
                Math.min(this.z, vec.z)
        );
    }

    @Override
    public Vec3d max(final Vec3d vec) {
        return new Vec3d(
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
    public Vec3d toVec3d() {
        return this;
    }
}
