package xyz.templecheats.templeclient.impl.modules.movement;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.impl.modules.Module;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.api.setting.Setting;

import java.util.ArrayList;

public class Speed extends Module {
    private Setting speedSetting;

    public Speed() {
        super("Speed", "Allows you to move faster", Keyboard.KEY_NONE, Category.Movement);
        ArrayList<String> options = new ArrayList<>();
        options.add("Vanilla");
        options.add("Bhop");
        TempleClient.settingsManager.rSetting(new Setting("Mode", this, options, "Vanilla"));
        this.speedSetting = new Setting("Speed", this, 0.2, 0.0, 3.0, false);
        TempleClient.settingsManager.rSetting(speedSetting);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (TempleClient.settingsManager == null || mc.player == null) {
            return;
        }

        String Mode = TempleClient.settingsManager.getSettingByName(this.getName(), "Mode").getValString();
        double sliderValue = speedSetting.getValDouble() / 30.0;

        if (mc.player.onGround && mc.player.moveForward > 0 && !mc.player.isInWater() && !mc.player.isInLava()) {
            mc.player.setSprinting(true);
            float yaw = mc.player.rotationYaw * 0.017453292F;

            if (Mode.equals("Vanilla") || Mode.equals("Bhop")) {
                mc.player.motionX -= Math.sin(yaw) * sliderValue;
                mc.player.motionZ += Math.cos(yaw) * sliderValue;
            }

            if (Mode.equals("Bhop")) {
                mc.player.jump();
            }
        }
    }
}