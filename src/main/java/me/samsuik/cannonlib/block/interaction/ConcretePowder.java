package me.samsuik.cannonlib.block.interaction;

import me.samsuik.cannonlib.World;
import me.samsuik.cannonlib.block.Block;
import me.samsuik.cannonlib.block.Blocks;
import me.samsuik.cannonlib.entity.EntityHelpers;
import me.samsuik.cannonlib.entity.component.EntityComponents;
import me.samsuik.cannonlib.physics.Rotation;
import me.samsuik.cannonlib.physics.vec3.Vec3i;

import java.util.List;

public final class ConcretePowder implements Interaction {
    @Override
    public int onBlockUpdate(final World world, final Vec3i position, final Block block) {
        for (final Rotation rotation : Rotation.values()) {
            if (world.getBlockAt(position.move(rotation)).has(BlockInteractions.WATER)) {
                world.setBlock(position, Blocks.CONCRETE);
                return 0;
            }
        }

        return 2;
    }

    @Override
    public void onTick(final World world, final Vec3i position, final Block block) {
        final Block blockBelow = world.getBlockAtRaw(position.down());
        if (blockBelow == null || blockBelow.replace()) {
            world.addEntity(EntityHelpers.create(
                    entity -> entity.position = position.toVec3d().center(),
                    List.of(
                            EntityComponents.ENTITY_TICK_WITH_COLLISION,
                            EntityComponents.fallingBlock(block, 1)
                    )
            ));
        }
    }
}
