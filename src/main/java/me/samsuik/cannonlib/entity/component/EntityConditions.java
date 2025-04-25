package me.samsuik.cannonlib.entity.component;

import me.samsuik.cannonlib.World;
import me.samsuik.cannonlib.block.Block;
import me.samsuik.cannonlib.block.Blocks;
import me.samsuik.cannonlib.entity.Entity;
import me.samsuik.cannonlib.physics.vec3.Vec3d;
import me.samsuik.cannonlib.physics.vec3.Vec3i;

import java.util.function.Predicate;

public final class EntityConditions {
    public static final Predicate<Entity> HAS_FLOATING_POINT_ISSUES = entity -> {
        // [4, 5, 6] are problematic exponents
        final double position = entity.position.y();
        for (int exponent = 16; exponent <= 64; exponent *= 2) {
            final double blockCoord = exponent - 1;
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
    public static final Predicate<Entity> CAN_STACK = entity -> {
        if (!entity.onGround) {
            return false;
        }

        final World world = entity.getWorld();
        final Vec3i blockPos = entity.position.toVec3i();
        final Block presentBlock = world.getBlockAt(blockPos);
        if (presentBlock == Blocks.MOVING_PISTON) {
            return false;
        }

        // It would be too intensive to look for entity/global collisions to stack on.
        // As a compromise you have to "opt in" to falling blocks breaking when trying to stack midair.
        final Block belowBlock = world.getBlockAt(blockPos.add(0, -1, 0));
        return (belowBlock == null || !belowBlock.replace()) && (presentBlock == null || presentBlock.replace());
    };

    public static Predicate<Entity> clipY(final double y, final double margin) {
        return entity -> entity.position.getY() >= y - margin;
    }

    public static Predicate<Entity> hasEntityMoved(final Vec3d position) {
        return entity -> !entity.position.equals(position);
    }
}
