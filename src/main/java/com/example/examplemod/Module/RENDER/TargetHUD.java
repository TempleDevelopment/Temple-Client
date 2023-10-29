package com.example.examplemod.Module.RENDER;

import com.example.examplemod.Module.Module;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Objects;

public class TargetHUD extends Module {
    private boolean show;
    private double healthBarWidth;
    private String enemyNickname;
    private double enemyHP;
    private double enemyDistance;
    private EntityPlayer entityPlayer;
    private Entity entity;

    public TargetHUD() {
        super("TargetHUD", Keyboard.KEY_NONE, Category.RENDER);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        RayTraceResult objectMouseOver = mc.objectMouseOver;

        if (objectMouseOver != null) {
            if (objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY) {
                entity = objectMouseOver.entityHit;
                if (entity instanceof EntityPlayer) {
                    entityPlayer = (EntityPlayer) entity;
                    enemyNickname = entityPlayer.getName();
                    enemyHP = entityPlayer.getHealth();
                    enemyDistance = entityPlayer.getDistance(mc.player);
                    show = true;
                } else {
                    show = false;
                }
            } else {
                show = false;
            }
        }
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent e) {
        if (!e.getType().equals(e.getType().TEXT)) {
            return;
        } if (show && mc.world != null && mc.player != null) {
            double posX = 60;
            double posY = -250;

            ScaledResolution sr = new ScaledResolution(mc);

            final float scaledWidth = sr.getScaledWidth();

            final float x = (float) (scaledWidth / 2.0f - posX);
            final float y = (float) (scaledWidth / 2.0f + posY);

            final float health = Math.round(enemyHP);
            double hpPercentage = health / 20;

            hpPercentage = MathHelper.clamp(hpPercentage, 0, 1);
            final double hpWidth = 97.0 * hpPercentage;

            final String healthStr = String.valueOf(Math.round(enemyHP));

            Gui.drawRect((int) (x + 125.5), (int) (y - 9.5), (int) (x + 265), (int) (y + 30.5f), new Color(31, 31, 31, 255).getRGB());
            Gui.drawRect((int) (x + 166.0f), (int) (y + 6.0f), (int) (x + 263.0f), (int) (y + 15.0f), new Color(40, 40, 40, 255).getRGB());
            Gui.drawRect((int) (x + 166.0f), (int) (y + 6.0f), (int) (x + 166.0f + this.healthBarWidth), (int) (y + 15.0f), new Color(0x2AFA00).getRGB());

            Gui.drawRect((int) (x + 166.0f), (int) (y + 6.0f), (int) (x + 166.0f + hpWidth), (int) (y + 15.0f), new Color(0x2AFA00).getRGB());

            mc.fontRenderer.drawStringWithShadow(healthStr, x + 128.0f + 46.0f - mc.fontRenderer.getStringWidth(healthStr) / 2.0f, y + 19.5f, -1);
            mc.fontRenderer.drawStringWithShadow("\u2764", x + 128.0f + 46.0f + mc.fontRenderer.getStringWidth(healthStr), y + 19.5f, new Color(0x2AFA00).getRGB());
            mc.fontRenderer.drawStringWithShadow(entity.getName(), x + 167, y - 5.0f, -1);

            try {
                drawHead(Objects.requireNonNull(mc.getConnection()).getPlayerInfo(entity.getUniqueID()).getLocationSkin(), (int) (x + 127), (int) (y - 8));
            } catch (Exception ignored) {}
        }
    }

    public void drawHead(ResourceLocation skin, int width, int height) {
        GL11.glColor4f(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(skin);
        Gui.drawScaledCustomSizeModalRect(width, height, 8, 8, 8, 8, 37, 37, 64, 64);
    }
}