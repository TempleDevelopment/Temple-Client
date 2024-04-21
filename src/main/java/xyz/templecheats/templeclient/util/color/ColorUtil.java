package xyz.templecheats.templeclient.util.color;

import java.awt.Color;

import static xyz.templecheats.templeclient.util.math.MathUtil.clamp;
import static xyz.templecheats.templeclient.util.math.MathUtil.lerp;

public class ColorUtil {

    /**
     * Converts a position on the slider to a Color object.
     * Assumes the slider width represents a full range of hues (0 to 360 degrees).
     *
     * @param position The position on the slider.
     * @param sliderWidth The total width of the slider.
     * @return The Color object corresponding to the position on the slider.
     */
    public static Color getColorFromPosition(int position, int sliderWidth) {
        float hue = (float) position / sliderWidth;
        return Color.getHSBColor(hue, 1.0f, 1.0f);
    }

    /**
     * Converts a position on the slider to an alpha value.
     * Assumes the slider width represents a full range of alpha values (0 to 255).
     *
     * @param position The position on the slider.
     * @param sliderWidth The total width of the slider.
     * @return The alpha value corresponding to the position on the slider.
     */
    public static int getAlphaFromPosition(int position, int sliderWidth) {
        return (int) ((float) position / sliderWidth * 255);
    }

    /**
     * Converts an alpha value to a position on the slider based on alpha range (0 to 255).
     *
     * @param alpha The alpha value.
     * @param sliderWidth The total width of the slider.
     * @return The position on the slider corresponding to the alpha value.
     */
    public static float getPositionFromAlpha(int alpha, float sliderWidth) {
        return (float) alpha * sliderWidth / 255;
    }

    /**
     * Converts a Color object to a position on the slider based on saturation and brightness.
     *
     * @param color The Color object.
     * @param sliderWidth The total width of the slider.
     * @param sliderHeight The total height of the slider.
     * @return An array containing the y positions on the slider corresponding to the saturation and brightness of the color.
     */
    public static float[] getPositionFromColor(Color color, float sliderWidth, float sliderHeight) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        float saturationPosition = hsb[1] * sliderWidth;
        float brightnessPosition = (1.0f - hsb[2]) * sliderHeight;
        return new float[] {
                saturationPosition,
                brightnessPosition
        };
    }

    /**
     * Converts a Color object to a position on the slider.
     * Useful for initializing the slider position based on an existing color.
     *
     * @param color The Color object.
     * @param sliderWidth The total width of the slider.
     * @return The position on the slider that corresponds to the color.
     */
    public static float getPositionFromColor(Color color, float sliderWidth) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return hsb[0] * sliderWidth;
    }

    /**
     * Converts a Color object to a hexadecimal String.
     *
     * @param color The Color object.
     * @return The hexadecimal String representing the color.
     */
    public static String toHexString(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    public static Color setAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public static Color setAlpha(Color color, double alpha) {
        return setAlpha(color, (int) (clamp((float) alpha, 0.0f, 1.0f) * 255.0));
    }

    public static Color setAlpha(int color, double alpha) {
        return setAlpha(new Color(color), (int) (clamp((float) alpha, 0.0f, 1.0f) * 255.0));
    }

    public static Color getColor(Color color, float alpha) {
        float red = (float)color.getRed() / 255.0f;
        float green = (float)color.getGreen() / 255.0f;
        float blue = (float)color.getBlue() / 255.0f;
        return new Color(red, green, blue, alpha);
    }

    public static Color lerpColor(Color current, Color target, float lerp) {
        int red = (int) lerp(current.getRed(), target.getRed(), lerp);
        int green = (int) lerp(current.getGreen(), target.getGreen(), lerp);
        int blue = (int) lerp(current.getBlue(), target.getBlue(), lerp);
        int alpha = (int) lerp(current.getAlpha(), target.getAlpha(), lerp);

        return new Color(red, green, blue, alpha);
    }

    public static int lerpColor(int color1, int color2, float amount) {
        amount = Math.min(1, Math.max(0, amount));

        int red1 = getRed(color1);
        int green1 = getGreen(color1);
        int blue1 = getBlue(color1);
        int alpha1 = getAlpha(color1);

        int red2 = getRed(color2);
        int green2 = getGreen(color2);
        int blue2 = getBlue(color2);
        int alpha2 = getAlpha(color2);

        int interpolatedRed = (int) lerp(red1, red2, amount);
        int interpolatedGreen = (int) lerp(green1, green2, amount);
        int interpolatedBlue = (int) lerp(blue1, blue2, amount);
        int interpolatedAlpha = (int) lerp(alpha1, alpha2, amount);

        return (interpolatedAlpha << 24) | (interpolatedRed << 16) | (interpolatedGreen << 8) | interpolatedBlue;
    }

    public static int getRed(final int hex) {
        return hex >> 16 & 255;
    }

    public static int getGreen(final int hex) {
        return hex >> 8 & 255;
    }

    public static int getBlue(final int hex) {
        return hex & 255;
    }

    public static int getAlpha(final int hex) {
        return hex >> 24 & 255;
    }
}