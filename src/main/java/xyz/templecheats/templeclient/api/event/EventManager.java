package xyz.templecheats.templeclient.api.event;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import xyz.templecheats.templeclient.ModuleManager;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.api.config.rewrite.ConfigManager;

import static xyz.templecheats.templeclient.TempleClient.configManager;

/**
 * @author XeonLyfe
 */

public class EventManager {

    private ConfigManager configManager;
    private boolean playerLoaded;

    public EventManager() {
        configManager = new ConfigManager();
        MinecraftForge.EVENT_BUS.register(this);
        playerLoaded = false;
    }
/*
    @SubscribeEvent
    public void onPlayerEvent(TickEvent.PlayerTickEvent event) {
        if (playerLoaded && Minecraft.getMinecraft().player != null) {
            configManager.loadModules();
            playerLoaded = false;
        }
    }

 */

    @SubscribeEvent
    public void onTickEvent(TickEvent.ClientTickEvent event) {
        if (Minecraft.getMinecraft().player != null) {
            TempleClient.getModuleManager().onPlayerTick();
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        //configManager.loadModules();
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        configManager.saveModules();
    }
}