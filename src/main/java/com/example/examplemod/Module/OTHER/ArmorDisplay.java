package com.example.examplemod.Module.OTHER;

import com.example.examplemod.Module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class ArmorDisplay extends Module {
    public ArmorDisplay() {
        super("ArmorHUD", Keyboard.KEY_NONE, Category.OTHER);
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            drawEquippedArmor();
        }
    }

    private void drawEquippedArmor() {
        Minecraft mc = Minecraft.getMinecraft();
        int xPos = 2;
        int yPos = 25;

        // Define the color as black
        Color backgroundColor = Color.BLACK;

        // Calculate the width and height of the black square
        int rectWidth = 80; // Adjust as needed
        int rectHeight = 20; // Adjust as needed

        // Render the black square for armor display
        Gui.drawRect(xPos, yPos, xPos + rectWidth, yPos + rectHeight, backgroundColor.getRGB());

        // Render the equipped armor textures
        RenderHelper.enableGUIStandardItemLighting();

        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            if (slot.getSlotType() == EntityEquipmentSlot.Type.ARMOR) {
                mc.getRenderItem().renderItemAndEffectIntoGUI(mc.player.getItemStackFromSlot(slot), xPos + 6, yPos + 3);
                xPos += 18; // Adjust spacing as needed
            }
        }

        RenderHelper.disableStandardItemLighting();
    }
}
