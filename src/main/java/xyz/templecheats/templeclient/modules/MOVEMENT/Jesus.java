package xyz.templecheats.templeclient.modules.MOVEMENT;

import xyz.templecheats.templeclient.modules.Module;
import org.lwjgl.input.Keyboard;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Jesus extends Module {
    public Jesus() {
        super("Jesus", Keyboard.KEY_NONE, Category.MOVEMENT);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (mc.player != null && event.phase == TickEvent.Phase.START) {
            if (mc.player.isInWater()) {
                // Move the player up and forward to mimic walking on water
                mc.player.motionY = 0.1; // You can adjust the values to make it smoother
                mc.player.moveForward = 0.2f;
            }
        }
    }
}
