package xyz.templecheats.templeclient.features.module.modules.render;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.play.client.CPacketPlayer;
import org.lwjgl.input.Keyboard;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.event.EventStageable;
import xyz.templecheats.templeclient.event.events.network.PacketEvent;
import xyz.templecheats.templeclient.event.events.player.MotionEvent;
import xyz.templecheats.templeclient.event.events.player.MoveEvent;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.movement.*;
import xyz.templecheats.templeclient.features.module.modules.movement.speed.Speed;
import xyz.templecheats.templeclient.features.module.modules.movement.speed.sub.Bhop;
import xyz.templecheats.templeclient.features.module.modules.movement.speed.sub.Strafe;
import xyz.templecheats.templeclient.manager.ModuleManager;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;

public class Freecam extends Module {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    private final DoubleSetting horizontalSpeed = new DoubleSetting("Horizontal Speed", this, 0.1, 5d, 1d);
    private final DoubleSetting verticalSpeed = new DoubleSetting("Vertical Speed", this, 0.1, 5d, 1d);

    /****************************************************************
     *                      Variables
     ****************************************************************/
    private EntityOtherPlayerMP fakePlayer;
    private float startYaw, startPitch;

    private static boolean isFreecamActive = false;

    public Freecam() {
        super("Freecam", "Out of body experience", Keyboard.KEY_NONE, Category.Render);
        this.registerSettings(horizontalSpeed, verticalSpeed);
    }

    public static boolean isFreecamActive() {
        return isFreecamActive;
    }

    @Listener
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer) {
            event.setCanceled(true);
        }
    }

    @Listener
    public void onMove(MoveEvent event) {
        mc.player.noClip = true;
        mc.player.onGround = false;
        mc.player.fallDistance = 0;

        final double hSpeed = this.horizontalSpeed.doubleValue() * 2;

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
    }

    @Listener
    public void onMotion(MotionEvent event) {
        if (event.getStage() != EventStageable.EventStage.POST) {
            return;
        }

        final double vSpeed = this.verticalSpeed.doubleValue() * 2;

        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.player.motionY = vSpeed;
        } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.player.motionY = -vSpeed;
        } else {
            mc.player.motionY = 0;
        }

        if (this.fakePlayer != null) {
            this.fakePlayer.rotationYaw = this.fakePlayer.rotationYawHead = mc.player.rotationYaw;
            this.fakePlayer.rotationPitch = mc.player.rotationPitch;
            this.fakePlayer.inventory.copyInventory(mc.player.inventory);
            this.fakePlayer.inventory.currentItem = mc.player.inventory.currentItem;
        }
    }

    @Override
    public void onEnable() {
        isFreecamActive = true;
        mc.player.noClip = true;

        this.fakePlayer = new EntityOtherPlayerMP(mc.world, mc.getSession().getProfile());
        this.fakePlayer.copyLocationAndAnglesFrom(mc.player);
        this.startYaw = mc.player.rotationYaw;
        this.startPitch = mc.player.rotationPitch;
        mc.world.addEntityToWorld(696984837, this.fakePlayer);
    }

    @Override
    public void onDisable() {
        isFreecamActive = false;
        mc.player.noClip = false;

        mc.player.copyLocationAndAnglesFrom(this.fakePlayer);
        mc.player.rotationYaw = this.startYaw;
        mc.player.rotationPitch = this.startPitch;
        mc.world.removeEntity(this.fakePlayer);
        this.fakePlayer = null;
    }
}
