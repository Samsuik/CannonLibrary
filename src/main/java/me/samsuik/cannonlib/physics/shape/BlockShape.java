package me.samsuik.cannonlib.physics.shape;

import me.samsuik.cannonlib.physics.Rotation;
import me.samsuik.cannonlib.physics.vec3.Vec3;
import me.samsuik.cannonlib.physics.vec3.Vec3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public final class BlockShape implements Shape {
    private final List<Shape> shapes;

    public static BlockShape single(final Shape shape) {
        return new BlockShape(List.of(shape));
    }

    public BlockShape(final List<Shape> shapes) {
        this.shapes = shapes;
    }
    
    private BlockShape transformShapes(final Function<Shape, Shape> transform) {
        final List<Shape> transformedShapes = new ArrayList<>();
        for (final Shape shape : this.shapes) {
            transformedShapes.add(transform.apply(shape));
        }
        return new BlockShape(transformedShapes);
    }

    private boolean matchAnyShape(final Predicate<Shape> predicate) {
        return this.shapes.stream().anyMatch(predicate);
    }

    public List<Shape> getShapes() {
        return Collections.unmodifiableList(this.shapes);
    }

    @Override
    public Vec3d getSize() {
        final List<Vec3d> corners = this.corners();
        final Vec3d min = corners.getFirst();
        final Vec3d max = corners.getLast();
        return max.sub(min);
    }

    @Override
    public Vec3d getCenter() {
        final List<Vec3d> corners = this.corners();
        final Vec3d min = corners.getFirst();
        final Vec3d max = corners.getLast();
        return min.add(max.sub(min).div(2.0));
    }

    @Override
    public List<Vec3d> corners() {
        Vec3d min = Vec3d.xyz(Double.POSITIVE_INFINITY);
        Vec3d max = Vec3d.xyz(Double.NEGATIVE_INFINITY);
        for (final Shape shape : this.shapes) {
            for (final Vec3d corner : shape.corners()) {
                min = corner.min(min);
                max = corner.max(max);
            }
        }
        return new AABB(min, max).corners();
    }

    @Override
    public BlockShape move(final Vec3<?> vec3) {
        return this.transformShapes(shape -> shape.move(vec3));
    }

    public BlockShape rotate(final Rotation rotation) {
        if (rotation.getAxis().isY()) {
            return this.flip(rotation.getAxis());
        } else {
            return this.rotateXZ(rotation.getDegrees() - Rotation.NORTH.getDegrees());
        }
    }

    @Override
    public BlockShape rotateXZ(final int degrees) {
        return this.transformShapes(shape -> shape.rotateXZ(degrees));
    }

    @Override
    public BlockShape flip(final Rotation.RotationAxis axis) {
        return this.transformShapes(shape -> shape.flip(axis));
    }

    @Override
    public boolean clip(final Vec3d from, final Vec3d to) {
        return this.matchAnyShape(shape -> shape.clip(from, to));
    }

    @Override
    public boolean contains(final Vec3<?> vec3) {
        return this.matchAnyShape(shape -> shape.contains(vec3));
    }


    @Override
    public double collideX(final AABB entityBB, double movement) {
        for (final Shape shape : this.shapes) {
            movement = shape.collideX(entityBB, movement);
        }
        return movement;
    }

    @Override
    public double collideY(final AABB entityBB, double movement) {
        for (final Shape shape : this.shapes) {
            movement = shape.collideY(entityBB, movement);
        }
        return movement;
    }

    @Override
    public double collideZ(final AABB entityBB, double movement) {
        for (final Shape shape : this.shapes) {
            movement = shape.collideZ(entityBB, movement);
        }
        return movement;
    }

    @Override
    public String toString() {
        return "BlockShape{" +
                "shapes=" + shapes +
                '}';
    }
}
