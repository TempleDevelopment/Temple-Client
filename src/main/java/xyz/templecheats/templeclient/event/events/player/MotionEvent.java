package xyz.templecheats.templeclient.event.events.player;

import xyz.templecheats.templeclient.event.EventCancellable;

public class MotionEvent extends EventCancellable {

    private final double x;
    private final double y;
    private final double z;
    private float yaw;
    private float pitch;

    public MotionEvent(double x, double y, double z, float yaw, float pitch, EventStage stage) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.setStage(stage);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
}