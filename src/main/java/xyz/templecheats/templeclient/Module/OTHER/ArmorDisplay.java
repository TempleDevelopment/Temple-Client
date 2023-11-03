package xyz.templecheats.templeclient.Module.OTHER;

import xyz.templecheats.templeclient.Module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

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
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        int width = scaledResolution.getScaledWidth();
        int height = scaledResolution.getScaledHeight();
        int xPos = width / 2 - -8; // Adjust the horizontal position to move it slightly to the right
        int yPos = height - 55; // Adjust the vertical position as needed
        int iconSpacing = 22;

        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            if (slot.getSlotType() == EntityEquipmentSlot.Type.ARMOR) {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                mc.getRenderItem().renderItemAndEffectIntoGUI(mc.player.getItemStackFromSlot(slot), xPos, yPos);
                xPos += iconSpacing;
            }
        }
    }
}
