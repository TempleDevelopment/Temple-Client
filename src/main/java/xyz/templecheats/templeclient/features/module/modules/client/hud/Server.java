package xyz.templecheats.templeclient.features.module.modules.client.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import xyz.templecheats.templeclient.features.gui.font.TempleIcon;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.features.module.modules.client.HUD;
import xyz.templecheats.templeclient.util.render.shader.impl.RectBuilder;
import xyz.templecheats.templeclient.util.math.Vec2d;

import java.awt.*;
import java.util.Objects;

import static xyz.templecheats.templeclient.features.gui.font.Fonts.font18;
import static xyz.templecheats.templeclient.features.gui.font.Fonts.icon26;

public class Server extends HUD.HudElement {
    public Server() {
        super("Server", "Shows the server name in the HUD");
        registerSettings(fill, outline, blur, color, outlineColor, outlineWidth, blurRadius);

        this.setX(2);
        this.setY(2);
    }

    @Override
    public void renderElement(ScaledResolution sr) {
        final String serverName = Minecraft.getMinecraft().isSingleplayer() ? "singleplayer" : Objects.requireNonNull(Minecraft.getMinecraft().getCurrentServerData()).serverIP;

        new RectBuilder(new Vec2d(getX(), getY()), new Vec2d(getX() + getWidth(), getY() + getHeight()))
                .outlineColor(outlineColor.getColor())
                .width(outline.booleanValue() ? outlineWidth.doubleValue() : 0)
                .color(fill.booleanValue() ? color.getColor() : new Color(0,0,0,0))
                .radius(2.5)
                .blur(blur.booleanValue() ? blurRadius.doubleValue() : 0)
                .drawBlur()
                .draw();

        double iconOffset = (!this.isLeftOfCenter() ? this.getWidth() - icon26.getStringWidth(TempleIcon.SCREEN.getIcon()): -1);
        double textOffset = (!this.isLeftOfCenter() ? 1 : 15);

        if (HUD.INSTANCE.icon.booleanValue()) {
            icon26.drawIcon(TempleIcon.WORLD.getIcon(), (float) (this.getX() + iconOffset), (float) (this.getY() + 4), new Color(0xFFFFFF), false);
        } else {
            textOffset = getWidth() / 2 - font18.getStringWidth(serverName) / 2;
        }

        int textColor = HUD.INSTANCE.sync.booleanValue() ? ClickGUI.INSTANCE.getClientColor((int) getY()) : Color.WHITE.getRGB();

        font18.drawString(serverName, (float) this.getX() + textOffset, (float) this.getY() + 4, textColor, true);

        this.setWidth(font18.getStringWidth(serverName) + 18);
        this.setHeight(font18.getFontHeight() + 8);
    }
}