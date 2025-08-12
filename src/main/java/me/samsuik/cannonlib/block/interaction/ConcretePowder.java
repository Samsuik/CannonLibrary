package me.samsuik.cannonlib.block.interaction;

import me.samsuik.cannonlib.World;
import me.samsuik.cannonlib.block.Block;
import me.samsuik.cannonlib.block.Blocks;
import me.samsuik.cannonlib.component.Component;
import me.samsuik.cannonlib.entity.Entity;
import me.samsuik.cannonlib.physics.Rotation;
import me.samsuik.cannonlib.physics.vec3.Vec3i;

import java.util.ArrayList;
import java.util.List;

public final class ConcretePowder implements CommonFallingBlock {
    private final List<Component<Entity>> extraComponents = new ArrayList<>();

    public ConcretePowder(final List<Component<Entity>> extraComponents) {
        this.extraComponents.addAll(extraComponents);
    }

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
        this.fall(world, position, block, this.extraComponents);
    }
}
