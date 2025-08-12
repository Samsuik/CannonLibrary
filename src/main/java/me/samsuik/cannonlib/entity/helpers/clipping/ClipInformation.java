package me.samsuik.cannonlib.entity.helpers.clipping;

import me.samsuik.cannonlib.entity.helpers.StackHeight;
import me.samsuik.cannonlib.physics.vec3.Vec3i;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public record ClipInformation(List<ClippedBlock> clips, List<StackHeight> stackHeights, WallState wallState, int stackTop) {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClipInformation.class);

    public Severity severity(final Vec3i guiderPos) {
        return this.clips.stream()
                .map(clip -> clip.severity(guiderPos, this.stackTop))
                .min(Severity::compareTo)
                .orElse(Severity.NONE);
    }

    public String otherInformation(final Vec3i guiderPos) {
        final StringBuilder builder = new StringBuilder();
        builder.append("Guider-Y: %s\n".formatted(guiderPos.y()));

        if (!this.stackHeights.isEmpty()) {
            builder.append("Stacked: %sx\n".formatted(this.stackTop + 64));
            for (final StackHeight height : this.stackHeights) {
                builder.append("-> %s\n".formatted(height.information(false)));
            }
        }

        if (!this.clips.isEmpty()) {
            final long severeClips = this.clips.stream()
                    .map(clippedBlock -> clippedBlock.severity(guiderPos, this.stackTop))
                    .filter(severity -> severity == Severity.SEVERE)
                    .count();

            builder.append("Clips: %sx\n".formatted(severeClips));
            for (final ClippedBlock clippedBlock : this.clips) {
                builder.append("-> %s\n".formatted(clippedBlock.information(guiderPos, this.stackTop)));
            }
        }

        builder.append("Details:\n");

        final int stackedFromBarrel = this.stackTop - (guiderPos.y() - 1);
        final String stackInfo;
        if (stackedFromBarrel > 0) {
            stackInfo = "Overstacked: " + stackedFromBarrel + "x";
        } else if (stackedFromBarrel == 0){
            stackInfo = "Barrel stacked";
        } else if (stackedFromBarrel == -1) {
            stackInfo = "Stacked one below barrel height";
        } else {
            stackInfo = "Understacked: " + (-stackedFromBarrel) + "x";
        }

        builder.append(stackInfo).append("\n");

        if (wallState.isWaterBelowGuider()) {
            builder.append("Pushed water at or below barrel height");
        }

        return builder.toString();
    }
}
