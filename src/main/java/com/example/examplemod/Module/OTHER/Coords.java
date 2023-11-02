package com.example.examplemod.Module.OTHER;

import com.example.examplemod.Module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.FontRenderer;

import java.awt.Color;

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
        ScaledResolution sr = new ScaledResolution(mc);

        String coordinates = String.format("%.2f, %.2f, %.2f", mc.player.posX, mc.player.posY, mc.player.posZ);

        int textWidth = mc.fontRenderer.getStringWidth(coordinates); // Calculate the text width

        // Calculate the x-position to center the text
        int xPos = (sr.getScaledWidth() - textWidth) / 2;
        int yPos = 2;

        // Define the color as dark gray (0x181818)
        Color textColor = new Color(0xFF00FF); // Purple text color

        // Reduce the font size by half (original size divided by 2)
        int originalFontSize = mc.fontRenderer.FONT_HEIGHT;
        int smallerFontSize = originalFontSize / 2;

        // Save the original font size
        int prevFontSize = mc.fontRenderer.FONT_HEIGHT;

        // Set the new font size
        mc.fontRenderer.FONT_HEIGHT = smallerFontSize;

        // Calculate the width and height of the gray square
        int rectWidth = 140;
        int rectHeight = smallerFontSize + 2; // Adjust height to match the new font size

        // Make the text purple
        mc.fontRenderer.drawString(coordinates, xPos, yPos, textColor.getRGB());

        // Restore the original font size
        mc.fontRenderer.FONT_HEIGHT = prevFontSize;
    }
}
