package xyz.templecheats.templeclient.features.module.modules.chat;

import net.minecraft.client.Minecraft;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;
import xyz.templecheats.templeclient.util.setting.impl.StringSetting;

public class Spammer extends Module {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    private final IntSetting delay = new IntSetting("Delay", this, 1, 10, 5);
    private final StringSetting message = new StringSetting("Message", this, "Hello World");

    /****************************************************************
     *                      Variables
     ****************************************************************/
    private int timer = 0;

    public Spammer() {
        super("Spammer", "Spams chat", 0, Category.Chat);

        this.registerSettings(message, delay);
    }

    @Override
    public void onUpdate() {
        if (timer++ >= delay.intValue() * 20) {
            timer = 0;
            Minecraft.getMinecraft().player.sendChatMessage(message.getStringValue());
        }
    }
}