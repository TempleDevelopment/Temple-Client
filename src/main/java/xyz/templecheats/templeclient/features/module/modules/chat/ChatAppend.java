package xyz.templecheats.templeclient.features.module.modules.chat;

import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.StringSetting;

public class ChatAppend extends Module {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    private final StringSetting suffix = new StringSetting("Suffix", this, " | templeclient");
    private final StringSetting prefix = new StringSetting("Prefix", this, "templeclient | ");
    private final BooleanSetting enableSuffix = new BooleanSetting("Enable Suffix", this, true);
    private final BooleanSetting enablePrefix = new BooleanSetting("Enable Prefix", this, true);

    public ChatAppend() {
        super("ChatAppend", "Automatically adds text to your chat messages", 0, Category.Chat);
        this.registerSettings(suffix, prefix, enableSuffix, enablePrefix);
    }

    @SubscribeEvent
    public void onChat(ClientChatEvent event) {
        if (event.getMessage().startsWith("/") || event.getMessage().startsWith(".")) {
            return;
        }

        String message = event.getMessage();
        if (enablePrefix.booleanValue()) {
            message = prefix.getStringValue() + message;
        }
        if (enableSuffix.booleanValue()) {
            message += suffix.getStringValue();
        }
        event.setMessage(message);
    }
}