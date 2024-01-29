package xyz.templecheats.templeclient.impl.modules.movement;

import xyz.templecheats.templeclient.impl.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class FastFall extends Module {
    public FastFall() {
        super("FastFall","Increases falling speed (pretty bricked atm)", Keyboard.KEY_NONE, Category.MOVEMENT);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (mc.player.fallDistance >= 1) {
            mc.player.posY = mc.player.posY - mc.player.fallDistance;
        }
    }
}