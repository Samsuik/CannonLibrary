package me.samsuik.cannonlib.block;

import me.samsuik.cannonlib.physics.Rotation;
import me.samsuik.cannonlib.physics.shape.BlockShape;

public record Block(String name, BlockShape shape, float blastResistance, boolean replace) {
    public Block(String name, BlockShape shape, float blastResistance) {
        this(name, shape, blastResistance, false);
    }

    public Block rotate(final Rotation... rotations) {
        BlockShape rotatedShape = this.shape;
        for (final Rotation rotation : rotations) {
            rotatedShape = rotatedShape.rotate(rotation);
        }
        return new Block(this.name, rotatedShape, this.blastResistance);
    }

    public Block rotate(final int degreesX, final int degreesY) {
        final BlockShape rotatedShape = this.shape.rotate(degreesX, degreesY);
        return new Block(this.name, rotatedShape, this.blastResistance);
    }

    public Block flip(final Rotation.RotationAxis axis) {
        return new Block(this.name, this.shape.flip(axis), this.blastResistance);
    }

    public Block name(final String newName) {
        return new Block(newName, this.shape, this.blastResistance);
    }

    @Override
    public float blastResistance() {
        return (this.blastResistance + 0.3f) * 0.3f;
    }
}
