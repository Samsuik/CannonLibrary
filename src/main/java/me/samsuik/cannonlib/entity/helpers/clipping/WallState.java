package me.samsuik.cannonlib.entity.helpers.clipping;

import me.samsuik.cannonlib.World;
import me.samsuik.cannonlib.block.Block;
import me.samsuik.cannonlib.block.interaction.BlockInteractions;
import me.samsuik.cannonlib.physics.vec3.Vec3i;

import java.util.HashMap;
import java.util.Map;

public final class WallState {
    public static final int DESTROYED_WALL = 0b001;
    public static final int PUSHED_WATER = 0b010;
    public static final int PUSHED_WATER_BELOW_GUIDER = 0b100;

    private final Map<Vec3i, Block> wallBlocks = new HashMap<>();
    private int originalWallState = 0;
    private int pushedWaterHeight = Integer.MIN_VALUE;
    private boolean pushedWaterBelowGuider = false;

    public boolean destroyedWall() {
        return (this.originalWallState & DESTROYED_WALL) != 0;
    }

    public boolean pushedWater() {
        return (this.originalWallState & PUSHED_WATER) != 0 || (this.originalWallState & PUSHED_WATER_BELOW_GUIDER) != 0;
    }

    public boolean hasPushedWaterBelowGuider() {
        return this.pushedWaterBelowGuider;
    }

    public void addBlock(final Vec3i pos, final Block presentBlock) {
        if (!presentBlock.replace()) {
            this.wallBlocks.put(pos, presentBlock);
        }
    }

    public boolean problem(final int state) {
        if (this.destroyedWall() && (state & DESTROYED_WALL) == 0) {
            return true;
        }

        if (this.pushedWater() && ((state & PUSHED_WATER) == 0) && ((state & PUSHED_WATER_BELOW_GUIDER) == 0)) {
            return true;
        }

        return false;
    }

    public void updateState(final World world, final int guiderY) {
        for (final Map.Entry<Vec3i, Block> entry : this.wallBlocks.entrySet()) {
            final int blockY = entry.getKey().y();
            if (this.pushedWaterHeight < blockY && entry.getValue().has(BlockInteractions.WATER)) {
                this.pushedWaterHeight = blockY;
            }
        }

        if (this.pushedWaterHeight != Integer.MIN_VALUE && this.pushedWaterHeight > guiderY) {
            this.pushedWaterHeight = guiderY;
            this.pushedWaterBelowGuider = true;
        }

        this.originalWallState = this.getState(world, true);
    }

    public int getState(final World world, final boolean force) {
        int state = 0;
        for (final Map.Entry<Vec3i, Block> entry : this.wallBlocks.entrySet()) {
            final Vec3i position = entry.getKey();
            final Block block = world.getBlockAt(position);

            if (block.replace() && !entry.getValue().replace()) {
                state |= DESTROYED_WALL;
            }

            if (block.has(BlockInteractions.WATER) && (force || (this.originalWallState & PUSHED_WATER) != 0)) {
                if ((state & PUSHED_WATER) == 0) {
                    if (position.y() < this.pushedWaterHeight) {
                        state |= PUSHED_WATER_BELOW_GUIDER;
                    } else {
                        state |= PUSHED_WATER;
                        state &= ~PUSHED_WATER_BELOW_GUIDER;
                    }
                }
            }

            if (state == this.originalWallState && !force) {
                break; // no need to check further
            }
        }

        return state;
    }
}
