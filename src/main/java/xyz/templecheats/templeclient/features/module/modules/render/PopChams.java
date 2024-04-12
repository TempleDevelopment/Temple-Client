package xyz.templecheats.templeclient.features.module.modules.render;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.event.events.network.PacketEvent;
import xyz.templecheats.templeclient.event.events.render.ArmorEvent;
import xyz.templecheats.templeclient.event.events.render.FireEvent;
import xyz.templecheats.templeclient.event.events.render.HeldItemEvent;
import xyz.templecheats.templeclient.event.events.render.Render3DEvent;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.color.impl.GradientShader;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;

import static org.lwjgl.opengl.GL11.*;

import java.util.HashMap;
import java.util.Map;

public class PopChams extends Module {
    /*
     * Settings
     */
    private final DoubleSetting lineWidth = new DoubleSetting("LineWidth", this, 0.0, 5.0, 1);
    public static boolean rendering;
    /*
     * Variables
     */
    private final HashMap < EntityPlayer, Long > playerList = new HashMap < > ();
    public PopChams() {
        super("PopChams", "Renders a totempop effect", Keyboard.KEY_NONE, Category.Render);

        registerSettings(lineWidth);
    }


    @Listener
    public void onRender3d(Render3DEvent event) { //popchams will now be rendered AFTER shaderesp. fixing the render conflicts
        if (mc.world == null) {
            return;
        }
        for (final Map.Entry < EntityPlayer, Long > entry: new HashMap < > (playerList).entrySet()) {
            final float alpha = (System.currentTimeMillis() - entry.getValue()) / 1000.0f;
            if (alpha > 1.0f) {
                playerList.remove(entry.getKey());
                continue;
            }
            rendering = false;
            GradientShader.setup(Math.max(0.0f, 1.0f - alpha));
            rendering = true;
            glPushMatrix();
            glEnable(GL_BLEND);
            glDisable(GL_TEXTURE_2D);
            glDisable(GL_DEPTH_TEST);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            mc.getRenderManager().renderEntityStatic(entry.getKey(), mc.getRenderPartialTicks(), false);
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
            glLineWidth((float) lineWidth.doubleValue());
            mc.getRenderManager().renderEntityStatic(entry.getKey(), mc.getRenderPartialTicks(), false);
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            glEnable(GL_DEPTH_TEST);
            glEnable(GL_TEXTURE_2D);
            glDisable(GL_BLEND);
            glPopMatrix();
            GradientShader.finish();
            rendering = false;
        }
    }

    @Listener
    public void onPacketReceive(PacketEvent.Receive event) {
        if (mc.world == null) {
            return;
        }
        if (event.getPacket() instanceof SPacketEntityStatus) {
            final SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
            final Entity entity = packet.getEntity(mc.world);
            if (entity instanceof EntityPlayer && packet.getOpCode() == 35) {
                invokeEntity((EntityPlayer) entity);
            }
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
    @Listener
    public void onRenderFire(FireEvent event) {
        event.setCanceled(true);
    }
    @Override
    public void onEnable() {
            invokeEntity(mc.player);
    }
    private void invokeEntity(final EntityPlayer entityPlayer) {
        if (entityPlayer.equals(mc.player)) {
            return;
        }
        final EntityOtherPlayerMP player = new EntityOtherPlayerMP(mc.world, mc.player.getGameProfile());
        player.copyLocationAndAnglesFrom(entityPlayer);

        player.prevRotationYaw = player.rotationYaw;
        player.prevRotationYawHead = player.rotationYawHead;
        player.prevRotationPitch = player.rotationPitch;

        player.setEntityId(-1);
        playerList.put(player, System.currentTimeMillis());
    }

}