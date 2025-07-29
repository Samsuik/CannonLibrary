package me.samsuik.cannonlib.entity.helpers;

public final class CannonRatioTemplates {
    public static final String UNIVERSAL_SCATTER = """
                        scatter-1 tick: 40 (20)
                        scatter-2 tick: 55 (20)
                        scatter-3 tick: 70 (20)
                        scatter-4 tick: 75 (20)
                        scatter-5 tick: 79 (20)
                        """;

    public static String simple1RevRatio(
            final int powerTicks,
            final int oneRevTicks,
            final int hammerTicks,
            final int hammerAmount
    ) {
        return """
               sand power tick: 0
               sand tick: 100 (384)
               1rev tick: %s
               hammer power tick: %s
               hammer tick: %s (%s)
               """.formatted(oneRevTicks, powerTicks, hammerTicks, hammerAmount);
    }
}
