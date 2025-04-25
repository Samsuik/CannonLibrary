package me.samsuik.cannonlib.entity.component;

import me.samsuik.cannonlib.component.Component;
import me.samsuik.cannonlib.entity.Entity;
import me.samsuik.cannonlib.physics.vec3.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class LoggerComponent implements Component<Entity> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerComponent.class);
    private final Function<Entity, List<String>> entityInfo;
    private final Object[] extraInfo;

    public LoggerComponent(final Function<Entity, List<String>> entityInfo, final Object... extraInfo) {
        this.entityInfo = entityInfo;
        this.extraInfo = extraInfo;
    }

    @Override
    public void action(final Entity entity, final int tick) {
        final List<String> suffix = new ArrayList<>();
        final List<String> entityInfoList = this.entityInfo.apply(entity);
        for (int in = 1; in < entityInfoList.size(); ++in) {
            suffix.add(entityInfoList.get(in));
        }
        for (final Object ex : extraInfo) {
            suffix.add(String.valueOf(ex));
        }
        final String extraString = String.join(" ", suffix);
        final String suffixString = extraString.isEmpty() ? "" : "(%s)".formatted(extraString);

        final String name = !entityInfoList.isEmpty() ? entityInfoList.getFirst() : "";
        final Vec3d pos = entity.position;
        final Vec3d mot = entity.momentum;
        LOGGER.info("{}{}: {} {} {} {} {} {} {}", tick, name, pos.x(), pos.y(), pos.z(), mot.x(), mot.y(), mot.z(), suffixString);
    }
}
