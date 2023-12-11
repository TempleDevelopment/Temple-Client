package xyz.templecheats.templeclient.events;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import xyz.templecheats.templeclient.Client;
import xyz.templecheats.templeclient.TempleClient;

/**
 * @author XeonLyfe
 */

public class EventManager {

    @SubscribeEvent
    public void onTickEvent(TickEvent.ClientTickEvent event) {
        if (Minecraft.getMinecraft().player != null) {
            Client.onPlayerTick();
        }
    }
}
