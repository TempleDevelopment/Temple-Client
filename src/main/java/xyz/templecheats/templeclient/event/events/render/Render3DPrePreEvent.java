package xyz.templecheats.templeclient.event.events.render;

import xyz.templecheats.templeclient.event.EventStageable;

public class Render3DPrePreEvent extends EventStageable { //crazy naming ik. it's basically before the pre event. thus pre pre
    public final float partialTicks;

    public Render3DPrePreEvent(final float partialTicks) {
        this.partialTicks = partialTicks;
    }
}
