package xyz.templecheats.templeclient.features.modules.movement;

import xyz.templecheats.templeclient.features.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class ElytraPlus extends Module {
    public ElytraPlus() {
        super("Elytra+", Keyboard.KEY_NONE, Category.MOVEMENT);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!mc.player.isElytraFlying()) return;

        float yaw = mc.player.rotationYaw;
        float pitch = mc.player.rotationPitch;
        double boostSpeed = 0.01;

        if (mc.gameSettings.keyBindForward.isKeyDown()) {
            mc.player.motionX -= Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * boostSpeed;
            mc.player.motionZ += Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * boostSpeed;
            mc.player.motionY += Math.sin(Math.toRadians(pitch)) * boostSpeed;
        }
        if (mc.gameSettings.keyBindJump.isKeyDown()) mc.player.motionY += boostSpeed;
        if (mc.gameSettings.keyBindSneak.isKeyDown()) mc.player.motionY -= boostSpeed;
    }
}
