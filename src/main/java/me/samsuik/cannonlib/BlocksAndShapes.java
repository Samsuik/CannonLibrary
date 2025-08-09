package me.samsuik.cannonlib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.samsuik.cannonlib.block.Block;
import me.samsuik.cannonlib.physics.shape.AABB;
import me.samsuik.cannonlib.physics.shape.Shape;
import me.samsuik.cannonlib.physics.shape.Shapes;
import me.samsuik.cannonlib.physics.vec3.Vec3;
import me.samsuik.cannonlib.physics.vec3.Vec3i;

public final class BlocksAndShapes {
    private final Map<Vec3i, Section> sections = new HashMap<>();

        public void setBlock(final Vec3i position, final Block block) {
        final Vec3i sectionPosition = sectionPosition(position);
        this.sections.compute(sectionPosition, (sectionPos, section) -> {
            if (section == null) {
                section = new Section();
            }
            section.setBlock(position, block);
            return section.blocks.isEmpty() ? null : section;
        });
    }

    public Block getBlock(final Vec3i position) {
        final Vec3i sectionPosition = sectionPosition(position);
        final Section section = this.sections.get(sectionPosition);
        return section != null ? section.blocks.get(position) : null;
    }

    public Map<Vec3i, Block> getBlocks(final Vec3i position, final int radius) {
        final Map<Vec3i, Block> blocks = new HashMap<>();

        final Vec3i minSection = sectionPosition(position.sub(radius, radius, radius));
        final Vec3i maxSection = sectionPosition(position.add(radius, radius, radius));

        for (int x = minSection.x(); x <= maxSection.x(); x++) {
            for (int y = minSection.y(); y <= maxSection.y(); y++) {
                for (int z = minSection.z(); z <= maxSection.z(); z++) {
                    final Vec3i currentSectionPos = new Vec3i(x, y, z);
                    final Section section = this.sections.get(currentSectionPos);
                    if (section != null) {
                        blocks.putAll(section.blocks);
                    }
                }
            }
        }

        return blocks;
    }

    public Iterable<Shape> getCollisions(final AABB boundingBox) {
        return () -> new BlockCollisionIterator(boundingBox);
    }

    public void clear() {
        this.sections.clear();
    }

    public void copy(final BlocksAndShapes blocksAndShapes) {
        for (final Map.Entry<Vec3i, Section> sectionEntry : blocksAndShapes.sections.entrySet()) {
            final Section sectionCopy = sectionEntry.getValue().copy();
            this.sections.put(sectionEntry.getKey(), sectionCopy);
        }
    }

    private static Vec3i sectionPosition(final Vec3<?> position) {
        return position.div(16.0).toVec3i();
    }

    private final class BlockCollisionIterator implements Iterator<Shape> {
        private final Vec3i minSection;
        private final Vec3i sectionCount;
        private final int endIndex;
        private int index = 0;
        private Iterator<Shape> collisions = null;

        public BlockCollisionIterator(final AABB boundingBox) {
            final Vec3i maxSection = sectionPosition(boundingBox.maximum());
            this.minSection = sectionPosition(boundingBox.minimum());
            this.sectionCount = maxSection.sub(this.minSection).add(1, 1, 1);
            this.endIndex = (int) this.sectionCount.volume();
        }

        @Override
        public boolean hasNext() {
            for (;;) {
                if (this.collisions != null && this.collisions.hasNext()) {
                    return true;
                }

                final int sectionIndex = this.index++;
                if (sectionIndex >= this.endIndex) {
                    return false;
                }

                final Vec3i sectionCount = this.sectionCount;
                final int x = sectionIndex % sectionCount.x();
                final int y = (sectionIndex / sectionCount.x()) % sectionCount.y();
                final int z = sectionIndex / (sectionCount.x() * sectionCount.y());

                final Vec3i sectionPosition = this.minSection.add(x, y, z);
                final Section section = BlocksAndShapes.this.sections.get(sectionPosition);
                if (section != null) {
                    this.collisions = section.blockCollisions.iterator();
                }
            }
        }

        @Override
        public Shape next() {
            return this.collisions.next();
        }
    }

    private static final class Section {    
        private final Map<Vec3i, Block> blocks = new HashMap<>();
        private final Map<Vec3i, Shape> blockShapes = new HashMap<>();
        private final List<Shape> blockCollisions = new ArrayList<>();

        public void setBlock(final Vec3i position, final Block block) {
            if (block == null) {
                final Shape collision = this.blockShapes.remove(position);
                if (collision != null) {
                    this.blockCollisions.remove(collision);
                }
                this.blocks.remove(position);
            } else {
                final Shape collision = block.shape();
                if (collision != Shapes.EMPTY_SHAPE) {
                    final Shape movedCollision = collision.move(position);
                    final Shape present = this.blockShapes.put(position, movedCollision);
                    if (present != null) {
                        this.blockCollisions.remove(present);
                    }
                    this.blockCollisions.add(movedCollision);
                } else {
                    final Shape present = this.blockShapes.remove(position);
                    if (present != null) {
                        this.blockCollisions.remove(present);
                    }
                }
                this.blocks.put(position, block);
            }
        }

        public Section copy() {
            final Section section = new Section();
            section.blocks.putAll(this.blocks);
            section.blockShapes.putAll(this.blockShapes);
            section.blockCollisions.addAll(this.blockCollisions);
            return section;
        }
    }
}
