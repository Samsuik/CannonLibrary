package me.samsuik.cannonlib.block;

import me.samsuik.cannonlib.block.interaction.Interaction;
import me.samsuik.cannonlib.block.interaction.BlockInteractions;
import me.samsuik.cannonlib.physics.Rotation;
import me.samsuik.cannonlib.physics.shape.BlockShape;
import me.samsuik.cannonlib.physics.shape.Shape;
import me.samsuik.cannonlib.physics.shape.Shapes;

public record Block(String name, BlockShape shape, Interaction interaction, float strength, int durability, boolean replace) {
    public static Block from(final String name, final Shape shape, final float strength) {
        return new Block(name, Shapes.asBlockShape(shape), BlockInteractions.NONE, strength, 1, false);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Block name(final String newName) {
        return new Block(newName, this.shape, this.interaction, this.strength, this.durability, this.replace);
    }

    public Block shape(final BlockShape shape) {
        return new Block(this.name, shape, this.interaction, this.strength, this.durability, this.replace);
    }

    public Block interaction(final Interaction interaction) {
        return new Block(this.name, this.shape, interaction, this.strength, this.durability, this.replace);
    }

    public Block strength(final float strength) {
        return new Block(this.name, this.shape, this.interaction, strength, this.durability, this.replace);
    }

    public Block durability(final int durability) {
        return new Block(this.name, this.shape, this.interaction, this.strength, durability, this.replace);
    }

    public Block replace(final boolean replace) {
        return new Block(this.name, this.shape, this.interaction, this.strength, this.durability, replace);
    }

    public Block rotate(final Rotation... rotations) {
        BlockShape rotatedShape = this.shape;
        for (final Rotation rotation : rotations) {
            rotatedShape = rotatedShape.rotate(rotation);
        }
        return this.shape(rotatedShape);
    }

    public Block rotate(final int degreesX, final int degreesY) {
        return this.shape(this.shape.rotate(degreesX, degreesY));
    }

    public Block flip(final Rotation.RotationAxis axis) {
        return this.shape(this.shape.flip(axis));
    }

    public Block damage() {
        final int newDurability = this.durability - 1;
        if (newDurability <= 0) {
            return null;
        }
        return this.durability(newDurability);
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
        private int durability = 1;
        private boolean replace = false;

        private Builder() {}

        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        public Builder shape(final Shape shape) {
            this.shape = Shapes.asBlockShape(shape);
            return this;
        }

        public Builder interaction(final Interaction interaction) {
            this.interaction = interaction;
            return this;
        }

        public Builder strength(final float strength) {
            this.strength = strength;
            return this;
        }

        public Builder durability(final int durability) {
            this.durability = durability;
            return this;
        }

        public Builder replace() {
            this.replace = true;
            return this;
        }

        public Block build() {
            assert this.name != null : "name must be provided";
            return new Block(this.name, this.shape, this.interaction, this.strength, this.durability, this.replace);
        }
    }
}
