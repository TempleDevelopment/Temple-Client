package xyz.templecheats.templeclient.event.events.render;

import xyz.templecheats.templeclient.event.EventStageable;

public class Render3DPreEvent extends EventStageable {
    public final float partialTicks;

    public Render3DPreEvent(final float partialTicks) {
        this.partialTicks = partialTicks;
    }
}
