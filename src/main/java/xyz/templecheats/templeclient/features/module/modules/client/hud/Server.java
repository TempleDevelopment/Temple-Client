package xyz.templecheats.templeclient.features.module.modules.client.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import xyz.templecheats.templeclient.features.gui.font.FontUtils;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.features.module.modules.client.HUD;

public class Server extends HUD.HudElement {
    public Server() {
        super("Server", "Shows the server name in the HUD");

        this.setX(2);
        this.setY(2);
    }

    @Override
    protected void renderElement(ScaledResolution sr) {
        final String serverName = Minecraft.getMinecraft().isSingleplayer() ? "singleplayer" : Minecraft.getMinecraft().getCurrentServerData().serverIP;

        this.setWidth(FontUtils.getStringWidth(serverName));
        this.setHeight(FontUtils.getFontHeight());

        FontUtils.drawString(serverName, this.getX(), this.getY(), ClickGUI.INSTANCE.getStartColor(), true);
    }
}