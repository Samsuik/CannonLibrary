package me.samsuik.cannonlib.explosion;

public final class ExplosionFlags {
    public static final int DESTROY_BLOCKS       = 0b000001;
    public static final int OBSTRUCTION          = 0b000010;
    public static final int CONSISTENT_RADIUS    = 0b000100;
    public static final int ENTITIES_UP_TO_COUNT = 0b001000;
    public static final int SINGLE_IMPACT        = 0b010000;
    public static final int PAPER_OPTIMISE_EXP   = 0b100000;
    public static final int HANDLE_BLOCKS        = DESTROY_BLOCKS | OBSTRUCTION;
    public static final int BATCH_SWINGING       = ENTITIES_UP_TO_COUNT | SINGLE_IMPACT;

    public static int data(final int extra) {
        return (extra & 0xffff) << 16;
    }

    public static int writeData(final int flags, final int extra) {
        return flags | data(extra);
    }

    public static int readData(final int flags) {
        return flags >>> 16;
    }

    public static int readData(final int flags, final int def) {
        final int data = readData(flags);
        return data != 0 ? data : def;
    }

    public static boolean hasData(final int flags) {
        return readData(flags) != 0;
    }
}
