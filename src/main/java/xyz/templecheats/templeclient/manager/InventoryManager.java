package xyz.templecheats.templeclient.manager;

import net.minecraft.block.Block;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import xyz.templecheats.templeclient.features.module.modules.combat.AutoTotem;
import xyz.templecheats.templeclient.mixins.accessor.IPlayerControllerMP;
import xyz.templecheats.templeclient.util.Globals;

import java.util.ArrayList;
import java.util.List;

public class InventoryManager implements Globals {

    /****************************************************************
     *                    Inventory Switch Methods
     ****************************************************************/

    public void switchToSlot(int in, Switch swap) {
        if (InventoryPlayer.isHotbar(in)) {
            if (mc.player.inventory.currentItem != in) {
                switch (swap) {
                    case Normal:
                        mc.player.inventory.currentItem = in;
                        mc.player.connection.sendPacket(new CPacketHeldItemChange(in));
                        break;
                    case Packet:
                        mc.player.connection.sendPacket(new CPacketHeldItemChange(in));
                        ((IPlayerControllerMP) mc.playerController).setCurrentPlayerItem(in);
                        break;
                }
            }
        }
    }

    public void switchToItem(Item in, Switch swap) {
        int slot = searchSlot(in, InventoryRegion.HOTBAR);
        switchToSlot(slot, swap);
    }

    /****************************************************************
     *                    Inventory Search Methods
     ****************************************************************/

    public int searchSlot(Item in, InventoryRegion inventoryRegion) {
        int slot = -1;
        for (int i = inventoryRegion.getStart(); i < inventoryRegion.getBound(); i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem().equals(in)) {
                slot = i;
                break;
            }
        }
        return slot;
    }

    public static int findTotemSlot(int lower, int upper) {
        int slot = -1;
        List<ItemStack> mainInventory = mc.player.inventory.mainInventory;
        for (int i = lower; i <= upper; i++) {
            ItemStack stack = mainInventory.get(i);
            if (stack == ItemStack.EMPTY || stack.getItem() != Items.TOTEM_OF_UNDYING) continue;

            slot = i;
            break;
        }
        return slot;
    }

    public static int findObsidianSlot(boolean offHandActived, boolean activeBefore) {
        int slot = -1;
        List<ItemStack> mainInventory = mc.player.inventory.mainInventory;

        if (offHandActived && AutoTotem.isActive()) {
            if (!activeBefore) {
                AutoTotem.requestObsidian();
            }
            return 9;
        }

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mainInventory.get(i);
            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)) continue;

            Block block = ((ItemBlock) stack.getItem()).getBlock();
            if (block instanceof BlockObsidian) {
                slot = i;
                break;
            }
        }
        return slot;
    }

    public static int findFirstBlockSlot(Class<? extends Block> blockToFind, int lower, int upper) {
        int slot = -1;
        List<ItemStack> mainInventory = mc.player.inventory.mainInventory;

        for (int i = lower; i <= upper; i++) {
            ItemStack stack = mainInventory.get(i);
            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)) continue;

            if (blockToFind.isInstance(((ItemBlock) stack.getItem()).getBlock())) {
                slot = i;
                break;
            }
        }
        return slot;
    }

    public static int findFirstItemSlot(Class<? extends Item> itemToFind, int lower, int upper) {
        int slot = -1;
        List<ItemStack> mainInventory = mc.player.inventory.mainInventory;

        for (int i = lower; i <= upper; i++) {
            ItemStack stack = mainInventory.get(i);
            if (stack == ItemStack.EMPTY || !(itemToFind.isInstance(stack.getItem()))) continue;

            if (itemToFind.isInstance(stack.getItem())) {
                slot = i;
                break;
            }
        }
        return slot;
    }

    public static List<Integer> getItemInventory(Item item) {
        ArrayList<Integer> slots = new ArrayList<>();
        for (int i = 9; i < 36; ++i) {
            Item target = mc.player.inventory.getStackInSlot(i).getItem();
            if (!(item instanceof ItemBlock) || !((ItemBlock) item).getBlock().equals((Object) item)) continue;
            slots.add(i);
        }
        if (slots.isEmpty()) {
            slots.add(-1);
        }
        return slots;
    }


    public static int getItemHotbar(Item input) {
        for (int i = 0; i < 9; ++i) {
            Item item = mc.player.inventory.getStackInSlot(i).getItem();
            if (Item.getIdFromItem(item) != Item.getIdFromItem(input)) continue;
            return i;
        }
        return -1;
    }

    public static boolean getHeldItem(Item item) {
        return mc.player.getHeldItemMainhand().getItem().equals(item) || mc.player.getHeldItemOffhand().getItem().equals(item);
    }

    public static List<Integer> findAllItemSlots(Class<? extends Item> itemToFind) {
        List<Integer> slots = new ArrayList<>();
        List<ItemStack> mainInventory = mc.player.inventory.mainInventory;

        for (int i = 0; i < 36; i++) {
            ItemStack stack = mainInventory.get(i);

            if (stack == ItemStack.EMPTY || !(itemToFind.isInstance(stack.getItem()))) {
                continue;
            }

            slots.add(i);
        }
        return slots;
    }

    public static List<Integer> findAllBlockSlots(Class<? extends Block> blockToFind) {
        List<Integer> slots = new ArrayList<>();
        List<ItemStack> mainInventory = mc.player.inventory.mainInventory;

        for (int i = 0; i < 36; i++) {
            ItemStack stack = mainInventory.get(i);

            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)) {
                continue;
            }

            if (blockToFind.isInstance(((ItemBlock) stack.getItem()).getBlock())) {
                slots.add(i);
            }
        }
        return slots;
    }

    /****************************************************************
     *                      Inventory Enums
     ****************************************************************/

    public enum Switch {
        Normal,
        Packet,
        None
    }

    public enum InventoryRegion {
        INVENTORY(0, 45),
        HOTBAR(0, 8),
        CRAFTING(80, 83),
        ARMOR(100, 103);

        private final int start, bound;

        InventoryRegion(int start, int bound) {
            this.start = start;
            this.bound = bound;
        }

        public int getStart() {
            return start;
        }

        public int getBound() {
            return bound;
        }
    }
}
