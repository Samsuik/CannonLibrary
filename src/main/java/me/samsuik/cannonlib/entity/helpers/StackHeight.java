package me.samsuik.cannonlib.entity.helpers;

import me.samsuik.cannonlib.entity.Entity;
import me.samsuik.cannonlib.entity.EntityDataKeys;

import java.util.*;

public record StackHeight(String name, int height) {
    public static List<StackHeight> getStackHeights(final Collection<Entity> entities) {
        final Map<String, Entity> stackedEntities = new HashMap<>();
        for (final Entity entity : entities) {
            if (entity.hasData(EntityDataKeys.STACKED)) {
                final String entityName = entity.getDataOrDefault(EntityDataKeys.NAME, "unknown");
                final Entity present = stackedEntities.get(entityName);
                if (present == null || present.position.y() > entity.position.y()) {
                    stackedEntities.put(entityName, entity);
                }
            }
        }

        final Map<Integer, StackHeight> stackHeights = new TreeMap<>();
        for (final Map.Entry<String, Entity> stack : stackedEntities.entrySet()) {
            final Entity entity = stack.getValue();
            final int entityPos = entity.position.toVec3i().y();
            final int stackOrder = entity.getData(EntityDataKeys.STACKED);
            stackHeights.put(stackOrder, new StackHeight(stack.getKey(), entityPos));
        }
        return new ArrayList<>(stackHeights.values());
    }

    public String information(final boolean simplified) {
        final StringBuilder simplifiedName = new StringBuilder();
        for (final String part : this.name.split(" ")) {
            simplifiedName.append(part.charAt(0));
        }
        final String name = simplified ? simplifiedName.toString() : this.name;
        return "\"%s\": %s".formatted(name, this.height);
    }

    @Override
    public String toString() {
        return this.information(true);
    }
}
