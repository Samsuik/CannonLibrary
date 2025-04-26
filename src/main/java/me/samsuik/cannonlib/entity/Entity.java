package me.samsuik.cannonlib.entity;

import me.samsuik.cannonlib.World;
import me.samsuik.cannonlib.component.Component;
import me.samsuik.cannonlib.component.ComponentsHolder;
import me.samsuik.cannonlib.component.Components;
import me.samsuik.cannonlib.entity.data.DataKeys;
import me.samsuik.cannonlib.entity.data.KeyedDataStorage;
import me.samsuik.cannonlib.entity.data.KeyedDataStorageHolder;
import me.samsuik.cannonlib.physics.Rotation;
import me.samsuik.cannonlib.physics.shape.AABB;
import me.samsuik.cannonlib.physics.shape.Shapes;
import me.samsuik.cannonlib.physics.vec3.Vec3d;
import me.samsuik.cannonlib.physics.vec3.Vec3i;

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

    public void setWorld(final World world) {
        assert this.world == null || world == null : "This entity has already been added to a world";
        assert this.remove || world != null : "Cannot remove the entity from a world while the entity is alive";
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
        final Vec3d direction = rotation.getDirection();
        if (this.position.fraction().mul(direction).addAll() == 0.0) {
            this.position = this.position.add(Vec3d.xyz(0.5).mul(direction.abs()));
        }

        final AABB entityBB = Shapes.entityBoundingBox(this.position, 0.98f);
        final Vec3i alignTo = this.position.add(direction).toVec3i();
        final AABB bb = Shapes.block(alignTo.x(), alignTo.y(), alignTo.z());

        double movement = direction.scale(1.0).addAll();
        double moveX = 0.0;
        double moveY = 0.0;
        double moveZ = 0.0;

        switch (rotation.getAxis()) {
            case X -> moveX = bb.collideX(entityBB, movement);
            case Y -> moveY = bb.collideY(entityBB, movement);
            case Z -> moveZ = bb.collideZ(entityBB, movement);
        }

        if (moveX + moveY + moveZ != movement) {
            this.position = this.position.add(moveX, moveY, moveZ);
        }
    }

    public Entity copy() {
        final Entity entity = new Entity();
        entity.putData(DataKeys.COPY, true);
//        entity.dataStorage.copyFrom(this.dataStorage);
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
