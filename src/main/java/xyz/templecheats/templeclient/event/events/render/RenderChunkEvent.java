package xyz.templecheats.templeclient.event.events.render;

import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Event;

public class RenderChunkEvent extends Event {
    public BlockPos BlockPos;
    public RenderChunk RenderChunk;

    public RenderChunkEvent(RenderChunk renderChunk, BlockPos blockPos) {
        BlockPos = blockPos;
        RenderChunk = renderChunk;
    }
}
