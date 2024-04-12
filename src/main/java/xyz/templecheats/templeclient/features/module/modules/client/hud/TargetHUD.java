package xyz.templecheats.templeclient.features.module.modules.client.hud;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import org.lwjgl.opengl.GL11;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.features.module.modules.client.HUD;

import java.awt.*;
import java.util.Objects;

public class TargetHUD extends HUD.HudElement {
    public TargetHUD() {
        super("TargetHUD", "Draws a HUD featuring players name and health when aimed at");
    }

    @Override
    public void renderElement(ScaledResolution sr) {
        boolean show = false;
        double healthBarWidth = 0;
        String enemyNickname = "";
        double enemyHP = 0;
        double enemyDistance = 0;
        EntityPlayer entity = null;
        RayTraceResult objectMouseOver = mc.objectMouseOver;

        if (objectMouseOver != null && objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY && objectMouseOver.entityHit instanceof EntityPlayer) {
            entity = (EntityPlayer) objectMouseOver.entityHit;
            enemyNickname = entity.getName();
            enemyHP = entity.getHealth();
            enemyDistance = entity.getDistance(mc.player);
            show = true;
        }

        this.setWidth(140);
        this.setHeight(40);

        if (show && mc.world != null && mc.player != null) {
            final float x = (float)(this.getX() + this.getWidth() + 1);
            final float y = (float)(this.getY() + this.getHeight() - 30);

            final float health = Math.round(enemyHP);
            double hpPercentage = health / 20;

            hpPercentage = MathHelper.clamp(hpPercentage, 0, 1);
            final double hpWidth = 97.0 * hpPercentage;

            final String healthStr = String.valueOf(Math.round(enemyHP));

            Gui.drawRect((int)(x - 140.5), (int)(y - 9.5), (int)(x - 0.5), (int)(y + 30.5f), new Color(31, 31, 31, 255).getRGB());
            Gui.drawRect((int)(x - 99.0f), (int)(y + 6.0f), (int)(x - 2.0f), (int)(y + 15.0f), new Color(40, 40, 40, 255).getRGB());
            Gui.drawRect((int)(x - 99.0f), (int)(y + 6.0f), (int)(x - 99.0f + healthBarWidth), (int)(y + 15.0f), ClickGUI.INSTANCE.getStartColor().getRGB());

            Gui.drawRect((int)(x - 99.0f), (int)(y + 6.0f), (int)(x - 99.0f + hpWidth), (int)(y + 15.0f), ClickGUI.INSTANCE.getStartColor().getRGB());

            font.drawString(healthStr, x - 138.0f + 46.0f - mc.fontRenderer.getStringWidth(healthStr) / 2.0f, y + 19.5f, -1, true, 1.0f);
            font.drawString("\u2764", x - 138.0f + 46.0f + mc.fontRenderer.getStringWidth(healthStr), y + 19.5f, ClickGUI.INSTANCE.getStartColor().getRGB(), true, 1.0f);
            font.drawString(entity.getName(), x - 97, y - 5.0f, -1, true, 1.0f);

            try {
                this.drawHead(Objects.requireNonNull(mc.getConnection()).getPlayerInfo(entity.getUniqueID()).getLocationSkin(), (int)(x - 139), (int)(y - 8));
            } catch (Exception ignored) {}
        }
    }

    private void drawHead(ResourceLocation skin, int width, int height) {
        GL11.glColor4f(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(skin);
        Gui.drawScaledCustomSizeModalRect(width, height, 8, 8, 8, 8, 37, 37, 64, 64);
    }
}