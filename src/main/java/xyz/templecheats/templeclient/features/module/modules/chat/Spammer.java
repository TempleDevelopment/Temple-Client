package xyz.templecheats.templeclient.features.module.modules.chat;

import net.minecraft.client.Minecraft;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;

public class Spammer extends Module {
    private final IntSetting delay = new IntSetting("Delay", this, 1, 10, 5);
    private int timer = 0;

    public Spammer() {
        super("Spammer", "Spams a message in chat", 0, Category.Chat);

        this.registerSettings(delay);
    }

    @Override
    public void onUpdate() {
        if (timer++ >= delay.intValue() * 20) {
            timer = 0;
            Minecraft.getMinecraft().player.sendChatMessage("TempleClient on top ! | https://templecheats.xyz");
        }
    }
}