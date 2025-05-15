package me.samsuik.cannonlib.entity.component;

import me.samsuik.cannonlib.component.DataComponent;
import me.samsuik.cannonlib.entity.Entity;
import me.samsuik.cannonlib.entity.EntityDataKeys;

public final class HammerRatioComponent implements DataComponent<Entity, Double> {
    private int tnt = 1;
    private int currentTick = 0;

    @Override
    public Double data(final Entity user, final int tick, final Double value) {
        if (tick == 0 || this.currentTick != tick) {
            this.tnt = 1;
        } else {
            this.tnt++;

            final double lowestVelocity = user.getWorld().getEntityList().stream()
                    .filter(entity -> EntityConditions.named("sand").test(entity))
                    .mapToDouble(entity -> Math.abs(entity.momentum.y()))
                    .min()
                    .orElse(1.0);

            if (this.tnt == 100) {
                final double ratio = this.tnt / lowestVelocity;
                user.putData(EntityDataKeys.HAMMER_RATIO, ratio);
                return ratio;
            }
        }

        this.currentTick = tick;
        return value;
    }
}
