package xyz.templecheats.templeclient;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import xyz.templecheats.templeclient.features.module.modules.client.RPC;

public class DiscordPresence {
    private static final DiscordRPC rpc;
    public static DiscordRichPresence presence;
    private static Thread thread;

    static {
        rpc = DiscordRPC.INSTANCE;
        presence = new DiscordRichPresence();
    }

    public static void start() {
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        rpc.Discord_Initialize("1273936190592253974", handlers, true, "");
        DiscordPresence.presence.startTimestamp = System.currentTimeMillis() / 1000L;

        DiscordPresence.presence.details = Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu
                ? "In the main menu."
                : "Playing Minecraft";

        DiscordPresence.presence.state = RPC.INSTANCE.state.getStringValue();
        DiscordPresence.presence.largeImageKey = "temple-logo";
        DiscordPresence.presence.largeImageText = "Temple Client | 1.12.2";
        rpc.Discord_UpdatePresence(presence);

        thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                rpc.Discord_RunCallbacks();

                DiscordPresence.presence.details = Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu
                        ? "In the main menu."
                        : "Playing Minecraft";

                DiscordPresence.presence.state = RPC.INSTANCE.state.getStringValue();
                rpc.Discord_UpdatePresence(presence);

                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException interruptedException) {
                }
            }
        }, "RPC-Callback-Handler");
        thread.start();
    }

    public static void stop() {
        if (thread != null && !thread.isInterrupted()) {
            thread.interrupt();
        }
        rpc.Discord_Shutdown();
    }
}
