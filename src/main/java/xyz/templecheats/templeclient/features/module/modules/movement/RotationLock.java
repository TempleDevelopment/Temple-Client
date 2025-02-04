package xyz.templecheats.templeclient.features.module.modules.movement;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.render.Freecam;

public class RotationLock extends Module {
    public RotationLock() {
        super("RotationLock", "Lock your yaw and pitch", Keyboard.KEY_NONE, Category.Movement);
    }

    @SubscribeEvent
    public void onUpdate(RenderGameOverlayEvent.Post event) {
        if (Freecam.isFreecamActive()) {
            return;
        }
        if (mc.player != null) {
            mc.player.rotationYaw = mc.player.rotationYawHead;

        }
    }
}