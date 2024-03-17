package xyz.templecheats.templeclient.features.module.modules.client.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import xyz.templecheats.templeclient.features.gui.font.FontUtils;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.features.module.modules.client.HUD;

public class PlayerName extends HUD.HudElement {
    public PlayerName() {
        super("PlayerName", "Shows your ign in the HUD");

        this.setX(2);
        this.setY(2);
    }

    @Override
    protected void renderElement(ScaledResolution sr) {
        final String playerName = Minecraft.getMinecraft().player.getName();
        final String text = "Welcome " + playerName;

        this.setWidth(FontUtils.getStringWidth(text));
        this.setHeight(FontUtils.getFontHeight());

        FontUtils.drawString(text, this.getX(), this.getY(), ClickGUI.INSTANCE.getStartColor(), true);
    }
}