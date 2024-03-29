package xyz.templecheats.templeclient.event;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.manager.ModuleManager;

public class ForgeEventManager {
    private boolean playerLoaded;
    
    public ForgeEventManager() {
        MinecraftForge.EVENT_BUS.register(this);
        playerLoaded = false;
    }
    @SubscribeEvent
    public void onTickEvent(TickEvent.ClientTickEvent event) {
        TempleClient.getModuleManager().onPlayerTick();
    }
    
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        //TempleClient.configManager.loadAll();
    }
    
    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        TempleClient.configManager.saveAll();
    }
    
    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if(Minecraft.getMinecraft().player == null) {
            return;
        }
        
        for(Module module : ModuleManager.getModules()) {
            if(module.isToggled()) {
                module.onRenderWorld(event.getPartialTicks());
            }
        }
    }
}