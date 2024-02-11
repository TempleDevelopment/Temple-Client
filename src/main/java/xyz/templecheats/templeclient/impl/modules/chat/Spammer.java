package xyz.templecheats.templeclient.impl.modules.chat;

import net.minecraft.client.Minecraft;
import xyz.templecheats.templeclient.api.setting.Setting;
import xyz.templecheats.templeclient.impl.modules.Module;

public class Spammer extends Module {

    private Setting delay;
    private Thread spammerThread;
    private boolean running;

    public Spammer() {
        super("Spammer", "Spams a message in chat", 0, Category.Chat);
        delay = new Setting("Delay", this, 5, 1, 60, true);
    }

    @Override
    public void onEnable() {
        running = true;
        spammerThread = new Thread(() -> {
            while (running) {
                Minecraft.getMinecraft().player.sendChatMessage("TempleClient on top ! | https://templecheats.xyz");
                try {
                    Thread.sleep((long) delay.getValDouble() * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        spammerThread.start();
    }

    @Override
    public void onDisable() {
        running = false;
        spammerThread.interrupt();
    }
}