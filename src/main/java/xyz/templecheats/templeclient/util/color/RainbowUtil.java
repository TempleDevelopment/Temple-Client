package xyz.templecheats.templeclient.util.color;

import java.awt.*;

public class RainbowUtil {
    private int rainbowColor;

    public void updateRainbow() {
        double rainbowState = Math.ceil((System.currentTimeMillis() + 1) / 20.0);
        rainbowState %= 360;
        rainbowColor = Color.getHSBColor((float) (rainbowState / 360.0f), 0.5f, 1f).getRGB();
    }

    public int getRainbowColor() {
        return rainbowColor;
    }
}