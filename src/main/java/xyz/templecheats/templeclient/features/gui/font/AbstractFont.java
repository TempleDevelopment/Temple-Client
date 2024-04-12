package xyz.templecheats.templeclient.features.gui.font;

import java.awt.*;

public interface AbstractFont {
    float getStringWidth(String text);
    float getFontHeight();

    void drawStringWithShadow(String text, float x, float y, int color, float scale);
    void drawStringWithShadow(String text, float x, float y, Color color, float scale);

    void drawString(String text, float x, float y, Color color, boolean shadow, float scale);
    void drawString(String text, float x, float y, int color, boolean shadow, float scale);

    void drawCenteredString(String text, float x, float y, Color color, boolean shadow, float scale);

    String trimStringToWidth(String text, int width);

    String trimStringToWidth(String text, int width, boolean reverse);

}
