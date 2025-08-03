package me.samsuik.cannonlib.examples;

import me.samsuik.cannonlib.World;
import me.samsuik.cannonlib.entity.Entity;
import me.samsuik.cannonlib.entity.component.EntityComponents;
import me.samsuik.cannonlib.entity.component.EntityConditions;
import me.samsuik.cannonlib.entity.helpers.CannonRatio;
import me.samsuik.cannonlib.entity.helpers.ClippingBlocks;
import me.samsuik.cannonlib.entity.helpers.StackHeight;
import me.samsuik.cannonlib.physics.vec3.Vec3i;

import java.util.ArrayList;
import java.util.List;

public final class CannonRatioExample {
    private static final String RATIO = """
            // "1.2s 384 4os-osrb" by canabutter (https://discord.com/channels/778715854513635359/1373025771660251176/1373025771660251176) 
            Sand Power      Amount: -    Tick: 0
              rev           Amount: 1    Tick: 9
              sand          Amount: 381  Tick: 10+
            
            Hammer Power    Amount: -    Tick: 4
              hybrid sand   Amount: 1    Tick: 4-8
              hammer        Amount: 464  Tick: 10
              stopper       Amount: 7    Tick: 15
              os sand       Amount: 4    Tick: 15-16
              os hammer     Amount: 11   Tick: 17
            """;

    public static void main(String[] args) {
        // Create a new world using the cannon ratio preset
        final World world = CannonRatio.createWorldWithRatio(
                RATIO,
                true,
                318,
                List.of(
                        EntityComponents.LOGGER
                                .state(false) // disabled logging
                                .oncePerTick()
                                .condition(EntityConditions.named("hammer"))
                )
        );

        final Vec3i guiderPos = new Vec3i(0, 318, 0);
        final World snapshot = world.snapshot();
        final List<Entity> entities = new ArrayList<>(snapshot.getEntityList());
        snapshot.keepTicking();

        final List<StackHeight> stackHeights = StackHeight.getStackHeights(entities);
        final int highestSandStacked = stackHeights.stream()
                .mapToInt(stackHeight -> stackHeight.top() + 64)
                .max()
                .orElse(0);

        System.out.println("Stacked " + highestSandStacked + " sand");

        // Log the sand stacks
        for (final StackHeight stackHeight : stackHeights) {
            System.out.println(stackHeight.information(false));
        }

        // Log any clips
        for (final ClippingBlocks.ClippedBlock clip : ClippingBlocks.getClippingBlocks(world, guiderPos, guiderPos, stackHeights, true)) {
            System.out.println(clip.information(guiderPos, false));
        }
    }
}
