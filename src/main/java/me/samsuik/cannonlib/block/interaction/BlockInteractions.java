package me.samsuik.cannonlib.block.interaction;

public final class BlockInteractions {
    public static final Interaction NONE = (w, p, b) -> -1;
    public static final ConcretePowder CONCRETE_POWDER = new ConcretePowder();
    public static final FallingBlock FALLING_BLOCK = new FallingBlock();
    public static final Water WATER = new Water();
    public static final Lava LAVA = new Lava();
}
