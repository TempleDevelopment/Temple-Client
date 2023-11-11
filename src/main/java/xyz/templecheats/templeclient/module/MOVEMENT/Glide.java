package xyz.templecheats.templeclient.module.MOVEMENT;

import xyz.templecheats.templeclient.module.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class Glide extends Module {
    public Glide() {
        super("Glide", Keyboard.KEY_NONE, Category.MOVEMENT);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (mc.player.fallDistance != 0 && mc.player.motionY != 0) {
            mc.player.motionY = -0.125;
        }
    }
}