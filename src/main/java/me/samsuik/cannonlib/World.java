package me.samsuik.cannonlib;

import me.samsuik.cannonlib.block.Block;
import me.samsuik.cannonlib.block.Blocks;
import me.samsuik.cannonlib.entity.Entity;
import me.samsuik.cannonlib.physics.Rotation;
import me.samsuik.cannonlib.physics.shape.AABB;
import me.samsuik.cannonlib.physics.shape.Shape;
import me.samsuik.cannonlib.physics.shape.Shapes;
import me.samsuik.cannonlib.physics.vec3.Vec3d;
import me.samsuik.cannonlib.physics.vec3.Vec3i;

import java.util.*;

public final class World {
    private final EntityList entityList = new EntityList();
    private final BlocksAndShapes blocks = new BlocksAndShapes();
    private final ScheduledBlockTicks blockTicks = new ScheduledBlockTicks();
    private final List<Shape> globalCollisions = new ArrayList<>();
    private boolean blockUpdates = false;

    public void doBlockPhysics(final boolean physics) {
        this.blockUpdates = physics;
    }

    public boolean hasEntities() {
        return !this.entityList.isEmpty();
    }

    public void tick() {
        this.blockTicks.tick(this);
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
        this.entityList.add(entity);
        entity.changeWorld(this);
    }

    public void removeEntity(final Entity entity) {
        this.entityList.remove(entity);
        entity.changeWorld(null);
    }

    public List<Entity> cloneEntity(final Entity entity, final int amount) {
        assert entity.getWorld() == this : "entity must be apart of this world";
        final List<Entity> clonedEntities = new ArrayList<>(amount);
        for (int count = 0; count < amount; ++count) {
            clonedEntities.add(entity.copy());
        }
        this.entityList.addAllAfter(entity, clonedEntities);
        for (final Entity clonedEntity : clonedEntities) {
            clonedEntity.changeWorld(this);
        }
        return clonedEntities;
    }

    public Collection<Entity> getEntityList() {
        return this.entityList;
    }

    public void removeBlock(final Vec3i position) {
        this.setBlock(position, null);
    }

    public Block getBlockAt(final Vec3i position) {
        final Block block = this.getBlockAtRaw(position);
        return block == null ? Blocks.AIR : block;
    }

    public Block getBlockAtRaw(final Vec3i position) {
        return this.blocks.getBlock(position);
    }

    public Map<Vec3i, Block> getBlocks(final Vec3i position, final int radius) {
        return this.blocks.getBlocks(position, radius);
    }

    public void setBlock(final Vec3i position, final Block block) {
        this.blocks.setBlock(position, block);

        if (this.blockUpdates) {
            this.updateBlocksAroundPos(position, block);
        }
    }

    public void updateBlocksAroundPos(final Vec3i position, final Block block) {
        for (final Rotation rotation : Rotation.values()) {
            final Vec3i neighbor = position.move(rotation);
            final Block neighborBlock = this.getBlockAtRaw(neighbor);

            if (neighborBlock != null) {
                this.blockUpdate(neighbor, neighborBlock);
            }
        }

        if (block != null) {
            this.blockUpdate(position, block);
        }
    }

    public void blockUpdate(final Vec3i position, final Block block) {
        final int ticks = block.interaction().onBlockUpdate(this, position, block);
        if (ticks > 0) {
            this.blockTicks.scheduleBlockTick(ticks - 1, position, block);
        }
    }

    public void removeBlocks() {
        this.blocks.clear();
    }

    public void floor(final int level) {
        this.addGlobalCollision(Shapes.floor(level));
    }

    public void border(final int size) {
        final Vec3d xyz = new Vec3d(size, size, size);
        for (final Rotation rotation : Rotation.values()) {
            this.addGlobalCollision(Shapes.plane(
                    xyz.mul(rotation.getDirection()).x(),
                    xyz.mul(rotation.getDirection()).y(),
                    xyz.mul(rotation.getDirection()).z()
            ));
        }
    }

    public void addGlobalCollision(final Shape collision) {
        this.globalCollisions.add(collision);
    }

    public void removeGlobalCollision(final Shape collision) {
        this.globalCollisions.remove(collision);
    }

    public Collisions getCollisions(final AABB boundingBox) {
        return new Collisions(this.blocks.getCollisions(boundingBox), this.globalCollisions);
    }

    public World snapshot() {
        final World world = new World();

        // deep copy all entities
        for (final Entity entity : this.entityList) {
            world.addEntity(entity.copy());
        }

        // Shapes and blocks are immutable
        world.blocks.copy(this.blocks);

        // copy all scheduled blocks
        world.blockTicks.copy(this.blockTicks);

        world.globalCollisions.addAll(this.globalCollisions);
        return world;
    }
}
