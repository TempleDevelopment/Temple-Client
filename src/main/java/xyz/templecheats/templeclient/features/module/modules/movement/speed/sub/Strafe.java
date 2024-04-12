package xyz.templecheats.templeclient.features.module.modules.movement.speed.sub;

import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.event.events.network.PacketEvent;
import xyz.templecheats.templeclient.event.events.player.MoveEvent;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;
import xyz.templecheats.templeclient.util.world.EntityUtil;

public class Strafe extends Module {
    private final DoubleSetting speed = new DoubleSetting("Speed", this, 0, 10, 2.6);
    private final BooleanSetting jump = new BooleanSetting("Jump", this, true);
    private final BooleanSetting liquid = new BooleanSetting("Liquid", this, true);

    private double moveSpeed = 0;
    private double lastDist = 0;
    private int stage = 4;

    public Strafe() {
        super("Strafe", "Allows you to move faster (but strafe)", Keyboard.KEY_NONE, Category.Movement, true);

        registerSettings(speed, jump, liquid);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.player == null || mc.world == null) return;
        if (!liquid.booleanValue() && (mc.player.isInLava() || mc.player.isInWater())) return;
        lastDist = Math.sqrt(Math.pow(mc.player.posX - mc.player.prevPosX, 2) + Math.pow(mc.player.posZ - mc.player.prevPosZ, 2));
    }

    @Listener
    public void onPlayerMove(MoveEvent event) {
        if (EntityUtil.isMoving()) {
            if (mc.player.onGround) stage = 2;
        }
        if (stage == 1 && (EntityUtil.isMoving())) {
            stage = 2;
            moveSpeed = 1.38 * (speed.doubleValue() / 10);
            if (mc.player.isPotionActive(MobEffects.SPEED)) {
                final int amplifier = mc.player.getActivePotionEffect(MobEffects.SPEED).getAmplifier();
                moveSpeed *= 1.0 + 0.2 * (amplifier + 1);
            }
        } else if (stage == 2) {
            stage = 3;
            if (jump.booleanValue()) {
                mc.player.motionY = 0.3995f;
                event.setY(0.3995f);
            }
            moveSpeed *= 2.149;
            if (mc.player.isPotionActive(MobEffects.SPEED)) {
                final int amplifier = mc.player.getActivePotionEffect(MobEffects.SPEED).getAmplifier();
                moveSpeed *= 1.0 + 0.2 * (amplifier + 1);
            }
        } else if (stage == 3) {
            stage = 4;
            moveSpeed = lastDist - (0.66 * (lastDist - (speed.doubleValue() / 10)));
            if (mc.player.isPotionActive(MobEffects.SPEED)) {
                final int amplifier = mc.player.getActivePotionEffect(MobEffects.SPEED).getAmplifier();
                moveSpeed *= 1.0 + 0.2 * (amplifier + 1);
            }
        } else {
            if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, mc.player.motionY, 0.0)).size() > 0 || mc.player.collidedVertically)
                stage = 1;
            moveSpeed = lastDist - (lastDist / 159.0);
        }
        moveSpeed = Math.min(Math.max(moveSpeed, (speed.doubleValue() / 10)), 0.551);

        float forward = mc.player.movementInput.moveForward;
        float strafe = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.rotationYaw;
        if (!(EntityUtil.isMoving())) {
            event.setX(0);
            event.setZ(0);
        } else if (forward != 0.0f) {
            if (strafe >= 1.0f) {
                yaw += (float) (forward > 0.0f ? -45 : 45);
                strafe = 0.0f;
            } else if (strafe <= -1.0f) {
                yaw += (float) (forward > 0.0f ? 45 : -45);
                strafe = 0.0f;
            }

            if (forward > 0.0f) forward = 1.0f;
            else if (forward < 0.0f) forward = -1.0f;
        }

        final double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        final double cos = Math.cos(Math.toRadians(yaw + 90.0f));

        event.setX(((double) forward * moveSpeed * cos + (double) strafe * moveSpeed * sin));
        event.setZ(((double) forward * moveSpeed * sin - (double) strafe * moveSpeed * cos));
        if (!(EntityUtil.isMoving())) {
            event.setX(0);
            event.setZ(0);
        }
    }

    @Listener
    public void onPacketReceive(PacketEvent.Receive e) {
        if (e.getPacket() instanceof SPacketPlayerPosLook) {
            moveSpeed = 0;
            lastDist = 0;
            stage = 4;
        }
    }


}
