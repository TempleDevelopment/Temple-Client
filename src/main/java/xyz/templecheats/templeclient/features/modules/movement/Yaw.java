package xyz.templecheats.templeclient.features.modules.movement;

import xyz.templecheats.templeclient.features.modules.Module;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class Yaw extends Module {
    public Yaw() {
        super("Yaw", Keyboard.KEY_NONE, Category.MOVEMENT);
    }

    @SubscribeEvent
    public void onUpdate(RenderGameOverlayEvent.Post event) {
        if (mc.player != null) {
            mc.player.rotationYaw = mc.player.rotationYawHead;

            // You can add more code here to control movement in the direction the player is looking.
        }
    }
}
