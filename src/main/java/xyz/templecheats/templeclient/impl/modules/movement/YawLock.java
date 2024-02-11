package xyz.templecheats.templeclient.impl.modules.movement;

import xyz.templecheats.templeclient.impl.modules.Module;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

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
