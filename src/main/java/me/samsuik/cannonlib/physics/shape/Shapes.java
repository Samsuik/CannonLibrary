package me.samsuik.cannonlib.physics.shape;

import me.samsuik.cannonlib.physics.vec3.Vec3d;

import java.util.List;

public final class Shapes {
    public static final BlockShape EMPTY_SHAPE = new BlockShape(List.of());
    public static final Shape FULL_BLOCK = block(0, 0, 0);
    public static final Shape LADDER = Shapes.box(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
    public static final Shape HANGING_SIGN = Shapes.column(16.0, 4.0, 14.0, 16.0);
    public static final Shape TOP_TRAPDOOR = Shapes.column(16.0, 16.0, 13.0, 16.0);
    public static final Shape BOTTOM_TRAPDOOR = Shapes.column(16.0, 16.0, 0, 3.0);
    public static final Shape SIDE_TRAPDOOR = TOP_TRAPDOOR.rotate(90, 0);
    public static final Shape SLAB = Shapes.column(16.0, 16.0, 0.0, 8.0);
    public static final BlockShape STAIR = new BlockShape(List.of(
            Shapes.column(16.0, 16.0, 0.0, 8.0),
            Shapes.box(0.0, 8.0, 0.0, 16.0, 16.0, 8.0)
    ));
    public static final BlockShape LANTERN = new BlockShape(List.of(
            Shapes.box(5.0, 0.0, 5.0, 11.0, 7.0, 11.0),
            Shapes.box(6.0, 7.0, 6.0, 10.0, 9.0, 10.0)
    ));
    public static final BlockShape HANGING_LANTERN = new BlockShape(List.of(
            Shapes.box(5.0, 1.0, 5.0, 11.0, 8.0, 11.0),
            Shapes.box(6.0, 8.0, 6.0, 10.0, 10.0, 10.0)
    ));
    public static final BlockShape FLOOR_GRINDSTONE = new BlockShape(List.of(
            Shapes.box(2.0, 0.0, 6.0, 4.0, 7.0, 10.0),
            Shapes.box(12.0, 0.0, 6.0, 14.0, 7.0, 10.0),
            Shapes.box(2.0, 7.0, 5.0, 4.0, 13.0, 11.0),
            Shapes.box(12.0, 7.0, 5.0, 14.0, 13.0, 11.0),
            Shapes.box(4.0, 4.0, 2.0, 12.0, 16.0, 14.0)
    ));
    public static final BlockShape WALL_GRINDSTONE = new BlockShape(List.of(
            Shapes.box(2.0, 6.0, 7.0, 4.0, 10.0, 16.0),
            Shapes.box(12.0, 6.0, 7.0, 14.0, 10.0, 16.0),
            Shapes.box(2.0, 5.0, 3.0, 4.0, 11.0, 9.0),
            Shapes.box(12.0, 5.0, 3.0, 14.0, 11.0, 9.0),
            Shapes.box(4.0, 2.0, 0.0, 12.0, 14.0, 12.0)
    ));
    public static final BlockShape CEILING_GRINDSTONE = new BlockShape(List.of(
            Shapes.box(2.0, 9.0, 6.0, 4.0, 16.0, 10.0),
            Shapes.box(12.0, 9.0, 6.0, 14.0, 16.0, 10.0),
            Shapes.box(2.0, 3.0, 5.0, 4.0, 9.0, 11.0),
            Shapes.box(12.0, 3.0, 5.0, 14.0, 9.0, 11.0),
            Shapes.box(4.0, 0.0, 2.0, 12.0, 12.0, 14.0)
    ));
    public static final Shape AMETHYST_CLUSTER = Shapes.box(3.0f, 3.0f, 9.0f, 13.0f, 13.0f, 16.0);
    public static final Shape CEILING_AMETHYST_CLUSTER = AMETHYST_CLUSTER.rotate(90, 0);
    public static final Shape LARGE_AMETHYST_BUD = Shapes.box(3.0, 3.0, 11.0, 13.0, 13.0, 16.0);
    public static final Shape MEDIUM_AMETHYST_BUD = Shapes.box(3.0, 3.0, 12.0, 13.0, 13.0, 16.0);
    public static final Shape SMALL_AMETHYST_BUD = Shapes.box(4.0, 4.0, 13.0, 12.0, 12.0, 16.0);
    public static final Shape AGE_0_COCOA = Shapes.box(6.0D, 7.0D, 1.0D, 10.0D, 12.0D, 5.0D);
    public static final Shape AGE_1_COCOA = Shapes.box(5.0D, 5.0D, 1.0D, 11.0D, 12.0D, 7.0D);
    public static final Shape AGE_2_COCOA = Shapes.box(4.0D, 3.0D, 1.0D, 12.0D, 12.0D, 9.0D);
    public static final Shape FLOOR_BELL = Shapes.box(0.0D, 0.0D, 4.0D, 16.0D, 16.0D, 12.0D);
    public static final BlockShape WALL_BELL = new BlockShape(List.of(
            Shapes.box(5.0D, 6.0D, 5.0D, 11.0D, 13.0D, 11.0D),
            Shapes.box(4.0D, 4.0D, 4.0D, 12.0D, 6.0D, 12.0D),
            Shapes.box(7.0D, 13.0D, 0.0D, 9.0D, 15.0D, 13.0D)
    ));
    public static final BlockShape BETWEEN_WALL_BELL = new BlockShape(List.of(
            Shapes.box(5.0D, 6.0D, 5.0D, 11.0D, 13.0D, 11.0D),
            Shapes.box(4.0D, 4.0D, 4.0D, 12.0D, 6.0D, 12.0D),
            Shapes.box(7.0D, 13.0D, 0.0D, 9.0D, 15.0D, 16.0D)
    ));
    public static final BlockShape CEILING_BELL = new BlockShape(List.of(
            Shapes.box(5.0D, 6.0D, 5.0D, 11.0D, 13.0D, 11.0D),
            Shapes.box(4.0D, 4.0D, 4.0D, 12.0D, 6.0D, 12.0D),
            Shapes.box(7.0D, 13.0D, 7.0D, 9.0D, 16.0D, 9.0D)
    ));
    public static final Shape CONDUIT = Shapes.box(5.0, 5.0, 5.0, 11.0, 11.0, 11.0);
    public static final Shape POINTED_DRIPSTONE = Shapes.box(5.0D, 0.0D, 5.0D, 11.0D, 11.0D, 11.0D);
    public static final Shape CHAINS = Shapes.box(6.5, 6.5, 0.0, 9.5, 9.5, 16.0);
    public static final Shape RODS = Shapes.box(6.0, 6.0, 0.0, 10.0, 10.0, 16.0);
    public static final Shape GENERIC_SKULL = Shapes.box(4.0, 0.0, 4.0, 12.0, 8.0, 12.0);
    public static final Shape PIGLIN_SKULL = Shapes.box(3.0, 0.0, 3.0, 13.0, 8.0, 13.0);
    public static final Shape WALL_SKULL = Shapes.box(4.0, 4.0, 8.0, 12.0, 12.0, 16.0);
    public static final Shape CHEST = Shapes.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);
    public static final Shape HONEY = Shapes.box(1.0, 0.0, 1.0, 15.0, 15.0, 15.0);
    public static final BlockShape ANVIL = new BlockShape(List.of(
            Shapes.box(2.0, 0.0, 2.0, 14.0, 4.0, 14.0),
            Shapes.box(4.0, 4.0, 3.0, 12.0, 5.0, 13.0),
            Shapes.box(6.0, 5.0, 4.0, 10.0, 10.0, 12.0),
            Shapes.box(3.0, 10.0, 0.0, 13.0, 16.0, 16.0)
    ));
    public static final Shape GLASS_PANE = Shapes.column(2.0, 2.0, 0.0, 16.0);
    public static final Shape FENCE_GATE = Shapes.box(0.0D, 0.0D, 6.0D, 16.0D, 24.0D, 10.0D);
    public static final Shape FENCE = Shapes.column(4.0, 4.0, 0.0, 24.0);
    public static final Shape WALL = Shapes.column(8.0, 8.0, 0.0, 24.0);
    public static final Shape CANDLE_0 = Shapes.box(7.0, 0.0, 7.0, 9.0, 6.0, 9.0);
    public static final Shape CANDLE_1 = Shapes.box(5.0, 0.0, 6.0, 11.0, 6.0, 9.0);
    public static final Shape CANDLE_2 = Shapes.box(5.0, 0.0, 6.0, 10.0, 6.0, 11.0);
    public static final Shape CANDLE_3 = Shapes.box(5.0, 0.0, 5.0, 11.0, 6.0, 10.0);
    public static final Shape MUD = Shapes.column(16.0, 16.0, 0.0, 14.0);
    public static final Shape BIG_DRIPLEAF_NONE = Shapes.box(0.0D, 11.0D, 0.0D, 16.0D, 15.0D, 16.0D);
    public static final Shape BIG_DRIPLEAF_PARTIAL = Shapes.box(0.0D, 11.0D, 0.0D, 16.0D, 13.0D, 16.0D);
    public static final Shape POWDER_SNOW = Shapes.box(0.0, 0.0, 0.0, 1.0, 0.9F, 1.0);

    public static BlockShape asBlockShape(final Shape shape) {
        if (shape instanceof BlockShape blockShape) {
            return blockShape;
        } else {
            return new BlockShape(List.of(shape));
        }
    }

    public static AABB entityBoundingBox(final Vec3d pos, final float entitySize) {
        final float half = entitySize / 2.0f;
        return new AABB(pos.sub(half, 0.0, half), pos.add(half, entitySize, half));
    }

    public static AABB withSize(
            final double x, final double y, final double z,
            final int sizeX, final int sizeY, final int sizeZ
    ) {
        return new AABB(
                x - sizeX, y - sizeY, z - sizeZ,
                x + sizeX, y + sizeY, z + sizeZ
        );
    }

    public static AABB floor(final double y) {
        return new AABB(
                Double.NEGATIVE_INFINITY,
                y - 1,
                Double.NEGATIVE_INFINITY,
                Double.POSITIVE_INFINITY,
                y,
                Double.POSITIVE_INFINITY
        );
    }

    public static AABB plane(final double x, final double y, final double z) {
        return new AABB(
                x == 0.0 ? Double.NEGATIVE_INFINITY : x,
                y == 0.0 ? Double.NEGATIVE_INFINITY : y,
                z == 0.0 ? Double.NEGATIVE_INFINITY : z,
                x == 0.0 ? Double.POSITIVE_INFINITY : x,
                y == 0.0 ? Double.POSITIVE_INFINITY : y,
                z == 0.0 ? Double.POSITIVE_INFINITY : z
        );
    }

    public static AABB block(final int x, final int y, final int z) {
        return new AABB(x, y, z, x + 1, y + 1, z + 1);
    }

    public static AABB box(
            final double px1, final double py1, final double pz1,
            final double px2, final double py2, final double pz2
    ) {
        return new AABB(
                px1 / 16.0,
                py1 / 16.0,
                pz1 / 16.0,
                px2 / 16.0,
                py2 / 16.0,
                pz2 / 16.0
        );
    }

    public static AABB column(
            final double width, final double length,
            final double minY, final double maxY
    ) {
        final double halfWidth = width / 2.0;
        final double halfLength = length / 2.0;
        return box(
                8.0 - halfWidth,
                minY,
                8.0 - halfWidth,
                8.0 + halfLength,
                maxY,
                8.0 + halfLength
        );
    }
}
