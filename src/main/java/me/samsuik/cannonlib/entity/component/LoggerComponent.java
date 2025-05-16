package me.samsuik.cannonlib.entity.component;

import me.samsuik.cannonlib.component.SimpleComponent;
import me.samsuik.cannonlib.entity.Entity;
import me.samsuik.cannonlib.entity.EntityDataKeys;
import me.samsuik.cannonlib.physics.vec3.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class LoggerComponent implements SimpleComponent<Entity> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerComponent.class);
    private final Function<Entity, List<Object>> entityInfo;
    private final List<Object> extraInfo;
    private final boolean includeEntityState;

    public LoggerComponent(final Function<Entity, List<Object>> entityInfo, final List<Object> extraInfo, final boolean includeEntityState) {
        this.entityInfo = entityInfo;
        this.extraInfo = extraInfo;
        this.includeEntityState = includeEntityState;
    }

    @Override
    public void action0(final Entity entity, final int tick) {
        final List<String> suffix = new ArrayList<>();
        final List<Object> entityInfoList = this.entityInfo.apply(entity);
        for (int in = 1; in < entityInfoList.size(); ++in) {
            suffix.add(String.valueOf(entityInfoList.get(in)));
        }
        for (final Object ex : extraInfo) {
            suffix.add(String.valueOf(ex));
        }

        final String extraString = String.join(" ", suffix);
        final String suffixString;
        final String entityState;
        if (this.includeEntityState) {
            final Vec3d pos = entity.position;
            final Vec3d mot = entity.momentum;
            entityState = "%s %s %s %s %s %s ".formatted(pos.x(), pos.y(), pos.z(), mot.x(), mot.y(), mot.z());
            suffixString = extraString.isEmpty() ? "" : "(%s)".formatted(extraString);
        } else {
            entityState = "";
            suffixString = extraString;
        }

        final String name = !entityInfoList.isEmpty() ? String.valueOf(entityInfoList.getFirst()) : "";
        LOGGER.info("{}{}: {}{}", tick, name, entityState, suffixString);
    }
}
