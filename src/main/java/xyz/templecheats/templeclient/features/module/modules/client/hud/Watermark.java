package xyz.templecheats.templeclient.features.module.modules.client.hud;

import net.minecraft.client.gui.ScaledResolution;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.features.gui.font.FontUtils;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.features.module.modules.client.HUD;

public class Watermark extends HUD.HudElement {
    public Watermark() {
        super("Watermark", "Shows watermark in the HUD");
        
        this.setEnabled(true);
        this.setX(2);
        this.setY(2);
    }
    
    @Override
    protected void renderElement(ScaledResolution sr) {
        final String text = "templecheats.xyz v" + TempleClient.VERSION;
        
        this.setWidth(FontUtils.getStringWidth(text));
        this.setHeight(FontUtils.getFontHeight());
        
        FontUtils.drawString(text, this.getX(), this.getY(), ClickGUI.INSTANCE.getStartColor(), true);
    }
}