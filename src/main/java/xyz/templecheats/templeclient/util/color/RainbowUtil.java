package xyz.templecheats.templeclient.util.color;

import java.awt.*;

import static xyz.templecheats.templeclient.util.color.ColorUtil.lerpColor;

public class RainbowUtil {
    private int rainbowColor;

    //Gradient with more than two colors.
    public int rainbowProgress(int speed, int index, int... colors) {
        int angle = (int) ((System.currentTimeMillis() / speed + index) % 360);
        angle = (angle > 180 ? 360 - angle : angle) + 180;
        int colorIndex = (int) (angle / 360f * colors.length);
        if (colorIndex == colors.length) {
            colorIndex--;
        }
        int color1 = colors[colorIndex];
        int color2 = colors[colorIndex == colors.length - 1 ? 0 : colorIndex + 1];
        return lerpColor(color1, color2, angle / 360f * colors.length - colorIndex);
    }

    public void updateRainbow() {
        double rainbowState = Math.ceil((System.currentTimeMillis() + 1) / 20.0);
        rainbowState %= 360;
        rainbowColor = Color.getHSBColor((float)(rainbowState / 360.0f), 0.5f, 1f).getRGB();
    }

    public int getRainbowColor() {
        return rainbowColor;
    }
}