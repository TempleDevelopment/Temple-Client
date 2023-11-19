package xyz.templecheats.templeclient.utils.render;

public class ColorUtil {
    public static int RGBtoHex(int red, int green, int blue) {
        return (red << 16) | (green << 8) | (blue);
    }

    public static int[] getRainbow(int cycle, float offset) {
        int r = 0, g = 0, b = 0;
        long timeInCycle = (System.currentTimeMillis() - Math.round(offset * 1000)) % (cycle * 1000);
        float portionOfCycle = (float) 6 * timeInCycle / (cycle * 1000);
        float timeInPortion = portionOfCycle - (float) Math.floor(portionOfCycle);
        int timeInPortionRGB = Math.round(255 * timeInPortion);

        if (portionOfCycle < 1) {
            r = 255;
            g = timeInPortionRGB;
        } else if (portionOfCycle < 2) {
            r = 255 - timeInPortionRGB;
            g = 255;
        } else if (portionOfCycle < 3) {
            g = 255;
            b = timeInPortionRGB;
        } else if (portionOfCycle < 4) {
            g = 255 - timeInPortionRGB;
            b = 255;
        } else if (portionOfCycle < 5) {
            r = timeInPortionRGB;
            b = 255;
        } else if (portionOfCycle < 6) {
            r = 255;
            b = 255 - timeInPortionRGB;
        }
        return new int[] {r, g, b};
    }
}