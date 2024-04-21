package xyz.templecheats.templeclient.features.command.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.features.command.Command;
import xyz.templecheats.templeclient.features.module.Module;

public class ToggleCommand extends Command {

    @Override
    public String getName() {
        return ".toggle";
    }

    @Override
    public void execute(String[] args) {
        if (args.length != 2) {
            sendMessage("Invalid syntax. Use .toggle <module>", true, null);
            return;
        }

        String moduleName = args[1];

        Module module = TempleClient.moduleManager.getModuleByName(moduleName);

        if (module == null) {
            sendMessage("Module " + moduleName + " not found.", true, null);
            return;
        }

        module.toggle();
        sendMessage("Toggled " + moduleName + ".", false, module);
    }

    public static void sendMessage(String message, boolean isError, Module module) {
        String templePrefix = isError ? TextFormatting.RED + "[Temple] " + TextFormatting.RESET : TextFormatting.AQUA + "[Temple] " + TextFormatting.RESET;
        String status = (module != null) ? (module.isEnabled() ? TextFormatting.GREEN + "[Enabled] " : TextFormatting.RED + "[Disabled] ") : "";
        String formattedMessage = TextFormatting.WHITE + message;
        Minecraft.getMinecraft().player.sendMessage(
                new TextComponentString(templePrefix + status + formattedMessage)
        );
    }
}