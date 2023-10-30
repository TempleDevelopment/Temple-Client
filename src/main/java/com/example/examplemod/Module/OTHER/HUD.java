package com.example.examplemod.Module.OTHER;

import com.example.examplemod.Module.Module;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class HUD extends Module {
    public HUD() {
        super("HUD", Keyboard.KEY_NONE, Category.OTHER);
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
        int yPos = 2;

        String coordinates = String.format("XYZ: %.2f, %.2f, %.2f", mc.player.posX, mc.player.posY, mc.player.posZ);

        // Make the text purple (0xFF00FF represents purple)
        mc.fontRenderer.drawString(coordinates, xPos, yPos, 0xFF00FF);
    }
}
