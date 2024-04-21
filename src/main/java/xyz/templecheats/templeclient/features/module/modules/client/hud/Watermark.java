package xyz.templecheats.templeclient.features.module.modules.client.hud;

import net.minecraft.client.gui.ScaledResolution;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.features.gui.font.TempleIcon;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.features.module.modules.client.HUD;
import xyz.templecheats.templeclient.util.color.impl.RectBuilder;
import xyz.templecheats.templeclient.util.math.Vec2d;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;

import java.awt.*;

import static xyz.templecheats.templeclient.features.gui.font.Fonts.*;

public class Watermark extends HUD.HudElement {
    private final EnumSetting<Mode> mode =  new EnumSetting<>("Mode", this, Mode.Normal);
    public Watermark() {
        super("Watermark", "Shows watermark in the HUD");
        registerSettings(mode, fill, outline, blur, color, outlineColor, outlineWidth, blurRadius);
        this.setEnabled(true);
        this.setX(2);
        this.setY(2);
    }

    @Override
    public void renderElement(ScaledResolution sr) {
        if (mode.value() == Mode.Normal) {
            final String text = "templecheats.xyz v" + TempleClient.VERSION;

            this.setWidth(font18.getStringWidth(text));
            this.setHeight(font18.getFontHeight());

            font18.drawString(text, getX(), getY(), ClickGUI.INSTANCE.getStartColor().getRGB(), true);
        } else if (mode.value() == Mode.Modern) {
            final String text = "TempleClient";

            new RectBuilder(new Vec2d(getX(), getY()), new Vec2d(getX() + getWidth(), getY() + getHeight()))
                    .outlineColor(outlineColor.getColor())
                    .width(outline.booleanValue() ? outlineWidth.doubleValue() : 0)
                    .color(fill.booleanValue() ? color.getColor() : new Color(0,0,0,0))
                    .radius(3.0)
                    .blur(3.0)
                    .drawBlur()
                    .draw();

            new RectBuilder(new Vec2d(getX(), getY()), new Vec2d(getX() + getHeight(), getY() + getHeight()))
                    .colorH(new Color(ClickGUI.INSTANCE.getClientColor(0)), new Color(ClickGUI.INSTANCE.getClientColor(5)))
                    .radius(3.0)
                    .blur(6.9420) //6.9420 is best value! change my mind.
                    .drawBlur()
                    .draw();

            icon32.drawIcon(TempleIcon.TEMPLE.getIcon(), (float) getX() - 0.8f, (float) (getY() + 2), -1, false);
            font18.drawString(text, getX() + getHeight() + 2, getY() + 3, -1, true);

            this.setWidth(icon32.getStringWidth(TempleIcon.TEMPLE.getIcon()) + font18.getStringWidth(text) + 10);
            this.setHeight(font18.getFontHeight() + 5);
        }
    }

    private enum Mode {
        Normal,
        Modern
    }
}