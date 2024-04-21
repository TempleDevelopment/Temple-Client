package xyz.templecheats.templeclient.features.command.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.features.command.Command;
import xyz.templecheats.templeclient.features.module.Module;

public class FakePlayerCommand extends Command {

    @Override
    public String getName() {
        return ".fakeplayer";
    }

    @Override
    public void execute(String[] args) {
        Module fakePlayerModule = TempleClient.moduleManager.getModuleByName("FakePlayer");
        if (fakePlayerModule != null) {
            fakePlayerModule.toggle();
            if (fakePlayerModule.isEnabled()) {
                sendMessage("Added a FakePlayer.", fakePlayerModule);
            } else {
                sendMessage("Removed FakePlayer.", fakePlayerModule);
            }
        }
    }

    public static void sendMessage(String message, Module module) {
        String templePrefix = TextFormatting.AQUA + "[Temple] " + TextFormatting.RESET;
        String formattedMessage = TextFormatting.WHITE + message;
        Minecraft.getMinecraft().player.sendMessage(
                new TextComponentString(templePrefix + formattedMessage)
        );
    }
}