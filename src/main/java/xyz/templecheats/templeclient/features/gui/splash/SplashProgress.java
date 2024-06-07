package xyz.templecheats.templeclient.features.gui.splash;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class SplashProgress {
    private static final ResourceLocation LOGO = new ResourceLocation("textures/icons/logo.png");
    private static TextureManager textureManager;
    private static int progress;
    private static final int MAX_PROGRESS = 6;
    private static String current;

    public static void setProgress(int givenProgress, String givenText) {
        progress = givenProgress;
        current = givenText;
        drawSplash(Minecraft.getMinecraft().getTextureManager());
    }

    public static void drawSplash(TextureManager tm) {
        if (textureManager == null) {
            textureManager = tm;
        }

        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int scaleFactor = sr.getScaleFactor();
        GlStateManager.clearColor(0, 0, 0, 1);
        GlStateManager.clear(16384);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0, sr.getScaledWidth_double(), sr.getScaledHeight_double(), 0.0, 1000.0, 3000.0);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0f, 0.0f, -2000.0f);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.disableDepth();
        GlStateManager.enableTexture2D();

        tm.bindTexture(LOGO);
        GlStateManager.color(1, 1, 1, 1);
        Gui.drawModalRectWithCustomSizedTexture(sr.getScaledWidth() / 2 - 50, sr.getScaledHeight() / 2 - 50, 0, 0, 100, 100, 100, 100);

        drawProgressBar();

        Minecraft.getMinecraft().updateDisplay();
    }

    private static void drawProgressBar() {
        if (Minecraft.getMinecraft().gameSettings == null) {
            return;
        }
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int barWidth = sr.getScaledWidth() - 100;
        int barHeight = 10;
        int x = 50;
        int y = sr.getScaledHeight() - 60;

        float progressPercentage = (float) progress / MAX_PROGRESS;
        int filled = (int) (progressPercentage * barWidth);

        GlStateManager.color(1, 1, 1, 1);
        Gui.drawRect(x, y, x + filled, y + barHeight, new Color(255, 255, 255, 255).getRGB());
        Gui.drawRect(x + filled, y, x + barWidth, y + barHeight, new Color(255, 255, 255, 100).getRGB());
    }
}
