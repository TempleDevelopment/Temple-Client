package xyz.templecheats.templeclient.features.command.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import xyz.templecheats.templeclient.features.command.Command;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class OpenFolderCommand extends Command {

    @Override
    public String getName() {
        return ".openfolder";
    }

    @Override
    public void execute(String[] args) {
        String templePrefix = TextFormatting.AQUA + "[Temple] " + TextFormatting.RESET;
        String errorPrefix = TextFormatting.RED + "[Temple] " + TextFormatting.RESET;

        File templeClientFolder = new File(Minecraft.getMinecraft().gameDir, "Temple Client");

        if (!templeClientFolder.exists()) {
            Minecraft.getMinecraft().player.sendMessage(
                    new TextComponentString(errorPrefix + "The Temple Client folder does not exist.")
            );
            return;
        }

        try {
            Desktop.getDesktop().open(templeClientFolder);
            Minecraft.getMinecraft().player.sendMessage(
                    new TextComponentString(templePrefix + "Opened Temple Client folder: " + templeClientFolder.getAbsolutePath())
            );
        } catch (IOException e) {
            e.printStackTrace();
            Minecraft.getMinecraft().player.sendMessage(
                    new TextComponentString(errorPrefix + "Failed to open Temple Client folder.")
            );
        }
    }
}
