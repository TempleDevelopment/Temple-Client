package xyz.templecheats.templeclient.features.module.modules.movement;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;

public class Speed extends Module {
    private final DoubleSetting speed = new DoubleSetting("Speed", this, 0d, 3d, 0.2d);
    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", this, Mode.Vanilla);

    public Speed() {
        super("Speed", "Allows you to move faster", Keyboard.KEY_NONE, Category.Movement);

        registerSettings(speed, mode);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (TempleClient.settingsManager == null || mc.player == null) {
            return;
        }

        double sliderValue = speed.doubleValue() / 30.0;

        if (mc.player.onGround && mc.player.moveForward > 0 && !mc.player.isInWater() && !mc.player.isInLava()) {
            mc.player.setSprinting(true);
            float yaw = mc.player.rotationYaw * 0.017453292F;

            mc.player.motionX -= Math.sin(yaw) * sliderValue;
            mc.player.motionZ += Math.cos(yaw) * sliderValue;

            if (mode.value() == Speed.Mode.BHop) {
                mc.player.jump();
            }
        }
    }

    private enum Mode {
        Vanilla,
        BHop
    }
}