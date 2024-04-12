package xyz.templecheats.templeclient.util.math;

import net.minecraft.util.math.Vec2f;
public class Vec2d {
    public final double x;
    public final double y;

    public Vec2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vec2d(int x, int y) {
        this.x = (double) x;
        this.y = (double) y;
    }

    public Vec2d div(Vec2d vec2d) {
        return div(vec2d.x, vec2d.y);
    }

    public Vec2d div(double divider) {
        return div(divider, divider);
    }

    public Vec2d div(double x, double y) {
        return new Vec2d(this.x / x, this.y / y);
    }

    public Vec2d times(Vec2d vec2d) {
        return times(vec2d.x, vec2d.y);
    }

    public Vec2d times(double multiplier) {
        return times(multiplier, multiplier);
    }

    public Vec2d times(double x, double y) {
        return new Vec2d(this.x * x, this.y * y);
    }

    public Vec2d minus(Vec2d vec2d) {
        return minus(vec2d.x, vec2d.y);
    }

    public Vec2d minus(double sub) {
        return minus(sub, sub);
    }

    public Vec2d minus(double x, double y) {
        return new Vec2d(this.x - x, this.y - y);
    }

    public Vec2d plus(Vec2d vec2d) {
        return plus(vec2d.x, vec2d.y);
    }

    public Vec2d plus(double add) {
        return plus(add, add);
    }

    public Vec2d plus(double x, double y) {
        return new Vec2d(this.x + x, this.y + y);
    }

    public Vec2f toVec2f() {
        return new Vec2f((float) x, (float) y);
    }

    @Override
    public String toString() {
        return "Vec2d[" + x + ", " + y + "]";
    }

    public static final Vec2d ZERO = new Vec2d(0.0, 0.0);
}

