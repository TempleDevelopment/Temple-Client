package com.example.examplemod.Module.OTHER;

import com.example.examplemod.Module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class Coords extends Module {
    public Coords() {
        super("Coords", Keyboard.KEY_NONE, Category.OTHER);
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            drawXYZCoordinates();
        }
    }

    private void drawXYZCoordinates() {
        Minecraft mc = Minecraft.getMinecraft();
        int xPos = 2;
        int yPos = 15;

        String coordinates = String.format("XYZ: %.2f, %.2f, %.2f", mc.player.posX, mc.player.posY, mc.player.posZ);

        // Calculate the text width for the black background
        int textWidth = mc.fontRenderer.getStringWidth(coordinates);

        // Define the color as black
        Color backgroundColor = Color.BLACK;

        // Calculate the width and height of the black square
        int rectWidth = textWidth + 10; // Add 10 for some padding
        int rectHeight = mc.fontRenderer.FONT_HEIGHT + 4;

        // Render the black square behind the coordinates text
        Gui.drawRect(xPos, yPos, xPos + rectWidth, yPos + rectHeight, backgroundColor.getRGB());

        // Make the text purple (0xFF00FF represents purple)
        mc.fontRenderer.drawString(coordinates, xPos + 5, yPos + 2, 0xFF00FF);
    }
}
