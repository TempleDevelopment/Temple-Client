package xyz.templecheats.templeclient.features.command.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import xyz.templecheats.templeclient.features.command.Command;

import java.awt.*;
import java.net.URI;

public class NameMcCommand extends Command {
    @Override
    public String getName() {
        return ".namemc";
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString(TextFormatting.RED + "[Temple] " + TextFormatting.RESET + "Name is required."));
            return;
        }

        String username = args[1];
        String url = "https://namemc.com/profile/" + username + ".1";

        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}