package xyz.templecheats.templeclient.impl.modules.render;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.play.client.CPacketPlayer;
import org.lwjgl.input.Keyboard;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.api.event.EventStageable;
import xyz.templecheats.templeclient.api.event.events.network.PacketEvent;
import xyz.templecheats.templeclient.api.event.events.player.MotionEvent;
import xyz.templecheats.templeclient.api.event.events.player.MoveEvent;
import xyz.templecheats.templeclient.impl.modules.Module;

public class Freecam extends Module {
    private EntityOtherPlayerMP fakePlayer;
    private float startYaw, startPitch;

    public Freecam() {
        super("Freecam", Keyboard.KEY_NONE, Category.RENDER);
    }

    @Listener
    public void onPacketSend(PacketEvent.Send event) {
        if(event.getPacket() instanceof CPacketPlayer) {
            event.setCanceled(true);
        }
    }

    @Listener
    public void onMove(MoveEvent event) {
        mc.player.noClip = mc.player.capabilities.isFlying = mc.player.capabilities.allowFlying = true;
        mc.player.onGround = false;
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