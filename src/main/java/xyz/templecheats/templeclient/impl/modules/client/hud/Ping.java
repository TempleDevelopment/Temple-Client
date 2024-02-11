package xyz.templecheats.templeclient.impl.modules.client.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import xyz.templecheats.templeclient.impl.gui.font.FontUtils;
import xyz.templecheats.templeclient.impl.modules.client.ClickGUI;
import xyz.templecheats.templeclient.impl.modules.client.HUD;

public class Ping extends HUD.HudElement {
    public Ping() {
        super("Ping", "Shows Ping in the HUD");
    }

    @Override
    protected void renderElement(ScaledResolution sr) {
        final String pingText = "Ping ";
        NetworkPlayerInfo playerInfo = Minecraft.getMinecraft().getConnection().getPlayerInfo(Minecraft.getMinecraft().player.getUniqueID());
        final int pingValue = playerInfo == null ? 0 : playerInfo.getResponseTime();

        this.setWidth(FontUtils.getStringWidth(pingText + pingValue));
        this.setHeight(FontUtils.getFontHeight());

        FontUtils.drawString(pingText, this.getX(), this.getY(), ClickGUI.INSTANCE.getStartColor(), true);
        FontUtils.drawString(String.valueOf(pingValue), this.getX() + FontUtils.getStringWidth(pingText), this.getY(), 0xFFFFFF, true);
    }
}