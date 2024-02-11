package xyz.templecheats.templeclient.impl.modules.movement;

import xyz.templecheats.templeclient.impl.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

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
