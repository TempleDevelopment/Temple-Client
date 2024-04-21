package xyz.templecheats.templeclient.features.command.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.features.command.Command;
import xyz.templecheats.templeclient.features.module.Module;

public class BindCommand extends Command {

    @Override
    public String getName() {
        return ".bind";
    }
    @Override
    public void execute(String[] args) {
        if (args.length != 3) {
            sendMessage("Invalid syntax. Use .bind <module> <key>", true);
            return;
        }

        String moduleName = args[1];
        String keyName = args[2];

        Module module = TempleClient.moduleManager.getModuleByName(moduleName);

        if (module == null) {
            sendMessage("Module " + moduleName + " not found.", true);
            return;
        }

        int keyCode = Keyboard.getKeyIndex(keyName.toUpperCase());

        if (keyCode == Keyboard.KEY_NONE) {
            sendMessage("Key " + keyName + " not found.", true);
            return;
        }

        module.setKey(keyCode);
        sendMessage("Bound " + moduleName + " to " + keyName + ".", false);
    }

    public static void sendMessage(String message, boolean isError) {
        String templePrefix = isError ? TextFormatting.RED + "[Temple] " + TextFormatting.RESET : TextFormatting.AQUA + "[Temple] " + TextFormatting.RESET;
        String formattedMessage = isError ? TextFormatting.WHITE + message : TextFormatting.RESET + message;
        Minecraft.getMinecraft().player.sendMessage(
                new TextComponentString(templePrefix + formattedMessage)
        );
    }
}