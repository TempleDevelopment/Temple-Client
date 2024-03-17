package xyz.templecheats.templeclient.util.autocrystal;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public class InventoryUtil {
    private static final Minecraft mc = Minecraft.getMinecraft();
    
    public static int findFirstItemSlot(Class<? extends Item> itemToFind, int lower, int upper) {
        int slot = -1;
        List<ItemStack> mainInventory = mc.player.inventory.mainInventory;
        
        for(int i = lower; i <= upper; i++) {
            ItemStack stack = mainInventory.get(i);
            
            if(stack == ItemStack.EMPTY || !(itemToFind.isInstance(stack.getItem()))) {
                continue;
            }
            
            if(itemToFind.isInstance(stack.getItem())) {
                slot = i;
                break;
            }
        }
        return slot;
    }
}