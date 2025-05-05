package me.samsuik.cannonlib.physics.shape;

import me.samsuik.cannonlib.physics.Rotation;
import me.samsuik.cannonlib.physics.vec3.Vec3;
import me.samsuik.cannonlib.physics.vec3.Vec3d;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class AABB implements Shape {
    public final double minX;
    public final double minY;
    public final double minZ;
    public final double maxX;
    public final double maxY;
    public final double maxZ;

    public AABB(final Vec3d min, final Vec3d max) {
        this(min.x(), min.y(), min.z(), max.x(), max.y(), max.z());
    }

    public AABB(
            final double minX, final double minY, final double minZ,
            final double maxX, final double maxY, final double maxZ
    ) {
        this.minX = Math.min(minX, maxX);
        this.minY = Math.min(minY, maxY);
        this.minZ = Math.min(minZ, maxZ);
        this.maxX = Math.max(minX, maxX);
        this.maxY = Math.max(minY, maxY);
        this.maxZ = Math.max(minZ, maxZ);
    }

    @Override
    public Vec3d getSize() {
        return new Vec3d(
                this.maxX - this.minX,
                this.maxY - this.minY,
                this.maxZ - this.minZ
        );
    }

    @Override
    public Vec3d getCenter() {
        return new Vec3d(
                this.minX + (this.maxX - this.minX) / 2.0,
                this.minY + (this.maxY - this.minY) / 2.0,
                this.minZ + (this.maxZ - this.minZ) / 2.0
        );
    }

    @Override
    public List<Vec3d> corners() {
        return List.of(
                new Vec3d(this.minX, this.minY, this.minZ),
                new Vec3d(this.minX, this.minY, this.maxZ),
                new Vec3d(this.minX, this.maxY, this.minZ),
                new Vec3d(this.minX, this.maxY, this.maxZ),
                new Vec3d(this.maxX, this.minY, this.minZ),
                new Vec3d(this.maxX, this.minY, this.maxZ),
                new Vec3d(this.maxX, this.maxY, this.minZ),
                new Vec3d(this.maxX, this.maxY, this.maxZ)
        );
    }

    @Override
    public AABB move(final Vec3<?> vec3) {
        return this.move(vec3.getX(), vec3.getY(), vec3.getZ());
    }

    public AABB move(final double x, final double y, final double z) {
        return new AABB(
                this.minX + x,
                this.minY + y,
                this.minZ + z,
                this.maxX + x,
                this.maxY + y,
                this.maxZ + z
        );
    }

    public AABB inflate(final Vec3<?> inflation) {
        return this.inflate(inflation.getX(), inflation.getY(), inflation.getZ());
    }

    public AABB inflate(final double x, final double y, final double z) {
        return new AABB(
                this.minX - x,
                this.minY - y,
                this.minZ - z,
                this.maxX + x,
                this.maxY + y,
                this.maxZ + z
        );
    }

    public AABB expandTo(final Vec3d pos) {
        return this.expandTo(new AABB(pos, pos));
    }

    public AABB expandTo(final AABB bb) {
        return new AABB(
                Math.min(this.minX, bb.minX),
                Math.min(this.minY, bb.minY),
                Math.min(this.minZ, bb.minZ),
                Math.max(this.maxX, bb.maxX),
                Math.max(this.maxY, bb.maxY),
                Math.max(this.maxZ, bb.maxZ)
        );
    }

    private AABB performBlockRotation(final int degrees, final BiFunction<Integer, Vec3d, Vec3d> transform) {
        final int normDeg = ((degrees % 360) + 360) % 360;
        if (normDeg % 90 != 0) {
            throw new IllegalArgumentException("Angle must be 0, 90, 180, or 270 degrees. Got: " + degrees);
        }

        Vec3d min = Vec3d.xyz(Double.POSITIVE_INFINITY);
        Vec3d max = Vec3d.xyz(Double.NEGATIVE_INFINITY);
        for (final Vec3d corner : this.corners()) {
            final Vec3d transformed = transform.apply(degrees, corner);
            min = transformed.min(min);
            max = transformed.max(max);
        }
        return new AABB(min, max);
    }

    @Override
    public AABB rotate(int degreesX, int degreesY) {
        return this.rotateYZ(degreesX).rotateXZ(degreesY);
    }

    @Override
    public AABB rotateYZ(final int degreesX) {
        return this.performBlockRotation(degreesX, (deg, p) -> switch (deg) {
            case 90  -> new Vec3d(p.x(), p.z(), 1.0 - p.y());
            case 180 -> new Vec3d(p.x(), 1.0 - p.y(), 1.0 - p.z());
            case 270 -> new Vec3d(p.x(), 1.0 - p.z(), p.y());
            default  -> p;
        });
    }

    @Override
    public AABB rotateXZ(final int degreesY) {
        return this.performBlockRotation(degreesY, (deg, p) -> switch (deg) {
            case 90  -> new Vec3d(p.z(), p.y(), 1.0 - p.x());
            case 180 -> new Vec3d(1.0 - p.x(), p.y(), 1.0 - p.z());
            case 270 -> new Vec3d(1.0 - p.z(), p.y(), p.x());
            default  -> p;
        });
    }

    @Override
    public AABB flip(final Rotation.RotationAxis axis) {
        return new AABB(
                axis.isX() ? 1.0 - this.minX : this.minX,
                axis.isY() ? 1.0 - this.minY : this.minY,
                axis.isZ() ? 1.0 - this.minZ : this.minZ,
                axis.isX() ? 1.0 - this.maxX : this.maxX,
                axis.isY() ? 1.0 - this.maxY : this.maxY,
                axis.isZ() ? 1.0 - this.maxZ : this.maxZ
        );
    }

    @Override
    public boolean contains(final Vec3<?> vec3) {
        return vec3.getX() >= this.minX && vec3.getX() < this.maxX
            && vec3.getY() >= this.minY && vec3.getY() < this.maxY
            && vec3.getZ() >= this.minZ && vec3.getZ() < this.maxZ;
    }

    public boolean containsVecInclusive(final Vec3<?> vec3) {
        return vec3.getX() >= this.minX && vec3.getX() <= this.maxX
            && vec3.getY() >= this.minY && vec3.getY() <= this.maxY
            && vec3.getZ() >= this.minZ && vec3.getZ() <= this.maxZ;
    }

    public boolean containsBBInclusive(final AABB bb) {
        return this.minX <= bb.minX && this.maxX >= bb.maxX
            && this.minY <= bb.minY && this.maxY >= bb.maxY
            && this.minZ <= bb.minZ && this.maxZ >= bb.maxZ;
    }

    public boolean clip(final Vec3d from, final Vec3d to) {
        final double diffX = to.x() - from.x();
        final double diffY = to.y() - from.y();
        final double diffZ = to.z() - from.z();

        final double minX = this.minX;
        final double minY = this.minY;
        final double minZ = this.minZ;
        final double maxX = this.maxX;
        final double maxY = this.maxY;
        final double maxZ = this.maxZ;

        return clipPoint(diffX, diffY, diffZ, minX, maxX, minY, maxY, minZ, maxZ, from.x(), from.y(), from.z())
            || clipPoint(diffY, diffZ, diffX, minY, maxY, minZ, maxZ, minX, maxX, from.y(), from.z(), from.x())
            || clipPoint(diffZ, diffX, diffY, minZ, maxZ, minX, maxX, minY, maxY, from.z(), from.x(), from.y());
    }

    private static boolean clipPoint(
            double distanceSide,
            double distanceOtherA,
            double distanceOtherB,
            double minSide,
            double maxSide,
            double minOtherA,
            double maxOtherA,
            double minOtherB,
            double maxOtherB,
            double startSide,
            double startOtherA,
            double startOtherB
    ) {
        final double side;
        if (distanceSide < -1.0e-7) {
            side = maxSide;
        } else if (distanceSide > 1.0e-7) {
            side = minSide;
        } else {
            return false;
        }

        double d = (side - startSide) / distanceSide;
        double d1 = startOtherA + d * distanceOtherA;
        double d2 = startOtherB + d * distanceOtherB;
        return 0.0 < d && minOtherA - 1.0E-7 < d1 && d1 < maxOtherA + 1.0E-7 && minOtherB - 1.0E-7 < d2 && d2 < maxOtherB + 1.0E-7;
    }

    public double collideX(final AABB entityBB, final double movement) {
        if (this.minY < entityBB.maxY && this.maxY > entityBB.minY &&
            this.minZ < entityBB.maxZ && this.maxZ > entityBB.minZ
        ) {
            if (movement > 0.0 && entityBB.maxX <= this.minX) {
                final double move = this.minX - entityBB.maxX;
                return Math.min(move, movement);
            } else if (movement < 0.0 && entityBB.minX >= this.maxX) {
                final double move = this.maxX - entityBB.minX;
                return Math.max(move, movement);
            }
        }
        return movement;
    }

    public double collideY(final AABB entityBB, final double movement) {
        if (this.minX < entityBB.maxX && this.maxX > entityBB.minX &&
            this.minZ < entityBB.maxZ && this.maxZ > entityBB.minZ
        ) {
            if (movement > 0.0 && entityBB.maxY <= this.minY) {
                final double move = this.minY - entityBB.maxY;
                return Math.min(move, movement);
            } else if (movement < 0.0 && entityBB.minY >= this.maxY) {
                final double move = this.maxY - entityBB.minY;
                return Math.max(move, movement);
            }
        }
        return movement;
    }

    public double collideZ(final AABB entityBB, final double movement) {
        if (this.minX < entityBB.maxX && this.maxX > entityBB.minX &&
            this.minY < entityBB.maxY && this.maxY > entityBB.minY
        ) {
            if (movement > 0.0 && entityBB.maxZ <= this.minZ) {
                final double move = this.minZ - entityBB.maxZ;
                return Math.min(move, movement);
            } else if (movement < 0.0 && entityBB.minZ >= this.maxZ) {
                final double move = this.maxZ - entityBB.minZ;
                return Math.max(move, movement);
            }
        }
        return movement;
    }

    @Override
    public String toString() {
        return "AABB{" +
                "minX=" + minX +
                ", minY=" + minY +
                ", minZ=" + minZ +
                ", maxX=" + maxX +
                ", maxY=" + maxY +
                ", maxZ=" + maxZ +
                '}';
    }
}
