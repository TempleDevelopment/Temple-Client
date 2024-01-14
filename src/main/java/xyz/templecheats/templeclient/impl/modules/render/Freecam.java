package xyz.templecheats.templeclient.impl.modules.render;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.play.client.CPacketPlayer;
import org.lwjgl.input.Keyboard;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.api.event.events.network.PacketEvent;
import xyz.templecheats.templeclient.api.event.events.player.MoveEvent;
import xyz.templecheats.templeclient.impl.gui.clickgui.setting.Setting;
import xyz.templecheats.templeclient.impl.modules.Module;

public class Freecam extends Module {

    private Setting horizontalSpeed;
    private Setting verticalSpeed;
    double startX, startY, startZ;
    EntityOtherPlayerMP entity;

    public Freecam() {
        super("Freecam", Keyboard.KEY_NONE, Category.RENDER);
        horizontalSpeed = new Setting("Horizontal Speed", this, 1.0, 0.1, 5.0, false);
        verticalSpeed = new Setting("Vertical Speed", this, 1.0, 0.1, 5.0, false);
    }

    @Listener
    public void onMove(MoveEvent event) {
        mc.player.noClip = true;
        mc.player.onGround = false;
        mc.player.capabilities.isFlying = true;
    }

    @Listener
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer) {
            event.setCanceled(true);
        }
    }

    @Override
    public void onUpdate(){
        controlMovement();
    }

    @Override
    public void onEnable() {
        entity = new EntityOtherPlayerMP(mc.world, mc.getSession().getProfile());
        entity.copyLocationAndAnglesFrom(mc.player);
        entity.rotationYaw = mc.player.rotationYaw;
        entity.rotationYawHead = mc.player.rotationYawHead;
        mc.world.addEntityToWorld(696984837, entity);
        mc.player.capabilities.isFlying = true;
        mc.player.capabilities.allowFlying = true;
        startX = mc.player.posX;
        startY = mc.player.posZ;
        startZ = mc.player.posZ;
    }

    @Override
    public void onDisable() {
        mc.player.noClip = false;
        mc.player.capabilities.allowFlying = false;
        mc.player.capabilities.isFlying = false;
        mc.player.setPosition(startX, startY, startZ);
        mc.world.removeEntity(entity);
    }

    private void controlMovement() {
        double hSpeed = horizontalSpeed.getValDouble();
        double vSpeed = verticalSpeed.getValDouble();

        if (mc.gameSettings.keyBindForward.isKeyDown()) {
            mc.player.motionX = hSpeed * Math.cos(Math.toRadians(mc.player.rotationYaw + 90));
            mc.player.motionZ = hSpeed * Math.sin(Math.toRadians(mc.player.rotationYaw + 90));
        } else if (mc.gameSettings.keyBindBack.isKeyDown()) {
            mc.player.motionX = -hSpeed * Math.cos(Math.toRadians(mc.player.rotationYaw + 90));
            mc.player.motionZ = -hSpeed * Math.sin(Math.toRadians(mc.player.rotationYaw + 90));
        } else {
            mc.player.motionX = 0;
            mc.player.motionZ = 0;
        }

        if (mc.gameSettings.keyBindLeft.isKeyDown()) {
            mc.player.motionX += hSpeed * Math.cos(Math.toRadians(mc.player.rotationYaw));
            mc.player.motionZ += hSpeed * Math.sin(Math.toRadians(mc.player.rotationYaw));
        } else if (mc.gameSettings.keyBindRight.isKeyDown()) {
            mc.player.motionX -= hSpeed * Math.cos(Math.toRadians(mc.player.rotationYaw));
            mc.player.motionZ -= hSpeed * Math.sin(Math.toRadians(mc.player.rotationYaw));
        }

        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.player.motionY = vSpeed;
        } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.player.motionY = -vSpeed;
        } else {
            mc.player.motionY = 0;
        }
    }
}