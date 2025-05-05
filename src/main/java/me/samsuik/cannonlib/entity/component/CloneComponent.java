package me.samsuik.cannonlib.entity.component;

import me.samsuik.cannonlib.World;
import me.samsuik.cannonlib.component.Component;
import me.samsuik.cannonlib.entity.Entity;

import java.util.List;

public final class CloneComponent implements Component<Entity> {
    private final int amount;

    public CloneComponent(final int amount) {
        this.amount = amount;
    }

    @Override
    public boolean action(final Entity entity, final int tick) {
        final World world = entity.getWorld();
        final List<Entity> entities = world.cloneEntity(entity, this.amount);
        for (final Entity clonedEntity : entities) {
            clonedEntity.getComponents().continueTicking(clonedEntity, entity.getComponents());
        }
        return true;
    }
}
