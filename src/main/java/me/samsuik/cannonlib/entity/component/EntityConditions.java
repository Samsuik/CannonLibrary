package me.samsuik.cannonlib.entity.component;

import me.samsuik.cannonlib.World;
import me.samsuik.cannonlib.block.Block;
import me.samsuik.cannonlib.block.Blocks;
import me.samsuik.cannonlib.data.DataKey;
import me.samsuik.cannonlib.entity.Entity;
import me.samsuik.cannonlib.entity.EntityDataKeys;
import me.samsuik.cannonlib.physics.vec3.Vec3d;
import me.samsuik.cannonlib.physics.vec3.Vec3i;

import java.util.Locale;
import java.util.function.Predicate;

public final class EntityConditions {
    public static final Predicate<Entity> HAS_FLOATING_POINT_ISSUES = entity -> {
        // [4, 5, 6] are problematic exponents
        final double position = entity.position.y();
        for (int exponent = 4; exponent <= 6; ++exponent) {
            final double blockCoord = (1 << exponent) - 1;
            final double movement = position + (-blockCoord - position);
            if ((movement + blockCoord) < 0.0) {
                return true;
            }
        }
        return false;
    };

    public static final Predicate<Entity> IS_ON_GROUND = entity -> entity.onGround;
    public static final Predicate<Entity> IS_ALIVE = entity -> !entity.shouldRemove();
    public static final Predicate<Entity> HAS_MOMENTUM = entity -> entity.momentum.magnitudeSquared() > 0.0;
    public static final Predicate<Entity> HAS_STACKED = entity -> entity.hasData(EntityDataKeys.STACKED);
    public static final Predicate<Entity> HAS_EXPLODED = entity -> entity.hasData(EntityDataKeys.EXPLODED);

    public static final Predicate<Entity> CAN_STACK = entity -> {
        if (!entity.onGround) {
            return false;
        }

        final World world = entity.getWorld();
        final Vec3i blockPos = entity.position.toVec3i();
        final Block presentBlock = world.getBlockAtRaw(blockPos);
        if (presentBlock == Blocks.MOVING_PISTON) {
            return false;
        }

        final Block belowBlock = world.getBlockAtRaw(blockPos.down());
        return belowBlock != Blocks.AIR && (presentBlock == null || presentBlock.replace());
    };

    public static Predicate<Entity> clipY(final double y) {
        return clipY(y, 1.0e-7);
    }

    public static Predicate<Entity> clipY(final double y, final double margin) {
        return entity -> {
            final double pos = entity.position.y();
            return pos >= y - margin && pos < y;
        };
    }

    public static Predicate<Entity> above(final double y) {
        return entity -> entity.position.getY() >= y;
    }

    public static Predicate<Entity> hasEntityMoved(final Vec3d position) {
        return entity -> !entity.position.equals(position);
    }

    public static Predicate<Entity> named(final String name) {
        return entity -> {
            final String entityName = entity.getDataOrDefault(EntityDataKeys.NAME, "");
            return entityName.toLowerCase(Locale.ROOT).contains(name);
        };
    }

    public static Predicate<Entity> hasData(final DataKey<?> dataKey) {
        return entity -> entity.hasData(dataKey);
    }

    public static Predicate<Entity> isDataTrue(final DataKey<Boolean> dataKey) {
        return entity -> entity.hasData(dataKey) && entity.getData(dataKey);
    }

    public static <T> Predicate<Entity> data(final DataKey<T> dataKey, final Predicate<T> more) {
        return entity -> entity.hasData(dataKey) && more.test(entity.getData(dataKey));
    }
}
