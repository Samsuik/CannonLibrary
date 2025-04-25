package me.samsuik.cannonlib.block;

import me.samsuik.cannonlib.physics.Rotation;
import me.samsuik.cannonlib.physics.shape.BlockShape;

public record Block(BlockShape shape, float blastResistance, boolean replace) {
    public Block(BlockShape shape, float blastResistance) {
        this(shape, blastResistance, false);
    }

    public Block rotate(final Rotation... rotations) {
        BlockShape rotatedShape = this.shape;
        for (final Rotation rotation : rotations) {
            rotatedShape = rotatedShape.rotate(rotation);
        }
        return new Block(rotatedShape, this.blastResistance);
    }

    public Block flip(final Rotation.RotationAxis axis) {
        return new Block(this.shape.flip(axis), this.blastResistance);
    }

    @Override
    public float blastResistance() {
        return (this.blastResistance + 0.3f) * 0.3f;
    }
}
