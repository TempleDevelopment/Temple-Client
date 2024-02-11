package xyz.templecheats.templeclient.impl.modules.client.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import xyz.templecheats.templeclient.impl.gui.font.FontUtils;
import xyz.templecheats.templeclient.impl.modules.client.ClickGUI;
import xyz.templecheats.templeclient.impl.modules.client.HUD;

public class FPS extends HUD.HudElement {
    public FPS() {
        super("FPS", "Shows FPS in the HUD");
    }
    
    @Override
    protected void renderElement(ScaledResolution sr) {
        final String fpsText = "FPS ";
        final int fpsValue = Minecraft.getDebugFPS();
        
        this.setWidth(FontUtils.getStringWidth(fpsText + fpsValue));
        this.setHeight(FontUtils.getFontHeight());
        
        FontUtils.drawString(fpsText, this.getX(), this.getY(), ClickGUI.INSTANCE.getStartColor(), true);
        FontUtils.drawString(String.valueOf(fpsValue), this.getX() + FontUtils.getStringWidth(fpsText), this.getY(), 0xFFFFFF, true);
    }
}