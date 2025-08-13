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
            Sand Power      Amount: -    Tick: 0
              sand          Amount: 381  Tick: -
            
            Hammer Power    Amount: -    Tick: 4
              hammer        Amount: 396  Tick: 4
              hybrid sand   Amount: 1    Tick: 4 (rendered after power)
              scatter       Amount: 4    Tick: 5
              swing 1       Amount: 2    Tick: 6
              swing 2       Amount: 57   Tick: 7
              osrb sand     Amount: 9    Tick: 7
              osrb hammer   Amount: 23   Tick: 8
              scatter       Amount: -    Tick: 14
            """;
    private static final Vec3i GUIDER = new Vec3i(0, 310, 0);

    public static void main(String[] args) {
        // Create a new world using the cannon ratio preset
        final World world = CannonRatio.createWorldWithRatio(
                RATIO,
                true,
                GUIDER.y(),
                List.of(
                        EntityComponents.LOGGER
                                .state(false) // disabled logging
                                .oncePerTick()
                                .condition(EntityConditions.named("scatter"))
                )
        );

        for (int y = 300; y < 340; ++y) {
            world.setBlock(new Vec3i(60, y, 0), Blocks.COBBLESTONE, false);
            world.setBlock(new Vec3i(59, y, 0), Blocks.WATER, false);
            world.setBlock(new Vec3i(59, y, 1), Blocks.WATER, false);
        }

        final ClipInformation clipInfo = ClippingBlocks.getClipInformation(world, GUIDER, true);
        System.out.println(clipInfo.otherInformation(GUIDER));
    }
}
