package xyz.templecheats.templeclient.features.module.modules.render.esp.sub;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.mixins.accessor.IEntityRenderer;
import xyz.templecheats.templeclient.mixins.accessor.IShaderGroup;
import xyz.templecheats.templeclient.util.render.shader.ShaderHelper;
import xyz.templecheats.templeclient.util.render.shader.impl.GradientShader;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.ColorSetting;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;

import java.awt.*;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Hand extends Module {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    private final BooleanSetting showHand = new BooleanSetting("Show origin", this, true);
    private final ColorSetting outlineColor = new ColorSetting("Color", this, Color.RED);
    private final DoubleSetting blurRadius = new DoubleSetting("Blur Radius", this, 0.0, 16.0, 1.0);
    private final DoubleSetting lineWidth = new DoubleSetting("LineWidth", this, 0.0, 5.0, 1);
    private final DoubleSetting opacity = new DoubleSetting("Opacity", this, 0.0, 1.0, 0.5);

    /****************************************************************
     *                      Variables
     ****************************************************************/
    public static boolean rendering;
    public static Hand INSTANCE;
    private final ShaderHelper shaderHelper = new ShaderHelper(new ResourceLocation("shaders/post/esp_outline.json"), "final");
    private final Framebuffer frameBuffer = this.shaderHelper.getFrameBuffer("final");

    public Hand() {
        super("Hand", "Highlights hand with shader", Keyboard.KEY_NONE, Category.Render, true);
        registerSettings(showHand, outlineColor, blurRadius, lineWidth, opacity);
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onHandRender(RenderSpecificHandEvent event) {
        if (!showHand.booleanValue() && !rendering) event.setCanceled(true);
    }

    @Override
    public void onUpdate() {
        this.setShaderSettings();
    }

    public void drawHand(float partialTicks, int pass) {
        if (mc.gameSettings.thirdPersonView != 0 || mc.world == null) {
            return;
        }

        // Clean up the frame buffer and bind it
        frameBuffer.framebufferClear();
        frameBuffer.bindFramebuffer(false);

        rendering = true;
        ((IEntityRenderer) mc.entityRenderer).invokeRenderHand(partialTicks, pass);
        rendering = false;

        // Setup
        GlStateManager.pushMatrix();
        glLineWidth(1f);
        glEnable(GL_LINE_SMOOTH);
        glEnable(GL32.GL_DEPTH_CLAMP);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(GL_SMOOTH);
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.depthMask(false);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();

        // Push matrix
        GlStateManager.matrixMode(GL_PROJECTION);
        GlStateManager.pushMatrix();
        GlStateManager.matrixMode(GL_MODELVIEW);
        GlStateManager.pushMatrix();

        shaderHelper.shader.render(partialTicks);

        // Re-enable blend because shaders rendering will disable it at the end
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();

        // Draw it on the main frame buffer
        mc.getFramebuffer().bindFramebuffer(false);
        frameBuffer.framebufferRenderExt(mc.displayWidth, mc.displayHeight, false);

        // Revert states
        GlStateManager.enableBlend();
        GlStateManager.enableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GlStateManager.disableCull();

        // Revert matrix
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.popMatrix();

        // Restore
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.enableAlpha();
        GlStateManager.depthMask(true);
        GL11.glDisable(GL32.GL_DEPTH_CLAMP);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.color(1f, 1f, 1f, 1f);
        GL11.glLineWidth(1f);
        GlStateManager.popMatrix();

        GradientShader.setup((float) opacity.doubleValue());
        rendering = true;
        ((IEntityRenderer) mc.entityRenderer).invokeRenderHand(partialTicks, pass);
        rendering = false;
        GradientShader.finish();
    }

    //Don't blame me
    private void setShaderSettings() {
        ShaderGroup group = shaderHelper.shader;
        if (shaderHelper.shader == null) return;
        List<Shader> shaders = ((IShaderGroup) group).getListShaders();
        if (shaders == null) return;

        for (net.minecraft.client.shader.Shader shader : shaders) {
            if (shader.getShaderManager().getShaderUniform("color") != null) {
                shader.getShaderManager().getShaderUniform("color").set(outlineColor.getColor().getRed() / 255f, outlineColor.getColor().getGreen() / 255f, outlineColor.getColor().getBlue() / 255f);
            }
            if (shader.getShaderManager().getShaderUniform("filledAlpha") != null) {
                shader.getShaderManager().getShaderUniform("filledAlpha").set((float) opacity.doubleValue());
            }
            if (shader.getShaderManager().getShaderUniform("outlineAlpha") != null) {
                shader.getShaderManager().getShaderUniform("outlineAlpha").set((float) outlineColor.getColor().getAlpha() / 255f);
            }
            if (shader.getShaderManager().getShaderUniform("width") != null) {
                shader.getShaderManager().getShaderUniform("width").set((float) lineWidth.doubleValue());
            }
            if (shader.getShaderManager().getShaderUniform("Radius") != null) {
                shader.getShaderManager().getShaderUniform("Radius").set(blurRadius.floatValue());
            }
        }
    }
}
