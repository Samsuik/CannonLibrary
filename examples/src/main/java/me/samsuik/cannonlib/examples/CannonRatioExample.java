package me.samsuik.cannonlib.examples;

import me.samsuik.cannonlib.World;
import me.samsuik.cannonlib.block.Blocks;
import me.samsuik.cannonlib.entity.helpers.CannonRatio;
import me.samsuik.cannonlib.entity.helpers.clipping.ClipInformation;
import me.samsuik.cannonlib.entity.helpers.clipping.ClippingBlocks;
import me.samsuik.cannonlib.physics.vec3.Vec3i;

import java.util.List;

public final class CannonRatioExample {
    private static final String RATIO = """
            // "1.2s 384 4os-osrb" by canabutter (https://discord.com/channels/778715854513635359/1373025771660251176/1373025771660251176) 
            Sand Power      Amount: -    Tick: 0.0
              sand          Amount: 381  Tick: 8.3
              rev           Amount: 10   Tick: 7.1
            
            Hammer Power    Amount: -    Tick: 4.0
              hammer        Amount: 561  Tick: 8.1
              hybrid sand 1 Amount: 2    Tick: 6.3
              hybrid sand 2 Amount: 1    Tick: 7.3
              osrb sand     Amount: 9    Tick: 8.3
              osrb hammer   Amount: 31   Tick: 9.1
              scatter       Amount: 6    Tick: 15
            """;

    public static void main(String[] args) {
        // Create a new world using the cannon ratio preset
        final World world = CannonRatio.createWorldWithRatio(
                RATIO,
                true,
                310,
                List.of(
//                        EntityComponents.LOGGER
//                                .state(false) // disabled logging
//                                .oncePerTick()
//                                .condition(EntityConditions.named("scatter"))
                )
        );

        for (int y = 300; y < 340; ++y) {
            world.setBlock(new Vec3i(60, y, 0), Blocks.COBBLESTONE, false);
            world.setBlock(new Vec3i(59, y, 0), Blocks.WATER, false);
            world.setBlock(new Vec3i(59, y, 1), Blocks.WATER, false);
        }

//        world.setBlock(new Vec3i(59, 310, 0), Blocks.COBBLESTONE, false);

//        world.keepTicking();

//        for (int x = 0; x < 5; ++x) {
//            world.tick();
//        }

        final Vec3i guiderPos = new Vec3i(0, 310, 0);
        final ClipInformation clipInfo = ClippingBlocks.getClipInformation(world, guiderPos, false);

        System.out.println(clipInfo.otherInformation(guiderPos));

//        System.out.println("Stacked " + (clipInfo.stackTop() + 64) + " sand");
//
//        // Log the sand stacks
//        for (final StackHeight stackHeight : clipInfo.stackHeights()) {
//            System.out.println(stackHeight.information(false));
//        }
//
//        // Log any clips and irregularities
//        for (final ClippedBlock clip : clipInfo.clips()) {
//            System.out.println(clip.information(guiderPos));
//        }
    }
}
