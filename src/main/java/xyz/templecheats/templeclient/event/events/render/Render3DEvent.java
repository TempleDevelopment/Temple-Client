package xyz.templecheats.templeclient.event.events.render;

import xyz.templecheats.templeclient.event.EventCancellable;
import xyz.templecheats.templeclient.event.EventStageable;

public class Render3DEvent extends EventStageable {
    public final float partialTicks;

    public Render3DEvent(final float partialTicks) {
        this.partialTicks = partialTicks;
    }
}
