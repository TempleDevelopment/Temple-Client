package xyz.templecheats.templeclient.features.module.modules.movement;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;

public class ElytraPlus extends Module {
    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", this, Mode.Boost);
    private final DoubleSetting speed = new DoubleSetting("Control Speed", this, 0, 5, 1);
    private boolean moving;

    public ElytraPlus() {
        super("Elytra+","Allows you to fly with elytras", Keyboard.KEY_NONE, Category.Movement);

        registerSettings(speed, mode);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (mc.player == null || !mc.player.isElytraFlying()) return;

        float yaw = mc.player.rotationYaw;
        float pitch = mc.player.rotationPitch;
        double boostSpeed = 0.02;
        double controlSpeed = speed.doubleValue();
        double controlUpSpeed = speed.doubleValue();
        double controlDownSpeed = -speed.doubleValue();
        double controlMoveSpeed = speed.doubleValue();

        if (mode.value() == Mode.Boost) {
            if (mc.gameSettings.keyBindForward.isKeyDown()) {
                mc.player.motionX -= Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * boostSpeed;
                mc.player.motionZ += Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * boostSpeed;
                mc.player.motionY += Math.sin(Math.toRadians(pitch)) * boostSpeed;
            }
            if (mc.gameSettings.keyBindJump.isKeyDown()) mc.player.motionY += boostSpeed;
            if (mc.gameSettings.keyBindSneak.isKeyDown()) mc.player.motionY -= boostSpeed;
        } else if (mode.value() == Mode.Control) {
            double verticalMotion = 0.0;
            double horizontalMotionX = 0.0;
            double horizontalMotionZ = 0.0;

            if (mc.gameSettings.keyBindForward.isKeyDown()) {
                horizontalMotionX -= Math.sin(Math.toRadians(yaw)) * controlMoveSpeed;
                horizontalMotionZ += Math.cos(Math.toRadians(yaw)) * controlMoveSpeed;
            }
            if (mc.gameSettings.keyBindBack.isKeyDown()) {
                horizontalMotionX += Math.sin(Math.toRadians(yaw)) * controlMoveSpeed;
                horizontalMotionZ -= Math.cos(Math.toRadians(yaw)) * controlMoveSpeed;
            }
            if (mc.gameSettings.keyBindLeft.isKeyDown()) {
                horizontalMotionX -= Math.sin(Math.toRadians(yaw - 90)) * controlMoveSpeed;
                horizontalMotionZ += Math.cos(Math.toRadians(yaw - 90)) * controlMoveSpeed;
            }
            if (mc.gameSettings.keyBindRight.isKeyDown()) {
                horizontalMotionX -= Math.sin(Math.toRadians(yaw + 90)) * controlMoveSpeed;
                horizontalMotionZ += Math.cos(Math.toRadians(yaw + 90)) * controlMoveSpeed;
            }
            if (mc.gameSettings.keyBindJump.isKeyDown()) verticalMotion += controlUpSpeed;
            if (mc.gameSettings.keyBindSneak.isKeyDown()) verticalMotion += controlDownSpeed;

            mc.player.setVelocity(horizontalMotionX, verticalMotion, horizontalMotionZ);
        }
    }

    private enum Mode {
        Boost,
        Control
    }
}