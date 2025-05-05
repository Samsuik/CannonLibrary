package me.samsuik.cannonlib.entity;

import me.samsuik.cannonlib.World;
import me.samsuik.cannonlib.block.Block;
import me.samsuik.cannonlib.block.Blocks;
import me.samsuik.cannonlib.component.Component;
import me.samsuik.cannonlib.component.ComponentsHolder;
import me.samsuik.cannonlib.component.Components;
import me.samsuik.cannonlib.data.KeyedDataStorage;
import me.samsuik.cannonlib.data.KeyedDataStorageHolder;
import me.samsuik.cannonlib.entity.helpers.Alignment;
import me.samsuik.cannonlib.physics.Rotation;
import me.samsuik.cannonlib.physics.vec3.Vec3d;

import java.util.List;
import java.util.function.Consumer;

public final class Entity implements ComponentsHolder<Entity>, KeyedDataStorageHolder {
    public static Entity withComponents(final List<Component<Entity>> components) {
        return create(entity -> {}, components);
    }

    public static Entity create(final Consumer<Entity> consumer, final List<Component<Entity>> components) {
        final Entity entity = new Entity();
        consumer.accept(entity);
        components.forEach(entity::addComponent);
        return entity;
    }

    public static EntityBuilder builder() {
        return new EntityBuilder();
    }

    private final Components<Entity> components = new Components<>();
    private final KeyedDataStorage dataStorage = new KeyedDataStorage();
    private World world = null;
    public Vec3d position = Vec3d.zero();
    public Vec3d momentum = Vec3d.zero();
    private EntityState entityState = EntityState.none();
    public boolean onGround = false;
    private boolean remove = false;

    @Override
    public Components<Entity> getComponents() {
        return this.components;
    }

    @Override
    public KeyedDataStorage getData() {
        return this.dataStorage;
    }

    public World getWorld() {
        return this.world;
    }

    public void changeWorld(final World world) {
        final boolean removeEntity = world == null;
        final boolean hasWorld = this.world != null;
        if (removeEntity != hasWorld) {
            throw new IllegalArgumentException("world mismatch");
        }
        this.world = world;
    }

    public boolean shouldRemove() {
        return this.remove;
    }

    public void remove() {
        this.remove = true;
    }

    public EntityState getEntityState() {
        return this.entityState;
    }

    public void tick() {
        this.entityState = new EntityState(this.position, this.momentum);
        this.tickComponents(this);
    }

    public void align(final Rotation rotation) {
        this.align(rotation, Blocks.BEDROCK);
    }

    public void align(final Rotation rotation, final Block block) {
        this.position = Alignment.alignPosition(rotation, this.position, block);
    }

    public Entity copy() {
        final Entity entity = new Entity();
        entity.putData(EntityDataKeys.COPY, true);
        entity.dataStorage.copyDataFrom(this.dataStorage);
        entity.components.copyComponentsFrom(this.components);
        entity.position = this.position;
        entity.momentum = this.momentum;
        entity.entityState = this.entityState;
        entity.onGround = this.onGround;
        entity.remove = this.remove;
        return entity;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "momentum=" + momentum +
                ", position=" + position +
                ", onGround=" + onGround +
                ", remove=" + remove +
                '}';
    }
}
