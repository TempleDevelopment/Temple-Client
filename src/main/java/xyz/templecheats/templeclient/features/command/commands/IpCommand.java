package xyz.templecheats.templeclient.features.command.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import xyz.templecheats.templeclient.features.command.Command;

public class IpCommand extends Command {
    @Override
    public String getName() {
        return ".ip";
    }

    @Override
    public void execute(String[] args) {
        String templePrefix = TextFormatting.AQUA + "[Temple] " + TextFormatting.RESET;

        if (Minecraft.getMinecraft().isSingleplayer()) {
            Minecraft.getMinecraft().player.sendMessage(
                    new TextComponentString(templePrefix + "You are in singleplayer mode.")
            );
            return;
        }

        String serverIp = Minecraft.getMinecraft().getCurrentServerData().serverIP;

        if (serverIp != null && !serverIp.isEmpty()) {
            setClipboardString(serverIp);

            Minecraft.getMinecraft().player.sendMessage(
                    new TextComponentString(templePrefix + "Server IP copied to clipboard: " + serverIp)
            );
        } else {
            Minecraft.getMinecraft().player.sendMessage(
                    new TextComponentString(templePrefix + "Failed to retrieve server IP.")
            );
        }
    }

    private void setClipboardString(String text) {
        try {
            java.awt.datatransfer.Clipboard clipboard =
                    java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
            java.awt.datatransfer.StringSelection stringSelection = new java.awt.datatransfer.StringSelection(text);
            clipboard.setContents(stringSelection, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}