package me.samsuik.cannonlib.block.interaction;

import me.samsuik.cannonlib.World;
import me.samsuik.cannonlib.block.Block;
import me.samsuik.cannonlib.block.Blocks;
import me.samsuik.cannonlib.entity.Entity;
import me.samsuik.cannonlib.entity.component.EntityComponents;
import me.samsuik.cannonlib.physics.vec3.Vec3i;

import java.util.List;

public final class FallingBlock implements Interaction {
    @Override
    public int onBlockUpdate(final World world, final Vec3i position, final Block block) {
        return 2;
    }

    @Override
    public void onTick(final World world, final Vec3i position, final Block block) {
        final Block blockBelow = world.getBlockAtRaw(position.down());
        if (blockBelow == null || blockBelow.replace()) {
            world.addEntity(Entity.create(
                    entity -> entity.position = position.toVec3d().center(),
                    List.of(
                            EntityComponents.ENTITY_TICK_WITH_COLLISION,
                            EntityComponents.fallingBlock(block, 1)
                    )
            ));
            world.setBlock(position, Blocks.AIR);
        }
    }
}
