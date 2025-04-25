package me.samsuik.cannonlib;

import me.samsuik.cannonlib.block.Block;
import me.samsuik.cannonlib.entity.Entity;
import me.samsuik.cannonlib.physics.shape.Shape;
import me.samsuik.cannonlib.physics.shape.Shapes;
import me.samsuik.cannonlib.physics.vec3.Vec3i;
import me.samsuik.cannonlib.scenario.Scenario;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public final class World {
    // todo: We only need to do a COW if the entity list is being changed while ticking
    private final List<Entity> entityList = new CopyOnWriteArrayList<>();
    private final List<Shape> globalCollisions = new ArrayList<>();
    // todo: Move this into a tree
    private final Map<Vec3i, Block> blocks = new HashMap<>();
    private final Map<Vec3i, Shape> blockShapes = new HashMap<>();
    private final List<Shape> blockCollisions = new ArrayList<>();

    public void keepTicking() {
        while (this.hasEntities()) {
            this.tick();
        }
    }

    public void tick() {
        final List<Entity> toRemove = new ArrayList<>(0);
        for (final Entity entity : this.entityList) {
            if (entity.shouldRemove()) {
                toRemove.add(entity);
            } else {
                entity.tick();
            }
        }
        toRemove.reversed().forEach(this::removeEntity);
    }

    public boolean testScenario(final Scenario scenario) {
        return scenario.test(this);
    }

    public boolean hasEntities() {
        return !this.entityList.isEmpty();
    }

    public void addEntity(final Entity entity) {
        entity.setWorld(this);
        this.entityList.add(entity);
    }

    public void removeEntity(final Entity entity) {
        entity.setWorld(null);
        this.entityList.remove(entity);
    }

    public List<Entity> cloneEntity(final Entity entity, final int amount) {
        assert entity.getWorld() == this;
        final List<Entity> clonedEntities = new ArrayList<>(amount);
        for (int count = 0; count < amount; ++count) {
            final Entity entityCopy = entity.copy();
            entityCopy.setWorld(this);
            clonedEntities.add(entityCopy);
        }
        final int index = this.entityList.indexOf(entity);
        this.entityList.addAll(index, clonedEntities);
        return clonedEntities;
    }

    public List<Entity> getEntityList() {
        return Collections.unmodifiableList(this.entityList);
    }

    public Block getBlockAt(final Vec3i position) {
        return this.blocks.get(position);
    }

    public void removeBlock(final Vec3i position) {
        this.setBlock(position, null);
    }

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

    public void removeBlocks() {
        this.blocks.clear();
        this.blockShapes.clear();
        this.blockCollisions.clear();
    }

    public Map<Vec3i, Block> getBlocks() {
        return Collections.unmodifiableMap(this.blocks);
    }

    public List<Shape> getBlockCollisions() {
        return Collections.unmodifiableList(this.blockCollisions);
    }

    public void addGlobalCollision(final Shape collision) {
        this.globalCollisions.add(collision);
    }

    public void removeGlobalCollision(final Shape collision) {
        this.globalCollisions.remove(collision);
    }

    public List<Shape> getGlobalCollisions() {
        return Collections.unmodifiableList(this.globalCollisions);
    }

    public World snapshot() {
        final World world = new World();

        for (final Entity entity : this.entityList) {
            world.addEntity(entity.copy());
        }

        // Shapes and blocks are immutable
        world.blocks.putAll(this.blocks);
        world.blockShapes.putAll(this.blockShapes);
        world.blockCollisions.addAll(this.blockCollisions);
        world.globalCollisions.addAll(this.globalCollisions);
        return world;
    }
}
