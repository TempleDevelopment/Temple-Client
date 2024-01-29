package xyz.templecheats.templeclient.impl.modules.render;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.play.client.CPacketPlayer;
import org.lwjgl.input.Keyboard;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.api.event.EventStageable;
import xyz.templecheats.templeclient.api.event.events.network.PacketEvent;
import xyz.templecheats.templeclient.api.event.events.player.MotionEvent;
import xyz.templecheats.templeclient.api.event.events.player.MoveEvent;
import xyz.templecheats.templeclient.impl.gui.setting.Setting;
import xyz.templecheats.templeclient.impl.modules.Module;

public class Freecam extends Module {
    private EntityOtherPlayerMP fakePlayer;
    private float startYaw, startPitch;

    private Setting horizontalSpeed = new Setting("Horizontal Speed", this, 1.0, 0.1, 5.0, false);
    private Setting verticalSpeed = new Setting("Vertical Speed", this, 1.0, 0.1, 5.0, false);

    public Freecam() {
        super("Freecam", "Out of body experience", Keyboard.KEY_NONE, Category.RENDER);
        this.registerSettings(horizontalSpeed, verticalSpeed);
    }

    @Listener
    public void onPacketSend(PacketEvent.Send event) {
        if(event.getPacket() instanceof CPacketPlayer) {
            event.setCanceled(true);
        }
    }

    @Listener
    public void onMove(MoveEvent event) {
        mc.player.noClip = true;
        mc.player.onGround = false;
        mc.player.fallDistance = 0;

        final double hSpeed = this.horizontalSpeed.getValDouble() * 2;

        if(mc.gameSettings.keyBindForward.isKeyDown()) {
            mc.player.motionX = hSpeed * Math.cos(Math.toRadians(mc.player.rotationYaw + 90));
            mc.player.motionZ = hSpeed * Math.sin(Math.toRadians(mc.player.rotationYaw + 90));
        } else if(mc.gameSettings.keyBindBack.isKeyDown()) {
            mc.player.motionX = -hSpeed * Math.cos(Math.toRadians(mc.player.rotationYaw + 90));
            mc.player.motionZ = -hSpeed * Math.sin(Math.toRadians(mc.player.rotationYaw + 90));
        } else {
            mc.player.motionX = 0;
            mc.player.motionZ = 0;
        }

        if(mc.gameSettings.keyBindLeft.isKeyDown()) {
            mc.player.motionX += hSpeed * Math.cos(Math.toRadians(mc.player.rotationYaw));
            mc.player.motionZ += hSpeed * Math.sin(Math.toRadians(mc.player.rotationYaw));
        } else if(mc.gameSettings.keyBindRight.isKeyDown()) {
            mc.player.motionX -= hSpeed * Math.cos(Math.toRadians(mc.player.rotationYaw));
            mc.player.motionZ -= hSpeed * Math.sin(Math.toRadians(mc.player.rotationYaw));
        }
    }

    @Listener
    public void onMotion(MotionEvent event) {
        if(event.getStage() != EventStageable.EventStage.POST) {
            return;
        }

        final double vSpeed = this.verticalSpeed.getValDouble() * 2;

        if(mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.player.motionY = vSpeed;
        } else if(mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.player.motionY = -vSpeed;
        } else {
            mc.player.motionY = 0;
        }

        if(this.fakePlayer != null) {
            this.fakePlayer.rotationYaw = this.fakePlayer.rotationYawHead = mc.player.rotationYaw;
            this.fakePlayer.rotationPitch = mc.player.rotationPitch;
            this.fakePlayer.inventory.copyInventory(mc.player.inventory);
            this.fakePlayer.inventory.currentItem = mc.player.inventory.currentItem;
        }
    }

    @Override
    public void onEnable() {
        mc.player.noClip = true;

        this.fakePlayer = new EntityOtherPlayerMP(mc.world, mc.getSession().getProfile());
        this.fakePlayer.copyLocationAndAnglesFrom(mc.player);
        this.startYaw = mc.player.rotationYaw;
        this.startPitch = mc.player.rotationPitch;
        mc.world.addEntityToWorld(696984837, this.fakePlayer);
    }

    @Override
    public void onDisable() {
        mc.player.noClip = false;

        mc.player.copyLocationAndAnglesFrom(this.fakePlayer);
        mc.player.rotationYaw = this.startYaw;
        mc.player.rotationPitch = this.startPitch;
        mc.world.removeEntity(this.fakePlayer);
        this.fakePlayer = null;
    }
}