package xyz.templecheats.templeclient.features.modules.render;

import xyz.templecheats.templeclient.event.events.PacketEvent;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;
import team.temple.enterprise.handler.Listener;
import xyz.templecheats.templeclient.features.modules.Module;

public class Freecam extends Module {

    double startX, startY, startZ;
    EntityOtherPlayerMP entity;

    public Freecam() {
        super("Freecam", Keyboard.KEY_Z, Category.RENDER);
    }

    private static Vec3d pos;
    private static Vec2f pitchyaw;
    private static boolean isRidingEntity;
    public static boolean enabled;
    private static Entity ridingEntity;
    private static EntityOtherPlayerMP originalPlayer;
    private Vec3d originalPlayerPos;

    public void onEnable() {
        if (mc.player == null || mc.world == null) {
            return;
        }
        enabled = true;
        if (isRidingEntity = mc.player.isRiding()) {
            ridingEntity = mc.player.getRidingEntity();
            mc.player.dismountRidingEntity();
        }
        pos = mc.player.getPositionVector();
        pitchyaw = mc.player.getPitchYaw();
        (originalPlayer = new EntityOtherPlayerMP(mc.world, mc.getSession().getProfile())).copyLocationAndAnglesFrom(mc.player);
        originalPlayer.rotationYawHead = mc.player.rotationYawHead;
        originalPlayer.inventory = mc.player.inventory;
        originalPlayer.inventoryContainer = mc.player.inventoryContainer;
        mc.world.addEntityToWorld(-100, originalPlayer);
        originalPlayerPos = mc.player.getPositionVector();
    }

    public void onDisable() {
        final EntityPlayerSP[] player = new EntityPlayerSP[1];
        final PlayerCapabilities[] gmCaps = new PlayerCapabilities[1];
        final PlayerCapabilities[] capabilities = new PlayerCapabilities[1];
        mc.addScheduledTask(() -> {
            player[0] = mc.player;
            if (player[0] == null || player[0].capabilities == null) {
                return;
            }
            else {
                gmCaps[0] = new PlayerCapabilities();
                mc.playerController.getCurrentGameType().configurePlayerCapabilities(gmCaps[0]);
                capabilities[0] = player[0].capabilities;
                capabilities[0].allowFlying = gmCaps[0].allowFlying;
                capabilities[0].isFlying = (gmCaps[0].allowFlying && capabilities[0].isFlying);
                capabilities[0].setFlySpeed(gmCaps[0].getFlySpeed());
                return;
            }
        });
        if (mc.player == null || originalPlayer == null) {
            return;
        }
        enabled = false;
        mc.world.removeEntityFromWorld(-100);
        originalPlayer = null;
        mc.player.noClip = false;
        mc.player.setVelocity(0.0, 0.0, 0.0);
        if (isRidingEntity) {
            mc.player.startRiding(ridingEntity, true);
            ridingEntity = null;
            isRidingEntity = false;
        }
        if (originalPlayerPos != null && mc.player != null) {
            mc.player.setPosition(originalPlayerPos.x, originalPlayerPos.y, originalPlayerPos.z);
        }
        originalPlayerPos = null;
    }

    @Override
    public void onUpdate() {
        mc.addScheduledTask(() -> {
            if (mc.player == null || mc.player.capabilities == null) {
                return;
            }
            else {
                mc.player.capabilities.allowFlying = true;
                mc.player.capabilities.isFlying = true;
                return;
            }
        });
        mc.player.capabilities.setFlySpeed(0.5f);
        mc.player.noClip = true;
        mc.player.onGround = false;
        mc.player.fallDistance = 0.0f;
        if (!mc.gameSettings.keyBindForward.isPressed() && !mc.gameSettings.keyBindBack.isPressed() && !mc.gameSettings.keyBindLeft.isPressed() && !mc.gameSettings.keyBindRight.isPressed() && !mc.gameSettings.keyBindJump.isPressed() && !mc.gameSettings.keyBindSneak.isPressed()) {
            mc.player.setVelocity(0.0, 0.0, 0.0);
        }
        pos = mc.player.getPositionVector();
    }

    @Listener
    public void onUpdate(PacketEvent.Receive event) {
        if (!enabled || originalPlayer == null || mc.player == null) {
            return;
        }
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            final SPacketPlayerPosLook packet = (SPacketPlayerPosLook)event.getPacket();
            pos = new Vec3d(packet.getX(), packet.getY(), packet.getZ());
            event.setCanceled(true);
        }
    }


    static {
        pos = Vec3d.ZERO;
        pitchyaw = Vec2f.ZERO;
        enabled = false;
    }

}