package me.samsuik.cannonlib.world;

import me.samsuik.cannonlib.block.Block;
import me.samsuik.cannonlib.component.Components;
import me.samsuik.cannonlib.component.ComponentsHolder;
import me.samsuik.cannonlib.entity.Entity;
import me.samsuik.cannonlib.physics.shape.Shape;
import me.samsuik.cannonlib.physics.shape.Shapes;
import me.samsuik.cannonlib.physics.vec3.Vec3i;

import java.util.*;

public final class World implements ComponentsHolder<World> {
    private final Components<World> components = new Components<>();
    private final EntityList entityList = new EntityList();
    private final List<Shape> globalCollisions = new ArrayList<>();
    // todo: tree
    private final Map<Vec3i, Block> blocks = new HashMap<>();
    private final Map<Vec3i, Shape> blockShapes = new HashMap<>();
    private final List<Shape> blockCollisions = new ArrayList<>();

    public boolean hasEntities() {
        return !this.entityList.isEmpty();
    }

    public void tick() {
        this.entityList.setTicking(true);
        for (final Entity entity : this.entityList) {
            if (!entity.shouldRemove()) {
                entity.tick();
            }
        }
        this.entityList.setTicking(false);
        this.entityList.removeIf(Entity::shouldRemove);
    }

    public void keepTicking() {
        while (this.hasEntities()) {
            this.tick();
        }
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
        assert entity.getWorld() == this : "entity must be a member of this world";
        final List<Entity> clonedEntities = new ArrayList<>(amount);
        for (int count = 0; count < amount; ++count) {
            final Entity entityCopy = entity.copy();
            entityCopy.setWorld(this);
            clonedEntities.add(entityCopy);
        }
        this.entityList.addAllAfter(entity, clonedEntities);
        return clonedEntities;
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

    public void addGlobalCollision(final Shape collision) {
        this.globalCollisions.add(collision);
    }

    public void removeGlobalCollision(final Shape collision) {
        this.globalCollisions.remove(collision);
    }

    public Collection<Entity> getEntityList() {
        return this.entityList;
    }

    public Map<Vec3i, Block> getBlocks() {
        return Collections.unmodifiableMap(this.blocks);
    }
    
    public Collisions getCollisions() {
        return new Collisions(this.globalCollisions, this.blockCollisions);
    }

    @Override
    public Components<World> getComponents() {
        return this.components;
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
