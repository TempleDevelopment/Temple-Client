package xyz.templecheats.templeclient.event;
public class EventCancellable extends EventStageable {

    private boolean canceled;
    public EventCancellable() {}
    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }
}