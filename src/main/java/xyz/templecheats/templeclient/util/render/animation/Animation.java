package xyz.templecheats.templeclient.util.render.animation;

import static xyz.templecheats.templeclient.util.math.MathUtil.clamp;

public class Animation {
    private Easing easing;
    private long length;
    private long lastMillis;

    private double startPoint;
    private double endPoint;
    private double value;
    private boolean finished;

    public Animation(final Easing easing, final long length) {
        this.easing = easing;
        this.lastMillis = System.currentTimeMillis();
        this.length = length;
    }

    public void progress(final double endPoint) {
        long currentTime = System.currentTimeMillis();

        if (this.endPoint != endPoint) {
            this.endPoint = endPoint;
            this.reset();
        } else {
            this.finished = currentTime - this.length > this.lastMillis;
            if (this.finished) {
                this.value = endPoint;
                return;
            }
        }

        double result = this.easing.inc(this.getProgress());
        if (this.value > endPoint) {
            this.value = this.startPoint - (this.startPoint - endPoint) * result;
        } else {
            this.value = this.startPoint + (endPoint - this.startPoint) * result;
        }
    }

    public double getProgress() {
        long currentTime = System.currentTimeMillis();
        return clamp((float) ((currentTime - this.lastMillis) / (double) this.length) , 0f, 1f);
    }

    public void reset() {
        this.lastMillis = System.currentTimeMillis();
        this.startPoint = value;
        this.finished = false;
    }

    public void setEasing(Easing easing) {
        this.easing = easing;
    }

    public void setLength(long length) {
        this.length = length;
    }
}
