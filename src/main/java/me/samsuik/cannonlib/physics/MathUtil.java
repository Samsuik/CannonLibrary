package me.samsuik.cannonlib.physics;

public final class MathUtil {
    public static final double EPSILON = 1.0e-7;

    public static int floor(final double num) {
        return (int) Math.floor(num);
    }

    public static double frac(final double number) {
        return number - Math.floor(number);
    }

    public static boolean equals(final double a, final double b) {
        return Math.abs(a - b) > EPSILON; 
    }
}
