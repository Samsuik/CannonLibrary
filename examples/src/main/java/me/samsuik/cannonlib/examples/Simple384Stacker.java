package me.samsuik.cannonlib.examples;

import me.samsuik.cannonlib.World;
import me.samsuik.cannonlib.block.Blocks;
import me.samsuik.cannonlib.entity.Entity;
import me.samsuik.cannonlib.entity.component.EntityComponents;
import me.samsuik.cannonlib.explosion.ExplosionFlags;
import me.samsuik.cannonlib.physics.shape.Shapes;
import me.samsuik.cannonlib.physics.vec3.Vec3d;
import me.samsuik.cannonlib.physics.vec3.Vec3i;

public final class Simple384Stacker {
    public static void main(String[] args) {
        final World world = new World();
        world.floor(-64);

        // wall
        for (int y = -64; y < 320; ++y) {
            world.setBlock(new Vec3i(32, y, 0), Blocks.COBBLESTONE);
            world.setBlock(new Vec3i(31, y, 0), Blocks.WATER);
        }

        // guider
        world.setBlock(new Vec3i(1, 319, 0), Blocks.COBBLESTONE);

        // power #1
        world.addEntity(Entity.builder()
                .position(Vec3d.center(0, 160, 0))
                .components(EntityComponents.explode(0, 500))
                .build()
        );

        // power #2
        world.addEntity(Entity.builder()
                .position(Vec3d.center(0, 180, 0))
                .components(EntityComponents.explode(4, 500))
                .build()
        );

        // 1 rev
        world.addEntity(Entity.builder()
                .position(Vec3d.center(1, 161, 0))
                .components(
                        EntityComponents.ENTITY_TICK_WITH_COLLISION,
                        EntityComponents.explode(12, 1)
                        //EntityComponents.entityLogger("1 rev")
                )
                .build()
        );


        // hammer
        world.addEntity(Entity.builder()
                .position(Vec3d.center(1, 181, 0))
                .components(
                        EntityComponents.ENTITY_TICK_WITH_COLLISION,
                        EntityComponents.explode(16, 469)
                        //EntityComponents.entityLogger("hammer").oncePerTick()
                )
                .build()
        );

        // sand
        world.addEntity(Entity.builder()
                .position(Vec3d.center(1, 161, 0))
                .components(
                        EntityComponents.ENTITY_TICK_WITH_COLLISION,
                        EntityComponents.fallingBlock(Blocks.SAND, 382)
                        //EntityComponents.entityLogger("sand").oncePerTick()
                )
                .build()
        );

        // nuke hammer
        world.addEntity(Entity.builder()
                .position(Vec3d.center(1, 181, 0))
                .components(
                        EntityComponents.ENTITY_TICK_WITH_COLLISION,
                        EntityComponents.explode(20, 64, ExplosionFlags.HANDLE_BLOCKS)
                        //EntityComponents.entityLogger("nuke hammer").oncePerTick()
                )
                .build()
        );

        // nuke tnt
        for (int segments = 0; segments < 8; ++segments) {
            world.addEntity(Entity.builder()
                    .position(Vec3d.center(1, 161, 0))
                    .components(
                            EntityComponents.ENTITY_TICK_WITH_COLLISION,
                            EntityComponents.explode(20 + segments, 24, ExplosionFlags.HANDLE_BLOCKS)
                            //EntityComponents.entityLogger("nuke tnt")
                            //        .state(segments == 7)
                            //        .oncePerTick()
                    )
                    .build()
            );
        }

        // oneshot sand
        world.addEntity(Entity.builder()
                .position(Vec3d.center(1, 181, 0))
                .components(
                        EntityComponents.ENTITY_TICK_WITH_COLLISION,
                        EntityComponents.fallingBlock(Blocks.RED_SAND, 1)
                )
                .build()
        );

        world.keepTicking();

        int destroyed = 0;
        for (int y = -64; y < 320; ++y) {
            if (world.getBlockAt(new Vec3i(32, y, 0)) != Blocks.COBBLESTONE) {
                destroyed++;
            }
        }

        System.out.println("blocks destroyed: " + destroyed);
    }
}
