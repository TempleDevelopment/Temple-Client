package xyz.templecheats.templeclient.event;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.event.events.player.LeftClickBlockEvent;
import xyz.templecheats.templeclient.event.events.render.Render3DEvent;
import xyz.templecheats.templeclient.event.events.render.Render3DPostEvent;
import xyz.templecheats.templeclient.event.events.render.Render3DPreEvent;
import xyz.templecheats.templeclient.event.events.render.Render3DPrePreEvent;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.manager.ModuleManager;

public class ForgeEventManager {

    public static long lastFrame, deltaTime;
    private boolean playerLoaded;

    public ForgeEventManager() {
        MinecraftForge.EVENT_BUS.register(this);
        playerLoaded = false;
    }

    /****************************************************************
     *                      Event Handlers
     ****************************************************************/

    @SubscribeEvent
    public void onTickEvent(TickEvent.ClientTickEvent event) {
        TempleClient.getModuleManager().onPlayerTick();
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        TempleClient.configManager.saveAll();
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (Minecraft.getMinecraft().player == null) {
            return;
        }

        TempleClient.eventBus.dispatchEvent(new Render3DPrePreEvent(event.getPartialTicks()));
        TempleClient.eventBus.dispatchEvent(new Render3DPreEvent(event.getPartialTicks()));
        TempleClient.eventBus.dispatchEvent(new Render3DEvent(event.getPartialTicks()));
        TempleClient.eventBus.dispatchEvent(new Render3DPostEvent(event.getPartialTicks()));

        deltaTime = System.currentTimeMillis() - lastFrame;
        lastFrame = System.currentTimeMillis();

        for (Module module : ModuleManager.getModules()) {
            if (module.isToggled()) {
                module.onRenderWorld(event.getPartialTicks());
            }
        }
    }

    @SubscribeEvent
    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        LeftClickBlockEvent leftClickBlockEvent = new LeftClickBlockEvent(event.getPos(), event.getFace());
        MinecraftForge.EVENT_BUS.post(leftClickBlockEvent);
    }
}
