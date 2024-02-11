package xyz.templecheats.templeclient.impl.modules.movement;

import xyz.templecheats.templeclient.impl.modules.Module;
import org.lwjgl.input.Keyboard;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Flight extends Module {
    public Flight() {
        super("Flight","Allows flying (really?)", Keyboard.KEY_NONE, Category.Movement);
    }

    @Override
    public void onEnable() {
        mc.player.capabilities.isFlying = true;
        mc.player.capabilities.allowFlying = true;
    }

    @Override
    public void onDisable() {
        mc.player.capabilities.isFlying = false;
        mc.player.capabilities.allowFlying = false;
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.player == mc.player) {
            onEnable();
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.player == mc.player) {
            onDisable();
        }
    }
}