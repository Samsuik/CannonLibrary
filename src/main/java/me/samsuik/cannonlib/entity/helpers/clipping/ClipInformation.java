package me.samsuik.cannonlib.entity.helpers.clipping;

import me.samsuik.cannonlib.entity.helpers.StackHeight;
import me.samsuik.cannonlib.physics.vec3.Vec3i;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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

        builder.append("Details:\n");
        for (final String detail : this.listAllDetails(guiderPos)) {
            builder.append("-> %s\n".formatted(detail));
        }

        if (!this.clips.isEmpty()) {
            final long severeClips = this.clips.stream()
                    .map(clippedBlock -> clippedBlock.severity(guiderPos, this.stackTop))
                    .filter(severity -> severity == Severity.SEVERE)
                    .count();

            builder.append("Clips (Severe: %sx, Minor: %sx):\n".formatted(severeClips, this.clips.size() - severeClips));
            for (final ClippedBlock clippedBlock : this.clips) {
                builder.append("-> %s\n".formatted(clippedBlock.information(guiderPos, this.stackTop)));
            }
        }

        return builder.toString();
    }

    private List<String> listAllDetails(final Vec3i guiderPos) {
        final List<String> details = new ArrayList<>();

        final int stackedFromBarrel = this.stackTop - (guiderPos.y() - 1);
        final Severity severity = this.severity(guiderPos);
        final boolean clipped = this.severity(guiderPos) == Severity.SEVERE;
        if (clipped) {
            details.add("Clipped");
        } else if (wallState.pushedWater() || stackedFromBarrel >= 0) {
            if (severity != Severity.MODERATE) {
                details.add("Unclippable");
            } else if (wallState.pushedWater()) {
                details.add("Breaks clips");
            }
        }

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

        details.add(stackInfo);

        if (wallState.pushedWater()) {
            details.add("OSRB");
        }

        if (wallState.isWaterBelowGuider()) {
            details.add("Pushed water at or below barrel height");
        }

        return details;
    }
}
