package xyz.templecheats.templeclient.features.module.modules.chat;

import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.templecheats.templeclient.features.module.Module;

public class GreenText extends Module {
    public GreenText() {
        super("GreenText", "Puts '>' in front of your messages", 0, Category.Chat);
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
