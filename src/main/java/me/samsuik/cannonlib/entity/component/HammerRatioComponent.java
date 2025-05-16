package me.samsuik.cannonlib.entity.component;

import me.samsuik.cannonlib.component.DataComponent;
import me.samsuik.cannonlib.entity.Entity;
import me.samsuik.cannonlib.entity.EntityDataKeys;

public final class HammerRatioComponent implements DataComponent<Entity, Double> {
    private int explosions = 1;
    private int currentTick = 0;
    private final int hammerTnt;

    public HammerRatioComponent(final int hammerTnt) {
        this.hammerTnt = hammerTnt;
    }

    @Override
    public Double data(final Entity user, final int tick, final Double value) {
        if (tick == 0 || this.currentTick != tick) {
            this.explosions = 1;
        } else if (++this.explosions == this.hammerTnt) {
            final double lowestVelocity = user.getWorld().getEntityList().stream()
                    .filter(entity -> EntityConditions.named("sand").test(entity))
                    .mapToDouble(entity -> Math.abs(entity.momentum.y()))
                    .min()
                    .orElse(1.0);

            final double ratio = this.explosions / lowestVelocity;
            user.putData(EntityDataKeys.HAMMER_RATIO, ratio);
            return ratio;
        }

        this.currentTick = tick;
        return value;
    }
}
