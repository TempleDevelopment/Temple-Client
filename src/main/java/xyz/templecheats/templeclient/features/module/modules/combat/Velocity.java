package xyz.templecheats.templeclient.features.module.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.event.events.network.PacketEvent;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.mixins.accessor.IMixinSPacketEntityVelocity;
import xyz.templecheats.templeclient.mixins.accessor.IMixinSPacketExplosion;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;

public final class Velocity extends Module {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", this, Mode.Packet);
    private boolean cancelVelocity = false;

    /****************************************************************
     *                      Variables
     ****************************************************************/

    public final Minecraft mc = Minecraft.getMinecraft();

    public Velocity() {
        super("Velocity", "Reduces knockback velocity", Keyboard.KEY_NONE, Category.Combat);
        registerSettings(mode);
    }

    @Listener
    public void onPacketReceive(PacketEvent.Receive event) {
        if (mc.player == null) return;

        if (event.getPacket() instanceof SPacketEntityVelocity) {
            SPacketEntityVelocity velocityPacket = (SPacketEntityVelocity) event.getPacket();
            if (velocityPacket.getEntityID() == mc.player.getEntityId()) {
                handleVelocityPacket(event, velocityPacket);
            }
        } else if (event.getPacket() instanceof SPacketExplosion) {
            SPacketExplosion explosionPacket = (SPacketExplosion) event.getPacket();
            handleExplosionPacket(event, explosionPacket);
        }
    }

    private void handleVelocityPacket(PacketEvent.Receive event, SPacketEntityVelocity velocityPacket) {
        switch (mode.value()) {
            case Packet:
                event.setCanceled(true);
                break;
            case Grim:
                event.setCanceled(true);
                cancelVelocity = true;
                break;
        }
    }

    private void handleExplosionPacket(PacketEvent.Receive event, SPacketExplosion explosionPacket) {
        switch (mode.value()) {
            case Packet:
                event.setCanceled(true);
                break;
            case Grim:
                event.setCanceled(true);
                cancelVelocity = true;
                break;
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START && cancelVelocity && mode.value() == Mode.Grim) {
            cancelVelocity = false;
            float yaw = mc.player.rotationYaw;
            float pitch = mc.player.rotationPitch;
            mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX, mc.player.getEntityBoundingBox().minY, mc.player.posZ, yaw, pitch, mc.player.onGround));
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
        }
    }

    @Override
    public String getHudInfo() {
        return ChatFormatting.WHITE + mode.value().name() + ChatFormatting.RESET;
    }

    public enum Mode {
        Packet,
        Grim
    }
}
