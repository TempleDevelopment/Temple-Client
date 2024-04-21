package xyz.templecheats.templeclient.features.module.modules.client.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import xyz.templecheats.templeclient.features.gui.font.TempleIcon;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.features.module.modules.client.HUD;
import xyz.templecheats.templeclient.util.color.impl.RectBuilder;
import xyz.templecheats.templeclient.util.math.Vec2d;
import xyz.templecheats.templeclient.util.setting.impl.ColorSetting;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;

import java.awt.*;
import java.util.Objects;

import static xyz.templecheats.templeclient.features.gui.font.Fonts.font18;
import static xyz.templecheats.templeclient.features.gui.font.Fonts.icon26;

public class Ping extends HUD.HudElement {
    public Ping() {
        super("Ping", "Shows Ping in the HUD");
        registerSettings(fill, outline, blur, color, outlineColor, outlineWidth, blurRadius);
    }

    @Override
    public void renderElement(ScaledResolution sr) {
        final String pingText = "Ping ";
        NetworkPlayerInfo playerInfo = Minecraft.getMinecraft().getConnection().getPlayerInfo(Minecraft.getMinecraft().player.getUniqueID());
        final int pingValue = playerInfo == null ? 0 : playerInfo.getResponseTime();

        new RectBuilder(new Vec2d(getX(), getY()), new Vec2d(getX() + getWidth(), getY() + getHeight()))
                .outlineColor(outlineColor.getColor())
                .width(outline.booleanValue() ? outlineWidth.doubleValue() : 0)
                .color(fill.booleanValue() ? color.getColor() : new Color(0,0,0,0))
                .radius(2.5)
                .blur(blur.booleanValue() ? blurRadius.doubleValue() : 0)
                .drawBlur()
                .draw();

        double iconOffset = (!this.isLeftOfCenter() ? this.getWidth() - icon26.getStringWidth(TempleIcon.WIFI2.getIcon()): -2);
        double textOffset = (!this.isLeftOfCenter() ? 1 : 13);

        if (HUD.INSTANCE.icon.booleanValue()) {
            icon26.drawIcon(TempleIcon.WIFI2.getIcon(), (float) (this.getX() + iconOffset), (float) (this.getY() + 5), new Color(100, 255, 90), false);
        } else {
            textOffset = getWidth() / 2 - font18.getStringWidth(pingText + pingValue) / 2;
        }

        int textColor = HUD.INSTANCE.sync.booleanValue() ? ClickGUI.INSTANCE.getClientColor((int) getY()) : Color.WHITE.getRGB();

        font18.drawString(pingText, this.getX() + textOffset, this.getY() + 5, textColor, true);
        font18.drawString(String.valueOf(pingValue), this.getX() + font18.getStringWidth(pingText) + textOffset, this.getY() + 5, 0xFFFFFF, true);

        this.setWidth(font18.getStringWidth(pingText + pingValue) + 18);
        this.setHeight(font18.getFontHeight() + 8);
    }
}