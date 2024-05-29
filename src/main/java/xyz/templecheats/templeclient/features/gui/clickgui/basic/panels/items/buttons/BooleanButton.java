package xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.items.buttons;

import xyz.templecheats.templeclient.features.gui.clickgui.basic.ClientGuiScreen;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;

import java.awt.*;

import static xyz.templecheats.templeclient.features.gui.font.Fonts.font16;

public class BooleanButton extends Button {
    private final Button parentButton;
    private final BooleanSetting setting;

    public BooleanButton(BooleanSetting setting, Button parentButton) {
        super(setting.name);
        this.setting = setting;
        this.parentButton = parentButton;
        height = 15;
    }

    private int lightenColor(int color, float amount) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        float[] hsb = Color.RGBtoHSB(r, g, b, null);

        float newBrightness = Math.min(hsb[2] + amount, 1.0f);
        return Color.HSBtoRGB(hsb[0], hsb[1], newBrightness) | (color & 0xFF000000);
    }


    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        RenderUtil.drawRect(x, y, x + getWidth() + 7.4F, y + height, !this.isHovering(mouseX, mouseY) ? 0x11555555 : 0x88555555);

        int checkboxSize = 6;
        int checkboxX = (int) (x + 2);
        int checkboxY = (int) (y + (height / 2) - (checkboxSize / 2));

        int topColor, bottomColor;
        if (this.getState()) {
            bottomColor = ClickGUI.INSTANCE.getStartColor().getRGB();
        } else {
            bottomColor = 0x11555555;
        }
        topColor = lightenColor(bottomColor, 0.1f);

        RenderUtil.drawGradientRect(checkboxX, checkboxY, checkboxX + checkboxSize, checkboxY + checkboxSize, topColor, bottomColor);

        font16.drawString(getLabel(), this.x + 2.3 + 8, this.y + 4, 0xFFFFFFFF, false);
    }

    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public int getHeight() {
        return 14;
    }

    @Override
    public void toggle() {
        setting.setBooleanValue(!setting.booleanValue());
    }

    @Override
    public boolean getState() {
        return setting.booleanValue();
    }

    @Override
    public ClientGuiScreen getClientScreen() {
        return this.parentButton.getClientScreen();
    }
}
