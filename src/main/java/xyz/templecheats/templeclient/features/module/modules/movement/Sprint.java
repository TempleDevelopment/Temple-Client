package xyz.templecheats.templeclient.features.module.modules.movement;

import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;

public class Sprint extends Module {
    public Sprint() {
        super("Sprint","Automatically sprints for you", Keyboard.KEY_NONE, Category.Movement);
    }

    @Override
    public void onUpdate() {
        if (mc.player.moveForward > 0 && !mc.player.collidedHorizontally) {
            mc.player.setSprinting(true);
        }
    }
}
