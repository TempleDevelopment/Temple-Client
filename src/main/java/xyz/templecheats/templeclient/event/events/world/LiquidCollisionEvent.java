package xyz.templecheats.templeclient.event.events.world;

import net.minecraft.block.BlockLiquid;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import xyz.templecheats.templeclient.event.EventStageable;

public class LiquidCollisionEvent extends EventStageable {
    private AxisAlignedBB bb = BlockLiquid.NULL_AABB;
    private final BlockPos pos;
    
    public LiquidCollisionEvent(BlockPos pos) {
        this.pos = pos;
    }
    
    public AxisAlignedBB getBoundingBox() {
        return bb;
    }
    
    public void setBoundingBox(AxisAlignedBB bb) {
        this.bb = bb;
    }
    
    public BlockPos getBlockPos() {
        return pos;
    }
}
