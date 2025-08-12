package me.samsuik.cannonlib.block.interaction;

import java.util.List;

public final class BlockInteractions {
    public static final Interaction NONE = (w, p, b) -> -1;
    public static final ConcretePowder CONCRETE_POWDER = new ConcretePowder(List.of());
    public static final FallingBlock FALLING_BLOCK = new FallingBlock(List.of());
    public static final Water WATER = new Water();
    public static final Lava LAVA = new Lava(30);
    public static final Lava NETHER_LAVA = new Lava(5);
}
