package xyz.templecheats.templeclient.features.modules.chat;

import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.templecheats.templeclient.features.modules.Module;

public class GreenText extends Module {

    public GreenText() {
        super("GreenText", 0, Category.CHAT);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onChat(ClientChatEvent event) {
        if (event.getMessage().startsWith("/") || event.getMessage().startsWith(".")) {
            return;
        }

        String greenText = "> " + event.getMessage();
        event.setMessage(greenText);
    }
}
