package xyz.templecheats.templeclient.impl.modules.render;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.play.client.CPacketPlayer;
import org.lwjgl.input.Keyboard;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.api.event.EventStageable;
import xyz.templecheats.templeclient.api.event.events.network.PacketEvent;
import xyz.templecheats.templeclient.api.event.events.player.MotionEvent;
import xyz.templecheats.templeclient.api.event.events.player.MoveEvent;
import xyz.templecheats.templeclient.impl.gui.clickgui.setting.Setting;
import xyz.templecheats.templeclient.impl.modules.Module;

public class Freecam extends Module {
    private EntityOtherPlayerMP fakePlayer;
    private float startYaw, startPitch;

    private Setting horizontalSpeed;
    private Setting verticalSpeed;

    public Freecam() {
        super("Freecam", Keyboard.KEY_NONE, Category.RENDER);
        horizontalSpeed = new Setting("Horizontal Speed", this, 1.0, 0.1, 5.0, false);
        verticalSpeed = new Setting("Vertical Speed", this, 1.0, 0.1, 5.0, false);
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
        mc.player.capabilities.isFlying = true;

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

    @Listener
    public void onMotion(MotionEvent event) {
        if(event.getStage() != EventStageable.EventStage.POST || this.fakePlayer == null) {
            return;
        }

        this.fakePlayer.rotationYaw = this.fakePlayer.rotationYawHead = mc.player.rotationYaw;
        this.fakePlayer.rotationPitch = mc.player.rotationPitch;
        this.fakePlayer.inventory.copyInventory(mc.player.inventory);
        this.fakePlayer.inventory.currentItem = mc.player.inventory.currentItem;
    }

    @Override
    public void onEnable() {
        mc.player.noClip = mc.player.capabilities.isFlying = mc.player.capabilities.allowFlying = true;

        this.fakePlayer = new EntityOtherPlayerMP(mc.world, mc.getSession().getProfile());
        this.fakePlayer.copyLocationAndAnglesFrom(mc.player);
        this.startYaw = mc.player.rotationYaw;
        this.startPitch = mc.player.rotationPitch;
        mc.world.addEntityToWorld(696984837, this.fakePlayer);
    }

    @Override
    public void onDisable() {
        mc.player.noClip = mc.player.capabilities.isFlying = mc.player.capabilities.allowFlying = false;

        mc.player.copyLocationAndAnglesFrom(this.fakePlayer);
        mc.player.rotationYaw = this.startYaw;
        mc.player.rotationPitch = this.startPitch;
        mc.world.removeEntity(this.fakePlayer);
        this.fakePlayer = null;
    }
}