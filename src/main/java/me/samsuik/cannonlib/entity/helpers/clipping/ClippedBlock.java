package me.samsuik.cannonlib.entity.helpers.clipping;

import me.samsuik.cannonlib.block.Block;
import me.samsuik.cannonlib.entity.helpers.StackHeight;
import me.samsuik.cannonlib.physics.vec3.Vec3i;

import java.util.*;

public record ClippedBlock(
        Vec3i position,
        Block block,
        WateredState wateredState,
        List<StackHeight> stackHeights,
        WallState wallState,
        int state,
        boolean inconsistent
) {
    public Severity severity(final Vec3i guiderPos, final int originalStackTop) {
        return this.problems(guiderPos, originalStackTop).values().stream()
                .min(Severity::compareTo)
                .orElse(Severity.NONE);
    }

    public String information(final Vec3i guiderPos) {
        return this.information(guiderPos, Integer.MIN_VALUE);
    }

    public String information(final Vec3i guiderPos, final int originalStackTop) {
        final Map<String, Severity> problems = this.problems(guiderPos, originalStackTop);
        if (!problems.isEmpty()) {
            return "\"%s\": %s (%s)".formatted(this.block().name(), this.position.y(), String.join(", ", problems.keySet()));
        }
        return "";
    }

    public Map<String, Severity> problems(final Vec3i guiderPos, final int originalStackTop) {
        final Map<String, Severity> problems = new LinkedHashMap<>();
        final int stackTop = this.stackHeights.stream()
                .mapToInt(StackHeight::top)
                .max()
                .orElse(Integer.MIN_VALUE);

        problems.put(this.wateredState.getFriendlyName(), Severity.NONE);

        if (stackTop < guiderPos.y() - 12) {
            problems.put("clipped", Severity.SEVERE);
        }

        if (this.wallState.destroyedWall() && (state & WallState.DESTROYED_WALL) == 0) {
            problems.put("unable to destroy wall", Severity.SEVERE);
        }

        final boolean stackedUpToClip = stackTop == this.position.y() - 1;
        if (this.wallState.pushedWater()) {
            if ((state & WallState.PUSHED_WATER) == 0) {
                problems.put("unable to push water", stackedUpToClip ? Severity.MODERATE : Severity.SEVERE);
            }

            if (!this.wallState.isWaterBelowGuider() && (state & WallState.PUSHED_WATER_BELOW_GUIDER) != 0) {
                problems.put("pushed water below guider", Severity.SEVERE);
            }
        }

        if (stackedUpToClip) {
            problems.put("stacked up to clip", Severity.NONE);
        }

        if (stackTop < this.position.y() - 1 && stackTop != originalStackTop && originalStackTop != Integer.MIN_VALUE) {
            problems.put("understacked %dx".formatted(this.position.y() - stackTop - 1), Severity.MODERATE);
        }

        if (this.inconsistent) {
            problems.put("inconsistent", Severity.NONE);
        }

        return problems;
    }
}
