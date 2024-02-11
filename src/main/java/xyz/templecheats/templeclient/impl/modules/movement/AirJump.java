package xyz.templecheats.templeclient.impl.modules.movement;

import xyz.templecheats.templeclient.impl.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class AirJump extends Module {
    public AirJump() {
        super("AirJump","Allows jumping in the air", Keyboard.KEY_NONE, Category.Movement);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (mc.gameSettings.keyBindJump.isPressed()) {
            mc.player.jump();
        }
    }
}
