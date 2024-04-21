package xyz.templecheats.templeclient.features.module.modules.render.esp.sub;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderUniform;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;
import scala.Array;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.event.events.render.*;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.mixins.accessor.IEntityRenderer;
import xyz.templecheats.templeclient.mixins.accessor.IShaderGroup;
import xyz.templecheats.templeclient.util.color.ShaderHelper;
import xyz.templecheats.templeclient.util.color.impl.GradientShader;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.util.setting.impl.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class Shader extends Module {
    /*
     * Settings
     */
    private final BooleanSetting crystal = new BooleanSetting("Crystals", this, false);
    private final BooleanSetting player = new BooleanSetting("Players", this, false);
    public final BooleanSetting hand = new BooleanSetting("Hand", this, false);
    private final ColorSetting outlineColor = new ColorSetting("Color", this, Color.RED);
    private final DoubleSetting lineWidth = new DoubleSetting("LineWidth", this, 0.0, 5.0, 1);
    private final DoubleSetting opacity = new DoubleSetting("Opacity", this, 0.0, 1.0, 0.5);
    public static boolean rendering;
    public static Shader INSTANCE;

    public Shader() {
        super("Shader", "Highlights players and crystals", Keyboard.KEY_NONE, Category.Render, true);
        registerSettings(player, crystal, hand, outlineColor, lineWidth, opacity);
        INSTANCE = this;
    }

    @Listener
    public void onNamePlate(NamePlateEvent event) {
        if (rendering && player.booleanValue())
            event.setCanceled(true);
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