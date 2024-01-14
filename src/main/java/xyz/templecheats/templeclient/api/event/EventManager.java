package xyz.templecheats.templeclient.api.event;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import xyz.templecheats.templeclient.TempleClient;

/**
 * @author XeonLyfe
 */

public class EventManager {
    private boolean playerLoaded;

    public EventManager() {
        MinecraftForge.EVENT_BUS.register(this);
        playerLoaded = false;
    }
/*
    @SubscribeEvent
    public void onPlayerEvent(TickEvent.PlayerTickEvent event) {
        if (playerLoaded && Minecraft.getMinecraft().player != null) {
            TempleClient.configManager.loadModules();
            playerLoaded = false;
        }
    }

 */

    @SubscribeEvent
    public void onTickEvent(TickEvent.ClientTickEvent event) {
        TempleClient.getModuleManager().onPlayerTick();
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        //TempleClient.configManager.loadModules();
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        TempleClient.configManager.saveModules();
    }
}