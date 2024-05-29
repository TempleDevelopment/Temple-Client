package xyz.templecheats.templeclient.features.gui.font;

import java.awt.*;

public interface AbstractFont {
    float getStringWidth(String text);

    float getFontHeight();

    void drawStringWithShadow(String text, float x, float y, int color);

    void drawStringWithShadow(String text, float x, float y, Color color);

    void drawString(String text, float x, float y, Color color, boolean shadow);

    void drawString(String text, float x, float y, int color, boolean shadow);

    void drawCenteredString(String text, float x, float y, Color color, boolean shadow);

    String trimStringToWidth(String text, int width);

    String trimStringToWidth(String text, int width, boolean reverse);

}
