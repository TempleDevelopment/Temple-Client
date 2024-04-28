package xyz.templecheats.templeclient.manager;

import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import xyz.templecheats.templeclient.mixins.accessor.IPlayerControllerMP;
import xyz.templecheats.templeclient.util.Globals;

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
    public int searchSlot(Block[] in, InventoryRegion inventoryRegion) {

        int slot = -1;

        for (int i = inventoryRegion.getStart(); i < inventoryRegion.getBound(); i++) {

            for (Block block: in) {

                if (slot == -1 && mc.player.inventory.getStackInSlot(i).getItem().equals(Item.getItemFromBlock(block))) {
                    slot = i;
                    break;
                }
            }
        }

        return slot;
    }
    public int searchSlot(Class < ? extends Item > in, InventoryRegion inventoryRegion) {

        int slot = -1;

        for (int i = inventoryRegion.getStart(); i <= inventoryRegion.getBound(); i++) {

            if (in.isInstance(mc.player.inventory.getStackInSlot(i).getItem())) {
                slot = i;
                break;
            }
        }

        return slot;
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