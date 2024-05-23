package xyz.templecheats.templeclient.manager;

import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import xyz.templecheats.templeclient.mixins.accessor.IPlayerControllerMP;
import xyz.templecheats.templeclient.util.Globals;

import java.util.ArrayList;
import java.util.List;

public class InventoryManager implements Globals {

    public void switchToSlot(int in , Switch swap) {
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
    public void switchToItem(Item in , Switch swap) {
        int slot = searchSlot(in, InventoryRegion.HOTBAR);
        switchToSlot(slot, swap);
    }

    public int searchSlot(Item in , InventoryRegion inventoryRegion) {
        int slot = -1;
        for (int i = inventoryRegion.getStart(); i < inventoryRegion.getBound(); i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem().equals(in)) {
                slot = i;
                break;
            }
        }

        return slot;
    }
    public static List<Integer> getItemInventory(Item item) {
        ArrayList<Integer> ints = new ArrayList<Integer>();
        for (int i = 9; i < 36; ++i) {
            Item target = mc.player.inventory.getStackInSlot(i).getItem();
            if (!(item instanceof ItemBlock) || !((ItemBlock)item).getBlock().equals((Object)item)) continue;
            ints.add(i);
        }
        if (ints.size() == 0) {
            ints.add(-1);
        }
        return ints;
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