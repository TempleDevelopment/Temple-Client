package com.example.examplemod.UI;

import com.example.examplemod.Client;
import com.example.examplemod.Module.Module;
import font.FontUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import static net.minecraft.client.gui.Gui.drawRect;

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

                drawRect(4, 6, 110, 15, new Color(0x181818).hashCode());
                drawRect(4, 6, 110, 5, new Color(128, 0, 128).getRGB()); // Purple color

                FontUtils.normal.drawString(Client.cName + " | " + mc.getSession().getUsername() +
                        " | FPS: " + Minecraft.getDebugFPS(), 7, 10, -1);

                for (Module module : Client.modules) {
                    if (module.toggled) {
                        int moduleHeight = 10; // Set the height of each module
                        Gui.drawRect(sr.getScaledWidth(), y, sr.getScaledWidth() - 2, y + moduleHeight, new Color(128, 0, 128).getRGB()); // Purple color
                        fr.drawStringWithShadow(module.name, sr.getScaledWidth() - 4 - fr.getStringWidth(module.name), y + 1, -1); // Adjust the y position
                        y += moduleHeight; // Increase the y position for the next module
                    }
                }
                break;
            default:
                break;
        }
    }
}
