package me.samsuik.cannonlib.entity.component;

import me.samsuik.cannonlib.World;
import me.samsuik.cannonlib.component.Component;
import me.samsuik.cannonlib.entity.Entity;
import me.samsuik.cannonlib.entity.data.DataKeys;
import me.samsuik.cannonlib.explosion.Explosion;
import me.samsuik.cannonlib.explosion.ExplosionFlags;
import me.samsuik.cannonlib.explosion.Obstruction;
import me.samsuik.cannonlib.physics.vec3.Vec3d;
import me.samsuik.cannonlib.physics.vec3.Vec3i;

import java.util.Set;

public final class ExplodeComponent implements Component<Entity> {
    private static final ThreadLocal<Boolean> TICKING = ThreadLocal.withInitial(() -> false);
    private final int fuse;
    private final int amount;
    private final int flags;

    public ExplodeComponent(final int fuse, final int amount, final int flags) {
        this.fuse = fuse;
        this.amount = amount;
        this.flags = flags;
    }

    @Override
    public void action(final Entity entity, final int tick) {
        if (tick < this.fuse || TICKING.get()) {
            return;
        }

        final World world = entity.getWorld();
        final Obstruction obstruction = new Obstruction();
        for (int count = this.amount - 1; count >= 0; --count) {
            final Set<Vec3i> blocksToExplode = Explosion.explode(entity, obstruction, this.amount - count, flags);

            if (count != 0 || entity.getData(DataKeys.REPEAT)) {
                // Handle swinging
                final Vec3d entityPosition = entity.getEntityState().position();
                final Vec3d explosionPosition = Explosion.explosionPosition(entity.position);
                final Vec3d swing = Explosion.impact(entityPosition, explosionPosition, world, obstruction, flags);
                entity.getEntityState().apply(entity);
                entity.momentum = entity.momentum.add(swing);

                TICKING.set(true);
                entity.tick();
                TICKING.set(false);
            }

            // Blow up blocks
            for (final Vec3i blockPos : blocksToExplode) {
                world.removeBlock(blockPos);
            }

            if (!blocksToExplode.isEmpty() && (flags & ExplosionFlags.PAPER_OPTIMISE_EXP) == 0) {
                obstruction.invalidate();
            }
        }

        entity.putData(DataKeys.EXPLODED, true);
        entity.removeCurrentComponent();
        entity.remove();
    }
}
