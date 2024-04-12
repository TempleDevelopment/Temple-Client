package xyz.templecheats.templeclient.features.module.modules.client.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.features.module.modules.client.HUD;

import java.util.Objects;

public class Server extends HUD.HudElement {
    public Server() {
        super("Server", "Shows the server name in the HUD");

        this.setX(2);
        this.setY(2);
    }

    @Override
    public void renderElement(ScaledResolution sr) {
        final String serverName = Minecraft.getMinecraft().isSingleplayer() ? "singleplayer" : Objects.requireNonNull(Minecraft.getMinecraft().getCurrentServerData()).serverIP;

        this.setWidth(font.getStringWidth(serverName));
        this.setHeight(font.getFontHeight());

        font.drawString(serverName, (float) this.getX(), (float) this.getY(), ClickGUI.INSTANCE.getStartColor().getRGB(), true, 1.0f);
    }
}