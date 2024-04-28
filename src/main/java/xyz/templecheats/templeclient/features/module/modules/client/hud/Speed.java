package xyz.templecheats.templeclient.features.module.modules.client.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import xyz.templecheats.templeclient.features.gui.font.TempleIcon;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.features.module.modules.client.HUD;
import xyz.templecheats.templeclient.util.render.shader.impl.RectBuilder;
import xyz.templecheats.templeclient.util.math.Vec2d;

import java.awt.*;

import static xyz.templecheats.templeclient.features.gui.font.Fonts.*;

public class Speed extends HUD.HudElement {
    /*
     * Variables
     */
    private Vec3d lastPos = Vec3d.ZERO;

    public Speed() {
        super("Speed", "Shows Speed in the HUD");
        registerSettings(fill, outline, blur, color, outlineColor, outlineWidth, blurRadius);
    }

    @Override
    public void renderElement(ScaledResolution sr) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null) return;

        Vec3d currentPos = new Vec3d(player.posX, player.posY, player.posZ);
        Vec3d lastTickPos = new Vec3d(player.lastTickPosX, player.lastTickPosY, player.lastTickPosZ);
        double speedValue = (currentPos.distanceTo(lastTickPos)) * 20;

        final String speedText = "Speed ";

        new RectBuilder(new Vec2d(getX(), getY()), new Vec2d(getX() + getWidth(), getY() + getHeight()))
                .outlineColor(outlineColor.getColor())
                .width(outline.booleanValue() ? outlineWidth.doubleValue() : 0)
                .color(fill.booleanValue() ? color.getColor() : new Color(0,0,0,0))
                .radius(2.5)
                .blur(blur.booleanValue() ? blurRadius.doubleValue() : 0)
                .drawBlur()
                .draw();

        double iconOffset = (!this.isLeftOfCenter() ? this.getWidth() - icon34.getStringWidth(TempleIcon.MOVEMENT.getIcon()): -1);
        double textOffset = (!this.isLeftOfCenter() ? 1 : 13);

        if (HUD.INSTANCE.icon.booleanValue()) {
            icon34.drawIcon(TempleIcon.MOVEMENT.getIcon(), (float) (this.getX() + iconOffset), (float) (this.getY() + 3), new Color(156, 51, 255), false);
        } else {
            textOffset = getWidth() / 2 - font18.getStringWidth(speedText + String.format("%.2f", speedValue)) / 2;
        }

        int textColor = HUD.INSTANCE.sync.booleanValue() ? ClickGUI.INSTANCE.getClientColor((int) getY()) : Color.WHITE.getRGB();

        font18.drawString(speedText, (float) this.getX() + textOffset, (float) this.getY() + 5, textColor, true);
        font18.drawString(String.format("%.2f", speedValue), (float) (this.getX() + font18.getStringWidth(speedText)) + textOffset, (float) this.getY() + 5, 0xFFFFFF, true);

        this.setWidth(font18.getStringWidth(speedText + String.format("%.2f", speedValue)) + 17);
        this.setHeight(font18.getFontHeight() + 8);
    }
}