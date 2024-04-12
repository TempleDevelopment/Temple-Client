package xyz.templecheats.templeclient.util.world;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class BlockPosWithFacing {
    public BlockPos blockPos;
    public EnumFacing enumFacing;

    public BlockPosWithFacing(BlockPos blockPos, EnumFacing enumFacing) {
        this.blockPos = blockPos;
        this.enumFacing = enumFacing;
    }
}