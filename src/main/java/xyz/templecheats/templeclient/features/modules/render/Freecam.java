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
    private static Vec3d pos;
    private static Vec2f pitchyaw;
    private static boolean isRidingEntity;
    public static boolean enabled;
    private static Entity ridingEntity;
    private static EntityOtherPlayerMP originalPlayer;
    private Vec3d originalPlayerPos;
    private PlayerCapabilities originalCapabilities;

    public Freecam() {
        super("Freecam", Keyboard.KEY_Z, Category.RENDER);
    }

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
        originalPlayer = new EntityOtherPlayerMP(mc.world, mc.getSession().getProfile());
        originalPlayer.copyLocationAndAnglesFrom(mc.player);
        originalPlayer.rotationYawHead = mc.player.rotationYawHead;
        originalPlayer.inventory = mc.player.inventory;
        originalPlayer.inventoryContainer = mc.player.inventoryContainer;
        mc.world.addEntityToWorld(-100, originalPlayer);
        originalPlayerPos = mc.player.getPositionVector();
        if (mc.player != null) {
            originalCapabilities = new PlayerCapabilities();
            originalCapabilities.allowFlying = mc.player.capabilities.allowFlying;
            originalCapabilities.isFlying = mc.player.capabilities.isFlying;
            originalCapabilities.setFlySpeed(mc.player.capabilities.getFlySpeed());
        }
    }

    public void onDisable() {
        if (mc.player == null || originalPlayer == null) {
            return;
        }
        mc.addScheduledTask(() -> {
            if (mc.player == null || mc.player.capabilities == null) {
                return;
            }
            else {
                if (originalCapabilities != null) {
                    mc.player.capabilities.allowFlying = originalCapabilities.allowFlying;
                    mc.player.capabilities.isFlying = originalCapabilities.isFlying;
                    mc.player.capabilities.setFlySpeed(originalCapabilities.getFlySpeed());
                }
            }
        });
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
        if (originalPlayerPos != null) {
            mc.player.setPosition(originalPlayerPos.x, originalPlayerPos.y, originalPlayerPos.z);
        }
        originalPlayerPos = null;
        originalCapabilities = null;
    }

    @Override
    public void onUpdate() {
        if (mc.player == null) {
            return;
        }
        mc.player.noClip = true;
        mc.player.capabilities.allowFlying = true;
        mc.player.capabilities.isFlying = true;
        mc.player.capabilities.setFlySpeed(0.5f);
        mc.player.onGround = false;
        mc.player.fallDistance = 0.0f;
        if (!mc.gameSettings.keyBindForward.isPressed() && !mc.gameSettings.keyBindBack.isPressed()
                && !mc.gameSettings.keyBindLeft.isPressed() && !mc.gameSettings.keyBindRight.isPressed()
                && !mc.gameSettings.keyBindJump.isPressed() && !mc.gameSettings.keyBindSneak.isPressed()) {
            mc.player.setVelocity(0.0, 0.0, 0.0);
        }
        pos = mc.player.getPositionVector();
    }

    @Listener
    public void onUpdate(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketPlayerPosLook && enabled) {
            event.setCanceled(true);
        }
    }

    static {
        pos = Vec3d.ZERO;
        pitchyaw = Vec2f.ZERO;
        enabled = false;
    }
}
