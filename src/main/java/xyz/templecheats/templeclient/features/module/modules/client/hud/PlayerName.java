package xyz.templecheats.templeclient.features.module.modules.client.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.features.module.modules.client.HUD;

public class PlayerName extends HUD.HudElement {
    public PlayerName() {
        super("PlayerName", "Shows your ign in the HUD");

        this.setX(2);
        this.setY(2);
    }

    @Override
    public void renderElement(ScaledResolution sr) {
        final String playerName = Minecraft.getMinecraft().player.getName();
        final String text = "Welcome " + playerName;

        this.setWidth(font.getStringWidth(text));
        this.setHeight(font.getFontHeight());

        font.drawString(text, (float) this.getX(), (float) this.getY(), ClickGUI.INSTANCE.getStartColor().getRGB(), true, 1.0f);
    }
}