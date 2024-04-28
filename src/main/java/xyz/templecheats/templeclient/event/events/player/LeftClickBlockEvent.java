package xyz.templecheats.templeclient.event.events.player;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Event;

public class LeftClickBlockEvent extends Event {
    private final BlockPos blockPos;
    private final EnumFacing blockFace;

    public LeftClickBlockEvent(BlockPos blockPos, EnumFacing blockFace) {
        this.blockPos = blockPos;
        this.blockFace = blockFace;
    }

    public BlockPos getPos() {
        return blockPos;
    }

    public EnumFacing getFace() {
        return blockFace;
    }
}
