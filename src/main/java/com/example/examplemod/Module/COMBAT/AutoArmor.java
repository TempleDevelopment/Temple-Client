package com.example.examplemod.Module.COMBAT;

import com.example.examplemod.Module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import org.lwjgl.input.Keyboard;

public class AutoArmor extends Module {
    public AutoArmor() {
        super("AutoArmor", Keyboard.KEY_NONE, Category.COMBAT);
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.getGui() instanceof GuiInventory) {
            autoEquipArmor();
        }
    }

    private void autoEquipArmor() {
        Minecraft mc = Minecraft.getMinecraft();

        for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            if (slot.getSlotType() == EntityEquipmentSlot.Type.ARMOR) {
                ItemStack stack = findBestArmorItem(slot);

                if (!stack.isEmpty()) {
                    int armorSlotIndex = slot.getIndex();

                    // Place the armor item in the armor slot directly
                    mc.playerController.windowClick(0, armorSlotIndex, 0, ClickType.PICKUP_ALL, mc.player);
                }
            }
        }
    }

    private ItemStack findBestArmorItem(EntityEquipmentSlot slot) {
        Minecraft mc = Minecraft.getMinecraft();
        ItemStack bestStack = ItemStack.EMPTY;

        for (int i = 9; i < 45; i++) {
            ItemStack stack = mc.player.inventoryContainer.getSlot(i).getStack();

            if (stack.getItem().isValidArmor(stack, slot, mc.player)) {
                if (bestStack.isEmpty() || stack.getMaxDamage() - stack.getItemDamage() > bestStack.getMaxDamage() - bestStack.getItemDamage()) {
                    bestStack = stack;
                }
            }
        }

        return bestStack;
    }
}
