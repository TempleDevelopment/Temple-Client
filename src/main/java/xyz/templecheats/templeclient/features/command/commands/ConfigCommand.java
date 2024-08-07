package xyz.templecheats.templeclient.features.command.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.features.command.Command;

import java.io.File;

public class ConfigCommand extends Command {

    @Override
    public String getName() {
        return ".config";
    }

    @Override
    public void execute(String[] args) {
        String templePrefix = TextFormatting.AQUA + "[Temple] " + TextFormatting.RESET;
        String errorPrefix = TextFormatting.RED + "[Temple] " + TextFormatting.RESET;

        if (args.length < 2) {
            Minecraft.getMinecraft().player.sendMessage(
                    new TextComponentString(errorPrefix + "Usage: .config <list/load/save> <name>")
            );
            return;
        }

        String action = args[1];
        String configName = args.length == 3 ? args[2] : "default";
        if (!configName.endsWith(".cfg")) {
            configName += ".cfg";
        }

        File configFile = new File(TempleClient.configManager.getConfigDirectory(), configName);

        switch (action.toLowerCase()) {
            case "load":
                if (configFile.exists()) {
                    TempleClient.configManager.loadConfig(configFile);
                    Minecraft.getMinecraft().player.sendMessage(
                            new TextComponentString(templePrefix + "Config loaded: " + configName)
                    );
                } else {
                    Minecraft.getMinecraft().player.sendMessage(
                            new TextComponentString(errorPrefix + "Config file does not exist: " + configName)
                    );
                }
                break;

            case "save":
                TempleClient.configManager.saveConfig(configFile);
                Minecraft.getMinecraft().player.sendMessage(
                        new TextComponentString(templePrefix + "Config saved: " + configName)
                );
                break;
            case "list":
                File[] files = TempleClient.configManager.getConfigDirectory().listFiles();
                if (files != null) {
                    Minecraft.getMinecraft().player.sendMessage(
                            new TextComponentString(templePrefix + "Config files: ")
                    );
                    for (File file : files) {
                        if (file.isFile() && file.getName().endsWith(".cfg")) {
                            Minecraft.getMinecraft().player.sendMessage(
                                    new TextComponentString(" - " + file.getName())
                            );
                        }
                    }
                } else {
                    Minecraft.getMinecraft().player.sendMessage(
                            new TextComponentString(errorPrefix + "No config files found.")
                    );
                }
                break;

            default:
                Minecraft.getMinecraft().player.sendMessage(
                        new TextComponentString(errorPrefix + "Unknown action: " + action)
                );
                break;
        }
    }
}
