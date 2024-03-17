package xyz.templecheats.templeclient.features.module.modules.movement;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;

public class Spider extends Module {
    public Spider() {
        super("Spider","Allows climbing on blocks", Keyboard.KEY_NONE, Category.Movement);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (mc.player.collidedHorizontally) {
            mc.player.motionY = 0.25;
        }
    }
}