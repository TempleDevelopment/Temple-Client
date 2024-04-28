package xyz.templecheats.templeclient.features.module.modules.client.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import xyz.templecheats.templeclient.features.gui.font.TempleIcon;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.features.module.modules.client.HUD;
import xyz.templecheats.templeclient.util.render.shader.impl.RectBuilder;
import xyz.templecheats.templeclient.util.math.Vec2d;

import java.awt.*;

import static xyz.templecheats.templeclient.features.gui.font.Fonts.*;

public class FPS extends HUD.HudElement {

    public FPS() {
        super("FPS", "Shows FPS in the HUD");
        registerSettings(fill, outline, blur, color, outlineColor, outlineWidth, blurRadius);
    }

    @Override
    public void renderElement(ScaledResolution sr) {
        final String fpsText = "FPS";
        final int fpsValue = Minecraft.getDebugFPS();

        new RectBuilder(new Vec2d(getX(), getY()), new Vec2d(getX() + getWidth(), getY() + getHeight()))
                .outlineColor(outlineColor.getColor())
                .width(outline.booleanValue() ? outlineWidth.doubleValue() : 0)
                .color(fill.booleanValue() ? color.getColor() : new Color(0,0,0,0))
                .radius(2.5)
                .blur(blur.booleanValue() ? blurRadius.doubleValue() : 0)
                .drawBlur()
                .draw();

        double iconOffset = (!this.isLeftOfCenter() ? this.getWidth() - icon26.getStringWidth(TempleIcon.SCREEN.getIcon()): 0);
        double textOffset = (!this.isLeftOfCenter() ? 1 : 15);

        if (HUD.INSTANCE.icon.booleanValue()) {
            icon26.drawIcon(TempleIcon.SCREEN.getIcon(), (float) (this.getX() + iconOffset), (float) (this.getY() + 5), -1, false);
        } else {
            textOffset = getWidth() / 2 - font18.getStringWidth(fpsText + fpsValue) / 2;
        }
        int textColor = HUD.INSTANCE.sync.booleanValue() ? ClickGUI.INSTANCE.getClientColor((int) getY()) : Color.WHITE.getRGB();

        font18.drawString(fpsText, this.getX() + textOffset, this.getY() + 5, textColor, true);
        font18.drawString(String.valueOf(fpsValue), this.getX() + font18.getStringWidth(fpsText) + textOffset + 2, this.getY() + 5, 0xFFFFFF, true);

        this.setWidth(font18.getStringWidth(fpsText + fpsValue) + 20);
        this.setHeight(font18.getFontHeight() + 8);
    }
}