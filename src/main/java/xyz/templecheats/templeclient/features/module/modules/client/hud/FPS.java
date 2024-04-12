package xyz.templecheats.templeclient.features.module.modules.client.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.features.module.modules.client.HUD;

public class FPS extends HUD.HudElement {
    public FPS() {
        super("FPS", "Shows FPS in the HUD");
    }

    @Override
    public void renderElement(ScaledResolution sr) {
        final String fpsText = "FPS ";
        final int fpsValue = Minecraft.getDebugFPS();

        this.setWidth(font.getStringWidth(fpsText + fpsValue));
        this.setHeight(font.getFontHeight());

        font.drawString(fpsText, this.getX(), this.getY(), ClickGUI.INSTANCE.getStartColor().getRGB(), true, 1.0f);
        font.drawString(String.valueOf(fpsValue), this.getX() + font.getStringWidth(fpsText), this.getY(), 0xFFFFFF, true, 1.0f);
    }
}