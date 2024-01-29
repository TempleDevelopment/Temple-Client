package xyz.templecheats.templeclient.impl.modules.movement;

import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.impl.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.impl.gui.setting.Setting;

public class Speed extends Module {
    private Setting speedSetting;

    public Speed() {
        super("Speed","Allows you to move faster    ", Keyboard.KEY_NONE, Category.MOVEMENT);
        this.speedSetting = new Setting("Speed", this, 0.0, 0.0, 10.0, true);
        TempleClient.settingsManager.rSetting(speedSetting);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (mc.player.onGround && mc.player.moveForward > 0 && !mc.player.isInWater() && !mc.player.isInLava()) {
            double speed = speedSetting.getValDouble();

            mc.player.setSprinting(true);

            float yaw = mc.player.rotationYaw * 0.0174532920F;

            mc.player.motionX -= Math.sin(yaw) * (speed / 5);
            mc.player.motionZ += Math.cos(yaw) * (speed / 5);
        }
    }
}
