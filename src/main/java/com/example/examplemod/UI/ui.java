package com.example.examplemod.UI;

import com.example.examplemod.Client;
import com.example.examplemod.Module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class ui {
    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post e) {
        switch (e.getType()) {
            case TEXT:
                int y = 2; // Set the initial y position to match "Coords" module's background
                final int[] counter = {1};

                Minecraft mc = Minecraft.getMinecraft();
                FontRenderer fr = mc.fontRenderer;
                ScaledResolution sr = new ScaledResolution(mc);

                // Define the color as black
                Color backgroundColor = Color.BLACK;

                // Calculate the slightly smaller width of the black square (reduce by 10%)
                int rectWidth = (int) (fr.getStringWidth("Temple Client | FPS:") * 0.75) + 10 + 50;  // Reduce width by 10%

                // Calculate the height of the black square to barely touch the top and bottom of the text
                int rectHeight = fr.FONT_HEIGHT + 4;

                // Calculate the x position to render the black square on the left side
                int xPosition = 2;

                // Render the black square on the left side with a slightly smaller width
                Gui.drawRect(xPosition, y, xPosition + rectWidth, y + rectHeight, backgroundColor.getRGB());

                // Render the custom text with a purple "|"
                String customText = "Temple Client | " + "FPS: ";
                fr.drawString(customText, xPosition + 5, y + 2, Color.MAGENTA.getRGB()); // Purple color

                // Render the FPS value in white
                fr.drawString(String.valueOf(Minecraft.getDebugFPS()), xPosition + 5 + fr.getStringWidth(customText), y + 2, Color.WHITE.getRGB());

                for (Module module : Client.modules) {
                    if (module.toggled) {
                        // You have removed the purple background for enabled modules
                        fr.drawStringWithShadow(module.name, sr.getScaledWidth() - 4 - fr.getStringWidth(module.name), y, Color.MAGENTA.getRGB()); // Purple color
                        y += 10;
                        counter[0]++;
                    }
                }
                break;
            default:
                break;
        }
    }
}
