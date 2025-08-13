package me.samsuik.cannonlib.block;

import me.samsuik.cannonlib.block.interaction.BlockInteractions;
import me.samsuik.cannonlib.physics.shape.Shapes;

public final class Blocks {
    public static final Block AIR = Block.builder()
            .name("air")
            .strength(-0.3f)
            .replace()
            .build();
    public static final Block COBBLESTONE = Block.from("cobblestone", Shapes.FULL_BLOCK, 6.0f);
    public static final Block END_STONE = Block.from("end_stone", Shapes.FULL_BLOCK, 9.0f);
    public static final Block STONE = Block.from("stone", Shapes.FULL_BLOCK, 6.0f);
    public static final Block OBSIDIAN = Block.from("obsidian", Shapes.FULL_BLOCK, 1200.0f);
    public static final Block SAND = Block.builder()
            .name("sand")
            .shape(Shapes.FULL_BLOCK)
            .strength(0.5f)
            .interaction(BlockInteractions.FALLING_BLOCK)
            .build();
    public static final Block RED_SAND = Block.builder()
            .name("red_sand")
            .shape(Shapes.FULL_BLOCK)
            .strength(0.5f)
            .interaction(BlockInteractions.FALLING_BLOCK)
            .build();
    public static final Block GRAVEL = Block.builder()
            .name("gravel")
            .shape(Shapes.FULL_BLOCK)
            .strength(0.6f)
            .interaction(BlockInteractions.FALLING_BLOCK)
            .build();
    public static final Block CONCRETE_POWDER = Block.builder()
            .name("concrete_powder")
            .shape(Shapes.FULL_BLOCK)
            .strength(0.5f)
            .interaction(BlockInteractions.CONCRETE_POWDER)
            .build();
    public static final Block CONCRETE = Block.from("concrete", Shapes.FULL_BLOCK, 1.6f);
    public static final Block LADDER = Block.from("ladder", Shapes.LADDER, 0.4f);
    public static final Block MOVING_PISTON = Block.from("moving_piston", Shapes.EMPTY_SHAPE, 1200.0f);
    public static final Block BEDROCK = Block.from("bedrock", Shapes.FULL_BLOCK, 1200.0f);
    public static final Block WATER_SOURCE = Block.builder()
            .name("water_source")
            .strength(1200.0f)
            .interaction(BlockInteractions.WATER)
            .replace()
            .build();
    public static final Block WATER = Block.builder()
            .name("water")
            .strength(1200.0f)
            .interaction(BlockInteractions.WATER)
            .replace()
            .build();
    public static final Block LAVA_SOURCE = Block.builder()
            .name("lava_source")
            .strength(1200.0f)
            .interaction(BlockInteractions.LAVA)
            .replace()
            .build();
    public static final Block LAVA = Block.builder()
            .name("lava")
            .strength(1200.0f)
            .interaction(BlockInteractions.LAVA)
            .replace()
            .build();
    public static final Block TOP_TRAPDOOR = Block.from("top_trapdoor", Shapes.TOP_TRAPDOOR, 0.0f);
    public static final Block BOTTOM_TRAPDOOR = Block.from("bottom_trapdoor", Shapes.BOTTOM_TRAPDOOR, 0.0f);
    public static final Block SIDE_TRAPDOOR = Block.from("side_trapdoor", Shapes.SIDE_TRAPDOOR, 0.0f);
    public static final Block HANGING_SIGN = Block.from("hanging_sign", Shapes.HANGING_SIGN, 0.0f);
    public static final Block SLAB = Block.from("slab", Shapes.SLAB, 6.0f);
    public static final Block STAIR = Block.from("stair", Shapes.STAIR, 6.0f);
    public static final Block LANTERN = Block.from("lantern", Shapes.LANTERN, 0.0f);
    public static final Block HANGING_LANTERN = Block.from("hanging_lantern", Shapes.HANGING_LANTERN, 0.0f);
    public static final Block FLOOR_GRINDSTONE = Block.from("floor_grindstone", Shapes.FLOOR_GRINDSTONE, 0.0f);
    public static final Block WALL_GRINDSTONE = Block.from("wall_grindstone", Shapes.WALL_GRINDSTONE, 0.0f);
    public static final Block CEILING_GRINDSTONE = Block.from("ceiling_grindstone", Shapes.CEILING_GRINDSTONE, 0.0f);
    public static final Block CEILING_AMETHYST_CLUSTER = Block.from("ceiling_amethyst_cluster", Shapes.CEILING_AMETHYST_CLUSTER, 0.0f);
    public static final Block AMETHYST_CLUSTER = Block.from("amethyst_cluster", Shapes.AMETHYST_CLUSTER, 0.0f);
    public static final Block LARGE_AMETHYST_BUD = Block.from("large_amethyst_bud", Shapes.LARGE_AMETHYST_BUD, 0.0f);
    public static final Block MEDIUM_AMETHYST_BUD = Block.from("medium_amethyst_bud", Shapes.MEDIUM_AMETHYST_BUD, 0.0f);
    public static final Block SMALL_AMETHYST_BUD = Block.from("small_amethyst_bud", Shapes.SMALL_AMETHYST_BUD, 0.0f);
    public static final Block AGE_0_COCOA = Block.from("cocoa_0", Shapes.AGE_0_COCOA, 0.0f);
    public static final Block AGE_1_COCOA = Block.from("cocoa_1", Shapes.AGE_1_COCOA, 0.0f);
    public static final Block AGE_2_COCOA = Block.from("cocoa_2", Shapes.AGE_2_COCOA, 0.0f);
    public static final Block FLOOR_BELL = Block.from("floor_bell", Shapes.FLOOR_BELL, 0.0f);
    public static final Block WALL_BELL = Block.from("wall_bell", Shapes.WALL_BELL, 0.0f);
    public static final Block BETWEEN_WALL_BELL = Block.from("between_bell", Shapes.BETWEEN_WALL_BELL, 0.0f);
    public static final Block CEILING_BELL = Block.from("ceiling_bell", Shapes.CEILING_BELL, 0.0f);
    public static final Block CONDUIT = Block.from("conduit", Shapes.CONDUIT, 0.0f);
    public static final Block POINTED_DRIPSTONE = Block.from("pointed_dripstone", Shapes.POINTED_DRIPSTONE, 0.0f);
    public static final Block CHAINS = Block.from("chains", Shapes.CHAINS, 0.0f);
    public static final Block RODS = Block.from("rods", Shapes.RODS, 0.0f);
    public static final Block GENERIC_SKULL = Block.from("skull", Shapes.GENERIC_SKULL, 0.0f);
    public static final Block PIGLIN_SKULL = Block.from("piglin_skull", Shapes.PIGLIN_SKULL, 0.0f);
    public static final Block WALL_SKULL = Block.from("wall_skull", Shapes.WALL_SKULL, 0.0f);
    public static final Block CHEST = Block.from("chest", Shapes.CHEST, 0.0f);
    public static final Block HONEY = Block.from("honey", Shapes.HONEY, 0.0f);
    public static final Block ANVIL = Block.from("anvil", Shapes.ANVIL, 0.0f);
    public static final Block GLASS_PANE = Block.from("glass_pane", Shapes.GLASS_PANE, 0.0f);
    public static final Block FENCE_GATE = Block.from("fence_gate", Shapes.FENCE_GATE, 0.0f);
    public static final Block FENCE = Block.from("fence", Shapes.FENCE, 0.0f);
    public static final Block WALL = Block.from("wall", Shapes.WALL, 0.0f);
    public static final Block CANDLE_0 = Block.from("candle_0", Shapes.CANDLE_0, 0.0f);
    public static final Block CANDLE_1 = Block.from("candle_1", Shapes.CANDLE_1, 0.0f);
    public static final Block CANDLE_2 = Block.from("candle_2", Shapes.CANDLE_2, 0.0f);
    public static final Block CANDLE_3 = Block.from("candle_3", Shapes.CANDLE_3, 0.0f);
    public static final Block MUD = Block.from("mud", Shapes.MUD, 0.0f);
    public static final Block BIG_DRIPLEAF_NONE = Block.from("big_dripleaf_none", Shapes.BIG_DRIPLEAF_NONE, 0.0f);
    public static final Block BIG_DRIPLEAF_PARTIAL = Block.from("big_dripleaf_partial", Shapes.BIG_DRIPLEAF_PARTIAL, 0.0f);
    public static final Block COBWEB = Block.builder()
            .name("cobweb")
            .interaction(BlockInteractions.COBWEB)
            .build();
    public static final Block POWDERED_SNOW = Block.builder()
            .name("powdered_snow")
            .interaction(BlockInteractions.POWDERED_SNOW)
            .build();
}
