package xyz.templecheats.templeclient.module.MOVEMENT;

import xyz.templecheats.templeclient.module.Module;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class Speed extends Module {
    public Speed() {
        super("Speed", Keyboard.KEY_NONE, Category.MOVEMENT);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (mc.player.onGround && mc.player.moveForward > 0 && !mc.player.isInWater() && !mc.player.isInLava()) {
            double speed = 0.5;

            mc.player.setSprinting(true);
            mc.player.motionY = 0.1;

            float yaw = mc.player.rotationYaw * 0.0174532920F;

            mc.player.motionX -= MathHelper.sin(yaw) * (speed / 5);
            mc.player.motionZ += MathHelper.cos(yaw) * (speed / 5);
        }

        /*
        Legit:

        if (mc.player.onGround && mc.player.moveForward > 0 && !mc.player.isInWater() && !mc.player.isInLava()) {
            mc.player.jump();
        }

         */
    }
}