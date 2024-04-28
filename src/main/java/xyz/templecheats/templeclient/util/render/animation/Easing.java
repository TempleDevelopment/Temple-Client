/*
 * This Easing was made by LavaHack, and was rewritten in java.
 */
package xyz.templecheats.templeclient.util.render.animation;

import java.util.function.DoubleUnaryOperator;

import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.StrictMath.pow;
public enum Easing {
    Linear(x -> x),
    InSine(x -> 1 - Math.cos((x * Math.PI) / 2)),
    OutSine(x -> sin((x * Math.PI) / 2)),
    InOutSine(x -> -(Math.cos(Math.PI * x) - 1) / 2),
    InQuad(x -> x * x),
    OutQuad(x -> 1 - (1 - x) * (1 - x)),
    InOutQuad(x -> x < 0.5 ? 2 * x * x : 1 - Math.pow(-2 * x + 2, 2) / 2),
    InCubic(x -> x * x * x),
    OutCubic(x -> 1 - Math.pow(1 - x, 3)),
    InOutCubic(x -> x < 0.5 ? 4 * x * x * x : 1 - Math.pow(-2 * x + 2, 3) / 2),
    InQuart(it -> it * it * it * it),
    OutQuart(it -> 1.0 - pow(1.0 - it, 4)),
    InOutQuart(it -> it < 0.5 ? 8 * pow(it, 4) : 1.0 - pow(-2 * it + 2, 4) / 2),
    InQuint(it -> it * it * it * it * it),
    OutQuint(it -> 1.0 - pow(1.0 - it, 5)),
    InOutQuint(it -> it < 0.5 ? 16 * pow(it, 5) : 1.0 - pow(-2 * it + 2, 5) / 2),
    InExpo(it -> it == 0.0 ? 0.0 : pow(2, 10 * it - 10)),
    OutExpo(it -> it == 1.0 ? 1.0 : 1.0 - pow(2, -10 * it)),
    InOutExpo(it -> it == 0.0 || it == 1.0 ? it : it < 0.5 ? pow(2, 20 * it - 10) / 2 : (2 - pow(2, -20 * it + 10)) / 2),
    InCircle(it -> 1.0 - sqrt(1.0 - it * it)),
    OutCircle(it -> sqrt(1.0 - pow(it - 1.0, 2))),
    InOutCircle(it -> it < 0.5 ? (1.0 - sqrt(1.0 - 2 * it * 2 * it)) / 2 : (sqrt(1.0 - pow(-2 * it + 2, 2)) + 1) / 2.0),
    InSin(Math::sin),
    OutSin(it -> 1.0 - sin(1.0 - it)),
    OutElastic(it -> it == 0 ? 0 : it == 1 ? 1 : pow(2, -10 * it) * sin((it * 10 - 0.75) * ((2 * Math.PI) / 3)) * 0.5 + 1),
    InBack(it -> (1.70158 + 1) * it * it * it - 1.70158 * it * it);

    private final DoubleUnaryOperator function;

    Easing(DoubleUnaryOperator function) {
        this.function = function;
    }

    public double inc(double value) {
        double x = Math.min(Math.max(value, 0.0), 1.0);
        return function.applyAsDouble(x);
    }

    public double dec(double value) {
        return 1.0 - inc(value);
    }
}
