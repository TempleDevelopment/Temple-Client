package xyz.templecheats.templeclient.features.command.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import xyz.templecheats.templeclient.features.command.Command;

public class HelpCommand extends Command {
    @Override
    public String getName() {
        return ".help";
    }

    public void execute(String[] args) {
        String templePrefix = TextFormatting.AQUA + "[Temple] " + TextFormatting.RESET;
        String commandPrefix = TextFormatting.AQUA + "- " + TextFormatting.RESET;

        Minecraft.getMinecraft().player.sendMessage(new TextComponentString(templePrefix + "Available Commands:"));

        Minecraft.getMinecraft().player.sendMessage(new TextComponentString(commandPrefix + ".bind - Binds a module to a key (ex: .bind freecam r)"));
        Minecraft.getMinecraft().player.sendMessage(new TextComponentString(commandPrefix + ".coords - Copies your coordinates to your clipboard"));
        Minecraft.getMinecraft().player.sendMessage(new TextComponentString(commandPrefix + ".friend - Adds, removes & lists your friends"));
        Minecraft.getMinecraft().player.sendMessage(new TextComponentString(commandPrefix + ".help - Shows this help message"));
        Minecraft.getMinecraft().player.sendMessage(new TextComponentString(commandPrefix + ".ip - Copies current server ip to your clipboard"));
        Minecraft.getMinecraft().player.sendMessage(new TextComponentString(commandPrefix + ".clickgui - Adjusts the scale of the ClickGUI."));
    }
}
