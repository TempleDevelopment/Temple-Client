package xyz.templecheats.templeclient.features.command.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import xyz.templecheats.templeclient.features.command.Command;

public class DisconnectCommand extends Command {

    @Override
    public String getName() {
        return ".disconnect";
    }

    @Override
    public void execute(String[] args) {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc.isSingleplayer()) {
            sendMessage("This command can only be used in multiplayer.", false);
            return;
        }

        new Thread(() -> {
            mc.world.sendQuittingDisconnectingPacket();
        }).start();
    }

    protected void sendMessage(String message, boolean isError) {
        String prefix = TextFormatting.AQUA + "[Temple] " + TextFormatting.RESET;
        if (isError) {
            prefix += TextFormatting.RED;
        }
        Minecraft.getMinecraft().player.sendMessage(new TextComponentString(prefix + message));
    }
}