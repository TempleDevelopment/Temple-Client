package xyz.templecheats.templeclient.event.events.player;

import net.minecraftforge.fml.common.eventhandler.Event;

public class JumpEvent extends Event {
    public final double motionX;
    public final double motionZ;

    public JumpEvent(double motionX, double motionZ) {
        this.motionX = motionX;
        this.motionZ = motionZ;
    }
}
