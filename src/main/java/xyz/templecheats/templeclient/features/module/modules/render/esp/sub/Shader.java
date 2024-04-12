package xyz.templecheats.templeclient.features.module.modules.render.esp.sub;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.event.events.render.*;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.mixins.accessor.IEntityRenderer;
import xyz.templecheats.templeclient.util.color.impl.GradientShader;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.util.setting.impl.*;

import static org.lwjgl.opengl.GL11.*;

public class Shader extends Module {
    /*
     * Settings
     */
    private final BooleanSetting crystal = new BooleanSetting("Crystals", this, false);
    private final BooleanSetting player = new BooleanSetting("Players", this, false);
    public final BooleanSetting hand = new BooleanSetting("Hand", this, false);
    private final DoubleSetting lineWidth = new DoubleSetting("LineWidth", this, 0.0, 5.0, 1);
    private final DoubleSetting opacity = new DoubleSetting("Opacity", this, 0.0, 1.0, 0.5);
    public static boolean rendering;
    public static Shader INSTANCE;
    Framebuffer buffer = new Framebuffer(1, 1, false);
    ShaderGroup shader;
    public Shader() {
        super("Shader", "Highlights players and crystals", Keyboard.KEY_NONE, Category.Render, true);
        registerSettings(player, crystal, hand, lineWidth, opacity);
        INSTANCE = this;
    }

    @Listener
    public void onNamePlate(NamePlateEvent event) {
        if (rendering && player.booleanValue())
            event.setCanceled(true);
    }

    // ZANE will help me fix that <3
    public void drawHand(float partialTicks, int pass) {
        if (mc.gameSettings.thirdPersonView != 0 || mc.world == null) {
            return;
        }

        // Clean up the frame buffer and bind it
       // buffer.framebufferClear();
        //buffer.bindFramebuffer(false);

        rendering = true;
        GradientShader.setup((float) opacity.doubleValue());
        try {
            // Setup
            RenderUtil.bindBlank();
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

            // Re-enable blend because shader rendering will disable it at the end
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();

            // Draw it on the main frame buffer
            //RenderUtil.bindBlank();
            //mc.getFramebuffer().bindFramebuffer(false);
           // buffer.framebufferRenderExt(mc.displayWidth, mc.displayHeight, false);

            ((IEntityRenderer) mc.entityRenderer).invokeRenderHand(partialTicks, pass);

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
        } catch (Exception ignoredException) {
        }
        GradientShader.finish();
        rendering = false;
    }

    @Listener
    public void onRender3d(Render3DPreEvent event) {
        if (mc.gameSettings.thirdPersonView != 0 || mc.world == null) {
            return;
        }
        rendering = true;
        for (final Entity entity : mc.world.loadedEntityList) {
            if (entity != null && ((!entity.equals(mc.player) && entity instanceof EntityPlayer && player.booleanValue()) || (entity instanceof EntityEnderCrystal && crystal.booleanValue()))) {
                GradientShader.setup((float) opacity.doubleValue());
                try {
                    glPushMatrix();
                    glEnable(GL_BLEND);
                    glDisable(GL_TEXTURE_2D);
                    glDisable(GL_DEPTH_TEST);
                    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

                    mc.getRenderManager().renderEntityStatic(entity, mc.getRenderPartialTicks(), false);

                    glEnable(GL_DEPTH_TEST);
                    glEnable(GL_TEXTURE_2D);
                    glDisable(GL_BLEND);
                    glPopMatrix();

                    glPushMatrix();
                    glEnable(GL_BLEND);
                    glDisable(GL_TEXTURE_2D);
                    glDisable(GL_DEPTH_TEST);
                    glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                    glEnable(GL_LINE_SMOOTH);
                    glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
                    glLineWidth(lineWidth.floatValue());

                    mc.getRenderManager().renderEntityStatic(entity, mc.getRenderPartialTicks(), false);

                    glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
                    glEnable(GL_DEPTH_TEST);
                    glEnable(GL_TEXTURE_2D);
                    glDisable(GL_BLEND);
                    glPopMatrix();
                } catch (Exception ignoredException) {
                }
                GradientShader.finish();
                rendering = false;
            }
        }
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderBlockOverlayEvent event) {
        if (event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.FIRE || event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.WATER) {
            event.setCanceled(true);
        }
    }

    @Listener
    public void onRenderArmor(ArmorEvent event) {
        event.setCanceled(true);
    }

    @Listener
    public void onRenderHeldItem(HeldItemEvent event) {
        event.setCanceled(true);
    }
}