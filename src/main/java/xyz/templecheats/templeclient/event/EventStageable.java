package xyz.templecheats.templeclient.event;

public class EventStageable {

    private EventStage stage;

    public EventStageable() {
    }

    public EventStageable(EventStage stage) {
        this.stage = stage;
    }

    /****************************************************************
     *                      Getters and Setters
     ****************************************************************/

    public EventStage getStage() {
        return stage;
    }

    public void setStage(EventStage stage) {
        this.stage = stage;
    }

    /****************************************************************
     *                      Event Stages
     ****************************************************************/

    public enum EventStage {
        PRE,
        POST
    }
}
