package xyz.templecheats.templeclient.features.module.modules.combat;

import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.manager.InventoryManager;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Replenish extends Module {

    /****************************************************************
     *                      Settings
     ****************************************************************/

    private final IntSetting threshold = new IntSetting("Threshold", this, 1, 63, 32);
    private final IntSetting tickDelay = new IntSetting("Tick Delay", this, 1, 10, 2);

    /****************************************************************
     *                      Variables
     ****************************************************************/
    private int delayStep = 0;

    public Replenish() {
        super("Replenish", "Automatically refills hotbar items", Keyboard.KEY_NONE, Category.Combat);
        this.registerSettings(threshold, tickDelay);
    }

    public void onUpdate() {

        if (mc.player == null) {
            return;
        }

        if (mc.currentScreen instanceof GuiContainer) {
            return;
        }

        if (delayStep < tickDelay.intValue()) {
            delayStep++;
            return;
        } else {
            delayStep = 0;
        }

        SlotPair slots = findReplenishableHotbarSlot();

        if (slots == null) {
            return;
        }

        int inventorySlot = slots.getInventorySlot();
        int hotbarSlot = slots.getHotbarSlot();
        mc.playerController.windowClick(0, inventorySlot, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(0, hotbarSlot + 36, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(0, inventorySlot, 0, ClickType.PICKUP, mc.player);
    }

    private SlotPair findReplenishableHotbarSlot() {
        List<ItemStack> inventory = mc.player.inventory.mainInventory;

        for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
            ItemStack stack = inventory.get(hotbarSlot);

            if (!stack.isStackable()) {
                continue;
            }

            if (stack.isEmpty() || stack.getItem() == Items.AIR) {
                continue;
            }

            if (stack.getCount() >= stack.getMaxStackSize() || stack.getCount() > threshold.intValue()) {
                continue;
            }

            int inventorySlot = findCompatibleInventorySlot(stack);

            if (inventorySlot == -1) {
                continue;
            }
            return new SlotPair(inventorySlot, hotbarSlot);
        }
        return null;
    }

    private int findCompatibleInventorySlot(ItemStack hotbarStack) {
        List<Integer> potentialSlots;

        Item item = hotbarStack.getItem();
        if (item instanceof ItemBlock) {
            potentialSlots = InventoryManager.findAllBlockSlots(((ItemBlock) item).getBlock().getClass());
        } else {
            potentialSlots = InventoryManager.findAllItemSlots(item.getClass());
        }

        potentialSlots = potentialSlots.stream()
                .filter(integer -> integer > 8 && integer < 36)
                .sorted(Comparator.comparingInt(integer -> -integer))
                .collect(Collectors.toList());

        for (int slot : potentialSlots) {
            if (isCompatibleStacks(hotbarStack, mc.player.inventory.getStackInSlot(slot))) {
                return slot;
            }
        }
        return -1;
    }

    private boolean isCompatibleStacks(ItemStack stack1, ItemStack stack2) {
        if (!stack1.getItem().equals(stack2.getItem())) {
            return false;
        }
        if ((stack1.getItem() instanceof ItemBlock) && (stack2.getItem() instanceof ItemBlock)) {
            Block block1 = ((ItemBlock) stack1.getItem()).getBlock();
            Block block2 = ((ItemBlock) stack2.getItem()).getBlock();
            if (!block1.getDefaultState().equals(block2.getDefaultState())) {
                return false;
            }
        }
        if (!stack1.getDisplayName().equals(stack2.getDisplayName())) {
            return false;
        }
        if (stack1.getItemDamage() != stack2.getItemDamage()) {
            return false;
        }
        return true;
    }

    private static class SlotPair {
        private final int inventorySlot;
        private final int hotbarSlot;

        public SlotPair(int inventorySlot, int hotbarSlot) {
            this.inventorySlot = inventorySlot;
            this.hotbarSlot = hotbarSlot;
        }

        public int getInventorySlot() {
            return inventorySlot;
        }

        public int getHotbarSlot() {
            return hotbarSlot;
        }
    }
}
