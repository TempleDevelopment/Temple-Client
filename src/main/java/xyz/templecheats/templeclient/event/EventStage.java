package xyz.templecheats.templeclient.event;

import net.minecraftforge.fml.common.eventhandler.Event;

public class EventStage extends Event {

    private int stage;
	
	public EventStage() {}

    public EventStage(int stage) {
        this.stage = stage;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }
}