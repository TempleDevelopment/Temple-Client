package xyz.templecheats.templeclient.api.command.commands;

import net.minecraft.client.Minecraft;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.api.command.Command;
import xyz.templecheats.templeclient.impl.modules.Module;
import org.lwjgl.input.Keyboard;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class BindCommand implements Command {

    @Override
    public String getName() {
        return ".bind";
    }

    @Override
    public void execute(String[] args) {
        if (args.length != 3) {
            sendMessage("Invalid syntax. Use .bind <module> <key>");
            return;
        }

        String moduleName = args[1];
        String keyName = args[2];

        Module module = TempleClient.moduleManager.getModuleByName(moduleName);

        if (module == null) {
            sendMessage("Module " + moduleName + " not found.");
            return;
        }

        int keyCode = Keyboard.getKeyIndex(keyName.toUpperCase());

        if (keyCode == Keyboard.KEY_NONE) {
            sendMessage("Key " + keyName + " not found.");
            return;
        }

        module.setKey(keyCode);
        sendMessage("Bound " + moduleName + " to " + keyName + ".");
    }

    private void sendMessage(String message) {
        String templePrefix = TextFormatting.AQUA + "[Temple] " + TextFormatting.RESET;
        Minecraft.getMinecraft().player.sendMessage(
                new TextComponentString(templePrefix + message)
        );
    }
}