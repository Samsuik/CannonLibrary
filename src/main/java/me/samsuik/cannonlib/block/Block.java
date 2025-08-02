package me.samsuik.cannonlib.block;

import me.samsuik.cannonlib.block.interaction.Interaction;
import me.samsuik.cannonlib.block.interaction.BlockInteractions;
import me.samsuik.cannonlib.physics.Rotation;
import me.samsuik.cannonlib.physics.shape.BlockShape;
import me.samsuik.cannonlib.physics.shape.Shape;
import me.samsuik.cannonlib.physics.shape.Shapes;

public record Block(String name, BlockShape shape, Interaction interaction, float strength, boolean replace) {
    public static Block from(String name, BlockShape shape, float strength) {
        return new Block(name, shape, BlockInteractions.NONE, strength, false);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Block name(final String newName) {
        return new Block(newName, this.shape, this.interaction, this.strength, this.replace);
    }

    public Block shape(final BlockShape shape) {
        return new Block(this.name, shape, this.interaction, this.strength, this.replace);
    }

    public Block interaction(final Interaction interaction) {
        return new Block(this.name, this.shape, interaction, this.strength, this.replace);
    }

    public Block strength(final float strength) {
        return new Block(this.name, this.shape, this.interaction, strength, this.replace);
    }

    public Block replace(final boolean replace) {
        return new Block(this.name, this.shape, this.interaction, this.strength, replace);
    }

    public Block rotate(final Rotation... rotations) {
        BlockShape rotatedShape = this.shape;
        for (final Rotation rotation : rotations) {
            rotatedShape = rotatedShape.rotate(rotation);
        }
        return new Block(this.name, rotatedShape, this.interaction, this.strength, this.replace);
    }

    public Block rotate(final int degreesX, final int degreesY) {
        final BlockShape rotatedShape = this.shape.rotate(degreesX, degreesY);
        return new Block(this.name, rotatedShape, this.interaction, this.strength, this.replace);
    }

    public Block flip(final Rotation.RotationAxis axis) {
        return new Block(this.name, this.shape.flip(axis), this.interaction, this.strength, this.replace);
    }

    public float blastResistance() {
        return (this.strength + 0.3f) * 0.3f;
    }

    public boolean has(final Interaction interaction) {
        return this.interaction == interaction;
    }

    public static final class Builder {
        private String name;
        private BlockShape shape = Shapes.EMPTY_SHAPE;
        private Interaction interaction = BlockInteractions.NONE;
        private float strength = 0.0f;
        private boolean replace = false;

        private Builder() {}

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder shape(Shape shape) {
            return this.shape(BlockShape.single(shape));
        }

        public Builder shape(BlockShape shape) {
            this.shape = shape;
            return this;
        }

        public Builder interaction(Interaction interaction) {
            this.interaction = interaction;
            return this;
        }

        public Builder strength(float strength) {
            this.strength = strength;
            return this;
        }

        public Builder replace() {
            this.replace = true;
            return this;
        }

        public Block build() {
            assert this.name != null : "name must be provided";
            return new Block(this.name, this.shape, this.interaction, this.strength, this.replace);
        }
    }
}
