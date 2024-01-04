package xyz.templecheats.templeclient.impl.modules.movement;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.impl.modules.Module;
import xyz.templecheats.templeclient.impl.gui.clickgui.setting.Setting;

import java.util.ArrayList;

public class ElytraPlus extends Module {
    private Mode mode = Mode.BOOST;
    private boolean moving;

    public ElytraPlus() {
        super("Elytra+", Keyboard.KEY_NONE, Category.MOVEMENT);

        ArrayList<String> options = new ArrayList<>();
        options.add("Boost");
        options.add("Control");

        TempleClient.settingsManager.rSetting(new Setting("Mode", this, options, "Mode"));
    }

    public enum Mode {
        BOOST,
        CONTROL
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!mc.player.isElytraFlying()) return;

        float yaw = mc.player.rotationYaw;
        float pitch = mc.player.rotationPitch;
        double boostSpeed = 0.01;
        double controlSpeed = 1.0;
        double controlUpSpeed = 1.0;
        double controlDownSpeed = -1.0;
        double controlMoveSpeed = 1.0;

        String modeVal = TempleClient.settingsManager.getSettingByName(this.name, "Mode").getValString();
        mode = modeVal.equalsIgnoreCase("Boost") ? Mode.BOOST : Mode.CONTROL;

        if (mode == Mode.BOOST) {
            if (mc.gameSettings.keyBindForward.isKeyDown()) {
                mc.player.motionX -= Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * boostSpeed;
                mc.player.motionZ += Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * boostSpeed;
                mc.player.motionY += Math.sin(Math.toRadians(pitch)) * boostSpeed;
            }
            if (mc.gameSettings.keyBindJump.isKeyDown()) mc.player.motionY += boostSpeed;
            if (mc.gameSettings.keyBindSneak.isKeyDown()) mc.player.motionY -= boostSpeed;
        } else if (mode == Mode.CONTROL) {
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
}