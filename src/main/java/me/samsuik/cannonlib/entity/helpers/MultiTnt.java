package me.samsuik.cannonlib.entity.helpers;

import me.samsuik.cannonlib.block.Blocks;
import me.samsuik.cannonlib.component.Component;
import me.samsuik.cannonlib.entity.Entity;
import me.samsuik.cannonlib.entity.component.EntityComponents;
import me.samsuik.cannonlib.explosion.ExplosionFlags;
import me.samsuik.cannonlib.physics.vec3.Vec3d;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MultiTnt {
    private static final Pattern MULTI_PATTERN = Pattern.compile("(?<tag>[mwf]):(?<value>\\d+)");

    public static Entity createFromMultiString(
            final String multiString,
            final Consumer<Entity> entityConsumer,
            final List<Component<Entity>> components
    ) {
        final Map<MultiTag, Integer> tags = multiTags(multiString);
        final int wait = tags.getOrDefault(MultiTag.WAIT, 0);
        final int multi = tags.getOrDefault(MultiTag.MULTI, 1);

        final Component<Entity> interaction;
        if (tags.containsKey(MultiTag.FUSE)) {
            final int fuse = tags.get(MultiTag.FUSE);
            final int explodeAt = fuse + wait;
            interaction = EntityComponents.explode(explodeAt, multi, ExplosionFlags.HANDLE_BLOCKS);
        } else {
            interaction = EntityComponents.fallingBlock(Blocks.SAND, multi);
        }

        final List<Component<Entity>> multiComponents = List.of(
                Component.<Entity>user(entity -> entity.momentum = Vec3d.zero())
                        .atTick(wait)
                        .condition(e -> wait > 0),
                EntityComponents.ENTITY_TICK_WITH_COLLISION
                        .and(interaction)
                        .afterOrAtTick(wait)
        );

        return Entity.create(entityConsumer, Component.concatLists(multiComponents, components));
    }

    private static Map<MultiTag, Integer> multiTags(final String multiString) {
        final Map<MultiTag, Integer> tags = new HashMap<>();
        final Matcher matcher = MULTI_PATTERN.matcher(multiString);
        while (matcher.find()) {
            final MultiTag tag = MultiTag.match(matcher.group("tag"));
            final int value = Integer.parseInt(matcher.group("value"));
            tags.put(tag, value);
        }
        return tags;
    }

    private enum MultiTag {
        MULTI("m"), FUSE("f"), WAIT("w");

        private final String tag;

        MultiTag(final String tag) {
            this.tag = tag;
        }

        public String getTag() {
            return this.tag;
        }

        public static MultiTag match(final String tag) {
            for (final MultiTag multiTag : values()) {
                if (multiTag.getTag().equals(tag)) {
                    return multiTag;
                }
            }
            return null;
        }
    }
}
