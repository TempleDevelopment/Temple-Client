package xyz.templecheats.templeclient.features.command.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import xyz.templecheats.templeclient.features.command.Command;

public class CoordsCommand extends Command {
    @Override
    public String getName() {
        return ".coords";
    }

    @Override
    public void execute(String[] args) {
        String templePrefix = TextFormatting.AQUA + "[Temple] " + TextFormatting.RESET;

        double playerX = Minecraft.getMinecraft().player.posX;
        double playerY = Minecraft.getMinecraft().player.posY;
        double playerZ = Minecraft.getMinecraft().player.posZ;

        String coordinates = String.format(
                "X: %.2f, Y: %.2f, Z: %.2f",
                playerX,
                playerY,
                playerZ
        );

        setClipboardString(coordinates);

        Minecraft.getMinecraft().player.sendMessage(
                new TextComponentString(templePrefix + "Coordinates copied to clipboard: " + coordinates)
        );
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
