package xyz.templecheats.templeclient.features.module.modules.movement;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;

public class Glide extends Module {
    public Glide() {
        super("Glide","Glides when falling for a smoother landing", Keyboard.KEY_NONE, Category.Movement);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (mc.player.fallDistance != 0 && mc.player.motionY != 0) {
            mc.player.motionY = -0.125;
        }
    }
}