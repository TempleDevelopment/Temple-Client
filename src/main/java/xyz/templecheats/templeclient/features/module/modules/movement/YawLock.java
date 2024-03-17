package xyz.templecheats.templeclient.features.module.modules.movement;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;

public class YawLock extends Module {
    public YawLock() {
        super("YawLock","Locks your rotation for precision", Keyboard.KEY_NONE, Category.Movement);
    }

    @SubscribeEvent
    public void onUpdate(RenderGameOverlayEvent.Post event) {
        if (mc.player != null) {
            mc.player.rotationYaw = mc.player.rotationYawHead;

        }
    }
}
