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

public final class CannonRatioExample {
    private static final String RATIO = """
            // "1.2s 384 4os-osrb" by canabutter (https://discord.com/channels/778715854513635359/1373025771660251176/1373025771660251176) 
            // modified to remove the osrb
            Sand Power      Amount: -    Tick: 0
              rev           Amount: 1    Tick: 9
              sand          Amount: 381  Tick: 10+

            Hammer Power    Amount: -    Tick: 4
              hybrid sand   Amount: 1    Tick: 4-8
              hammer        Amount: 458  Tick: 10
              stopper       Amount: 13   Tick: 16
              os sand       Amount: 4    Tick: 16-17
              os hammer     Amount: 8    Tick: 18
              scatter       Amount: 10   Tick: 24
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

        final ClipInformation clipInfo = ClippingBlocks.getClipInformation(world, GUIDER, true);
        System.out.println(clipInfo.otherInformation(GUIDER));
    }
}
