package me.samsuik.cannonlib.examples;

import me.samsuik.cannonlib.World;
import me.samsuik.cannonlib.block.Blocks;
import me.samsuik.cannonlib.entity.component.EntityComponents;
import me.samsuik.cannonlib.entity.component.EntityConditions;
import me.samsuik.cannonlib.entity.helpers.CannonRatio;
import me.samsuik.cannonlib.entity.helpers.clipping.ClipInformation;
import me.samsuik.cannonlib.entity.helpers.clipping.ClippingBlocks;
import me.samsuik.cannonlib.physics.vec3.Vec3i;

import java.util.List;

public final class OSRBClippingExample {
    private static final String RATIO = """
            // "0.7 384 osrb 1 above barrel" (https://discord.com/channels/778715854513635359/1404593768493219871)
            0 sand power
            5 1rev
            10 sand (384)
            3 hammer power
            4 one shot sand
            6 hammer (320)
            7 os sand (5)
            8 splitter tnt (4)
            9 os hammer (15)
            """;
    private static final Vec3i GUIDER = new Vec3i(0, 185, 0);

    public static void main(String[] args) {
        // Create a new world using the cannon ratio preset
        final World world = CannonRatio.createWorldWithRatio(
                RATIO,
                false,
                GUIDER.y(),
                List.of(
                        EntityComponents.LOGGER
                                .state(false) // disabled logging
                                .oncePerTick()
                                .condition(EntityConditions.named("scatter"))
                )
        );

        for (int y = GUIDER.y() - 20; y < GUIDER.y() + 20; ++y) {
            world.setBlock(new Vec3i(60, y, 0), Blocks.COBBLESTONE, false);
            world.setBlock(new Vec3i(59, y, 0), Blocks.WATER, false);
            world.setBlock(new Vec3i(59, y, 1), Blocks.WATER, false);
        }

        final ClipInformation clipInfo = ClippingBlocks.getClipInformation(world, GUIDER, true);
        System.out.println(clipInfo.otherInformation(GUIDER));
    }
}
