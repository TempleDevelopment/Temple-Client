package xyz.templecheats.templeclient.features.modules.combat;

import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.modules.Module;
import net.minecraft.client.gui.GuiCommandBlock;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class AutoTotem extends Module {

    int delay = 0;
    int totems;
    int totemsOffHand;
    int totemSwtichDelay = 0;

    public AutoTotem() {
        super("AutoTotem", Keyboard.KEY_NONE, Category.COMBAT);
    }


    @Override
    public void onUpdate() {
        if (mc.world == null)
            return;
        totems = mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
        totemsOffHand = mc.player.inventory.offHandInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();

        for (int i = 0; i < 45; i++) {
            if (totems + totemsOffHand > 0) {
                if (!(mc.currentScreen instanceof GuiCrafting) && !(mc.currentScreen instanceof GuiFurnace) && !(mc.currentScreen instanceof GuiBeacon) && !(mc.currentScreen instanceof GuiBrewingStand) && !(mc.currentScreen instanceof GuiChest) && !(mc.currentScreen instanceof GuiCommandBlock) && !(mc.currentScreen instanceof GuiDispenser) && !(mc.currentScreen instanceof GuiEnchantment) && !(mc.currentScreen instanceof GuiShulkerBox) && !(mc.currentScreen instanceof GuiContainerCreative) && !(mc.currentScreen instanceof GuiHopper)) {
                    ItemStack stacks = mc.player.openContainer.getSlot(i).getStack();

                    if (stacks == ItemStack.EMPTY)
                        continue;
                    Item itemTotem = Items.TOTEM_OF_UNDYING;
                    if (mc.player.getHeldItemOffhand().isEmpty()) {
                        totemSwtichDelay++;
                        if (stacks.getItem() == itemTotem) {
                            if (totemSwtichDelay >= delay) {
                                mc.playerController.windowClick(0, i, 1, ClickType.PICKUP, mc.player);
                                mc.playerController.windowClick(0, 45, 1, ClickType.PICKUP, mc.player);
                                totemSwtichDelay = 0;
                            }
                        }
                    }

                }
            }
        }
    }

    @Override
    public void onEnable() {
        totemSwtichDelay = 0;
    }
}