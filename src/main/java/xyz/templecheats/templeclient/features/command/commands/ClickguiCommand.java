package xyz.templecheats.templeclient.features.command.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import xyz.templecheats.templeclient.features.command.Command;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;

public class ClickguiCommand extends Command {
    @Override
    public String getName() {
        return ".clickgui";
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 3 && args[1].equalsIgnoreCase("scale")) {
            try {
                double scale = Double.parseDouble(args[2]);
                ClickGUI.INSTANCE.scale.setDoubleValue(scale);
                Minecraft.getMinecraft().player.sendMessage(new TextComponentString(TextFormatting.AQUA + "[Temple] " + TextFormatting.RESET + "ClickGUI scale set to " + scale));
            } catch (NumberFormatException e) {
                Minecraft.getMinecraft().player.sendMessage(new TextComponentString(TextFormatting.RED + "[Temple] " + TextFormatting.RESET + "Invalid scale value."));
            }
        } else {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString(TextFormatting.RED + "[Temple] " + TextFormatting.RESET + "Usage: .clickgui scale <value>"));
        }
    }
}