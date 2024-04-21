package xyz.templecheats.templeclient.util.render;

public class Animation {
    private final float length;
    private final boolean initialState;
    private final Easing easing;
    private long lastMillis;
    private boolean state;

    public Animation(float length, boolean initialState, Easing easing) {
        this.length = length;
        this.initialState = initialState;
        this.easing = easing;
        this.lastMillis = 0L;
        this.state = initialState;
    }

    public void setState(boolean value) {
        this.lastMillis = (long) (!value ? System.currentTimeMillis() - ((1 - this.getLinearFactor()) * (long)this.length) : System.currentTimeMillis() - (this.getLinearFactor() * (long)this.length));
        this.state = value;
    }

    // Don't blame me
    public double getAnimationFactor() {
        return this.state
                ? this.easing.inc(((double)(System.currentTimeMillis() - this.lastMillis) / (double)this.length) >= 0.0D && (double)(System.currentTimeMillis() - this.lastMillis) / (double)this.length <= 1.0D
                ? (double)(System.currentTimeMillis() - this.lastMillis) / (double)this.length
                : 0.0D)
                : this.easing.inc(1.0D - ((double)(System.currentTimeMillis() - this.lastMillis) / (double)this.length) >= 0.0D && 1.0D - ((double)(System.currentTimeMillis() - this.lastMillis) / (double)this.length) <= 1.0D
                ? 1.0D - ((double)(System.currentTimeMillis() - this.lastMillis) / (double)this.length)
                : 0.0D
        );
    }

    public void resetToDefault() {
        this.setState(this.initialState);
        this.lastMillis = (long) (this.initialState ? System.currentTimeMillis() - ((1.0D - this.getLinearFactor()) * (long)this.length) : System.currentTimeMillis() - (this.getLinearFactor() * (long)this.length));
    }

    private double getLinearFactor() {
        return !this.state ? Math.min(Math.max(0.0D, 1.0D - ((double)(System.currentTimeMillis() - this.lastMillis) / (double)this.length)), 1.0D) : Math.min(Math.max(0.0D, (double)(System.currentTimeMillis() - this.lastMillis) / (double)this.length), 1.0D);
    }
}
