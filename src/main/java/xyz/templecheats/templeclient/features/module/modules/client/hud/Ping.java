package xyz.templecheats.templeclient.features.module.modules.client.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.features.module.modules.client.HUD;

public class Ping extends HUD.HudElement {
    public Ping() {
        super("Ping", "Shows Ping in the HUD");
    }

    @Override
    public void renderElement(ScaledResolution sr) {
        final String pingText = "Ping ";
        NetworkPlayerInfo playerInfo = Minecraft.getMinecraft().getConnection().getPlayerInfo(Minecraft.getMinecraft().player.getUniqueID());
        final int pingValue = playerInfo == null ? 0 : playerInfo.getResponseTime();

        this.setWidth(font.getStringWidth(pingText + pingValue));
        this.setHeight(font.getFontHeight());

        font.drawString(pingText, this.getX(), this.getY(), ClickGUI.INSTANCE.getStartColor().getRGB(), true, 1.0f);
        font.drawString(String.valueOf(pingValue), this.getX() + font.getStringWidth(pingText), this.getY(), 0xFFFFFF, true, 1.0f);
    }
}