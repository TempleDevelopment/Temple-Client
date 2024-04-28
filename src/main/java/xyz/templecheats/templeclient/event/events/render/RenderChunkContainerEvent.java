package xyz.templecheats.templeclient.event.events.render;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.client.renderer.chunk.RenderChunk;

public class RenderChunkContainerEvent extends Event {
    public RenderChunk RenderChunk;
    public RenderChunkContainerEvent(RenderChunk renderChunk)
    {
        RenderChunk = renderChunk;
    }
}
