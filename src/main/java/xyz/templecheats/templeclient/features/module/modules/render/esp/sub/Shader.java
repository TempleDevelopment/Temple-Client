package xyz.templecheats.templeclient.features.module.modules.render.esp.sub;

import net.minecraftforge.client.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import org.lwjgl.input.Keyboard;

import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.event.events.render.*;
import xyz.templecheats.templeclient.features.module.Module;

import xyz.templecheats.templeclient.util.render.shader.impl.GradientShader;
import xyz.templecheats.templeclient.util.setting.impl.*;

import java.util.LinkedHashSet;

import static org.lwjgl.opengl.GL11.*;
import static xyz.templecheats.templeclient.util.player.TargetUtil.updateEntityList;

public class Shader extends Module {
    /*
     * Settings
     */
    private final BooleanSetting crystal = new BooleanSetting("Crystals" , this , false);
    private final BooleanSetting self = new BooleanSetting("Self" , this , false);
    private final BooleanSetting player = new BooleanSetting("Players" , this , false);
    private final BooleanSetting items = new BooleanSetting("Items" , this , false);
    private final BooleanSetting hostiles = new BooleanSetting("Hostiles" , this , false);
    private final BooleanSetting animals = new BooleanSetting("Animals" , this , false);
    private final DoubleSetting range = new DoubleSetting("Range", this, 8.0, 256.0, 32.0);
    private final DoubleSetting lineWidth = new DoubleSetting("LineWidth" , this , 0.0 , 5.0 , 1);
    private final DoubleSetting opacity = new DoubleSetting("Opacity" , this , 0.0 , 1.0 , 0.5);

    private final LinkedHashSet<Entity> entityList = new LinkedHashSet<>();
    public static boolean rendering;
    public static Shader INSTANCE;

    public Shader() {
        super("Shader" , "Highlights players and crystals" , Keyboard.KEY_NONE , Category.Render , true);
        registerSettings(self, player, crystal, items, hostiles, animals, range, lineWidth, opacity);
        INSTANCE = this;
    }

    @Listener
    public void onNamePlate(NamePlateEvent event) {
        if (rendering && player.booleanValue())
            event.setCanceled(true);
    }

    @Override
    public void onUpdate() {
        updateEntityList(entityList, crystal.booleanValue(), self.booleanValue(), player.booleanValue(), items.booleanValue(), hostiles.booleanValue(), animals.booleanValue(), range.doubleValue());
    }

    @Listener
    public void onRender3d(Render3DEvent event) {
        if (mc.gameSettings.thirdPersonView != 0 || mc.world == null) {
            return;
        }
        rendering = true;

        for (Entity entity : entityList) {
            GradientShader.setup(opacity.floatValue());
            try {
                glPushMatrix();
                glEnable(GL_BLEND);
                glDisable(GL_TEXTURE_2D);
                glDisable(GL_DEPTH_TEST);
                glBlendFunc(GL_SRC_ALPHA , GL_ONE_MINUS_SRC_ALPHA);

                mc.getRenderManager().renderEntityStatic(entity, mc.getRenderPartialTicks(), false);

                glEnable(GL_DEPTH_TEST);
                glEnable(GL_TEXTURE_2D);
                glDisable(GL_BLEND);
                glPopMatrix();

                glPushMatrix();
                glEnable(GL_BLEND);
                glDisable(GL_TEXTURE_2D);
                glDisable(GL_DEPTH_TEST);
                glPolygonMode(GL_FRONT_AND_BACK , GL_LINE);
                glEnable(GL_LINE_SMOOTH);
                glHint(GL_LINE_SMOOTH_HINT , GL_NICEST);
                glLineWidth(lineWidth.floatValue());

                mc.getRenderManager().renderEntityStatic(entity, mc.getRenderPartialTicks(), false);

                glPolygonMode(GL_FRONT_AND_BACK , GL_FILL);
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
    public void onRenderHeldItem(HeldItemEvent event) {event.setCanceled(true);}

    @Listener
    public void onRenderFire(FireEvent event) {event.setCanceled(true);}
}