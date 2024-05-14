package xyz.templecheats.templeclient.event.events.render;

import xyz.templecheats.templeclient.event.EventStageable;

public class Render3DPostEvent extends EventStageable {
    public final float partialTicks;

    public Render3DPostEvent(final float partialTicks) {
        this.partialTicks = partialTicks;
    }
}
