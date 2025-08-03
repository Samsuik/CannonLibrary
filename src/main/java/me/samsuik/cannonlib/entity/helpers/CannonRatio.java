package me.samsuik.cannonlib.entity.helpers;

import me.samsuik.cannonlib.World;
import me.samsuik.cannonlib.block.Block;
import me.samsuik.cannonlib.block.Blocks;
import me.samsuik.cannonlib.component.Component;
import me.samsuik.cannonlib.entity.Entity;
import me.samsuik.cannonlib.entity.component.EntityComponents;
import me.samsuik.cannonlib.entity.component.EntityConditions;
import me.samsuik.cannonlib.entity.EntityDataKeys;
import me.samsuik.cannonlib.explosion.ExplosionFlags;
import me.samsuik.cannonlib.physics.shape.Shapes;
import me.samsuik.cannonlib.physics.vec3.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CannonRatio {
    private static final Logger LOGGER = LoggerFactory.getLogger(CannonRatio.class);

    private static final int DEFAULT_POWER_TNT = 768;
    private static final Pattern POWER_PATTERN = Pattern.compile("power|p\\d+");
    private static final Pattern RESTACK_PATTERN = Pattern.compile("osrb|restack", Pattern.CASE_INSENSITIVE);
    private static final Pattern CLEAN_UP_PATTERN = Pattern.compile("^(?:#|//).*$|\\([^0-9].*?\\)|/\\s*\\d+|[,/+<>]|(?:Amount|Tick):? *[A-z]+", Pattern.CASE_INSENSITIVE);
    private static final Pattern NAME_PATTERN = Pattern.compile("^(?:[\\d.]+\\s)?((?: ?\\w+)+).*", Pattern.CASE_INSENSITIVE);
    private static final Pattern EXTRACT_RATIO_PATTERN = Pattern.compile("(?:Tick:? *|^\\s*)(?<tick>[\\d.]+)\\b-?|(?:Amount:? *|\\()(?<amount>\\d+)?\\)?-?", Pattern.CASE_INSENSITIVE);

    public static World createWorldWithRatio(
            final String ratioString,
            final boolean useGameTicks,
            final int guider,
            final List<Component<Entity>> extraComponents
    ) {
        final World world = new World();
        // create a floor, guider and wall
        world.addGlobalCollision(Shapes.floor(-64));
        world.addGlobalCollision(Shapes.block(0, guider, 0).expandTo(new Vec3d(-64, 400, 0)));
        world.addGlobalCollision(Shapes.plane(64, 0, 0));

        loadRatioIntoWorld(
                ratioString,
                world,
                Vec3d.center(0, -63, 0),
                useGameTicks,
                ExplosionFlags.HANDLE_BLOCKS,
                Map.of(),
                extraComponents
        );

        return world;
    }

    public static void loadRatioIntoWorld(
            final String ratioString,
            final World world,
            final Vec3d position,
            final boolean useGameTicks,
            final int explosionFlags,
            final Map<String, Vec3d> offsets,
            final List<Component<Entity>> extraComponents
    ) {
        final int[] powerOffset = {-10};
        final PriorityQueue<RatioEntityData> sortedRatioEntityData = new PriorityQueue<>(Comparator.comparingDouble(RatioEntityData::tick));
        final String[] lines = ratioString.split("\n");
        for (final String line : lines) {
            if (line.isEmpty()) {
                continue;
            }

            final RatioEntityData ratioEntityData = readEntityData(line, powerOffset, position, offsets);
            if (ratioEntityData != null) {
                sortedRatioEntityData.add(ratioEntityData);
            } else {
                LOGGER.debug("? malformed {}", ratioString);
            }
        }

        RatioEntityData ratioEntityData;
        while ((ratioEntityData = sortedRatioEntityData.poll()) != null) {
            LOGGER.debug("+ {}", ratioEntityData);
            world.addEntity(ratioEntityData.createEntity(useGameTicks, explosionFlags, extraComponents));
        }
    }

    private static RatioEntityData readEntityData(
            final String line,
            final int[] powerOffset,
            final Vec3d position,
            final Map<String, Vec3d> positions
    ) {
        double tick = -1;
        int amount = -1;

        // Extract the ratio
        final String cleanLine = CLEAN_UP_PATTERN.matcher(line).replaceAll("");
        final Matcher matcher = EXTRACT_RATIO_PATTERN.matcher(cleanLine);
        while (matcher.find()) {
            final String tickGroup = matcher.group("tick");
            final String amountGroup = matcher.group("amount");
            if (tick == -1 && tickGroup != null) {
                tick = Double.parseDouble(tickGroup);
            }
            if (amount == -1 && amountGroup != null) {
                amount = Integer.parseInt(amountGroup);
            }
        }

        if (tick == -1 && amount == -1) {
            return null;
        }

        final String extracted = matcher.replaceAll("").trim();
        final String name = NAME_PATTERN.matcher(extracted).replaceAll("$1");
        final String nameLowercase = name.toLowerCase(Locale.ROOT);

        // Create entity data
        final boolean isPower = POWER_PATTERN.matcher(nameLowercase).find();
        final boolean isSand = !isPower && nameLowercase.contains("sand");

        if (isPower) {
            powerOffset[0] += 10;
        }

        final Vec3d defaultPos = isPower ? new Vec3d(-1, -1, 0) : Vec3d.zero();
        final Vec3d entityPos = positions.getOrDefault(name, defaultPos);
        final Vec3d offsetPos = entityPos.add(position).add(-Math.max(powerOffset[0], 0), 0, 0);

        if (isSand) {
            tick += 1.0e-3;
        } else if (!isPower) {
            tick += 1.0e-5; // always after the power if the ticks are equal
        }

        if (isPower && amount == -1) {
            amount = DEFAULT_POWER_TNT;
        } else {
            amount = Math.abs(amount);
        }

        return new RatioEntityData(name, offsetPos, tick, amount, isPower, isSand);
    }

    private record RatioEntityData(String name, Vec3d position, double tick, int amount, boolean power, boolean sand) {
        public Entity createEntity(final boolean useGameTicks, final int explosionFlags, final List<Component<Entity>> extraComponents) {
            final List<Component<Entity>> components = new ArrayList<>();
            components.add(EntityComponents.spawnWithData(EntityDataKeys.NAME, this.name));

            // Letting the power tick (and as a result swing) might create unexpected behaviour.
            if (!this.power()) {
                components.add(EntityComponents.ENTITY_TICK_WITH_COLLISION
                        .condition(EntityConditions.HAS_MOMENTUM
                                .or(EntityConditions.hasEntityMoved(this.position()))
                        )
                );
            }

            // Add extra components before the fb/explode component so the position and motion are accurate
            components.addAll(extraComponents);

            if (this.sand()) {
                final boolean restack = RESTACK_PATTERN.matcher(this.name).find();
                final Block fallingBlock = restack ? Blocks.GRAVEL : Blocks.SAND;
                components.add(EntityComponents.fallingBlock(fallingBlock, this.amount()));
            } else {
                final int gameTicks = (int) (this.tick() * (useGameTicks ? 1.0 : 2.0));
                components.add(EntityComponents.explode(gameTicks, this.amount(), explosionFlags));
            }

            return Entity.create(entity -> entity.position = this.position(), components);
        }
    }
}
