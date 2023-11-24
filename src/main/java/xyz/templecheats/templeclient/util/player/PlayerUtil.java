package xyz.templecheats.templeclient.util.player;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;

public class PlayerUtil {

    public static float[] getPlayerFacingRotations(double posX, double posY, double posZ) {
        Minecraft mc = Minecraft.getMinecraft();

        double deltaX = posX - mc.player.posX;
        double deltaY = posY - mc.player.posY - mc.player.getEyeHeight();
        double deltaZ = posZ - mc.player.posZ;
        double deltaGround = Math.sqrt(deltaX*deltaX + deltaZ*deltaZ);

        float pitch = (float) - Math.toDegrees(Math.atan(deltaY/deltaGround));
        float yaw = (float) - Math.toDegrees(Math.atan(deltaX/deltaZ));

        if (deltaZ <= 0) {
            if (deltaX > 0) {
                yaw = yaw - 180F;
            } else {
                yaw = yaw + 180F;
            }
        }

        return new float[] {pitch, yaw};
    }

    public static double[] getPlayerMoveVec() {
        Minecraft mc = Minecraft.getMinecraft();

        float yaw = mc.player.rotationYaw;
        float forward = mc.player.moveForward;
        float strafe = mc.player.moveStrafing;

        if (forward == 0 && strafe == 0) {
            return new double[] {0, 0};
        }

        if (forward > 0) {

            if (strafe > 0) {
                yaw = yaw - 45;
            } else if (strafe < 0) {
                yaw = yaw + 45;
            }

        } else if (forward < 0) {

            yaw = yaw - 180;
            if (strafe > 0) {
                yaw = yaw + 45;
            } else if (strafe < 0) {
                yaw = yaw - 45;
            }

        } else {

            if (strafe > 0) {
                yaw = yaw - 90;
            } else if (strafe < 0) {
                yaw = yaw + 90;
            }
        }

        return new double[] {-Math.sin(Math.toRadians(yaw)), Math.cos(Math.toRadians(yaw))};
    }

    public static void swapContainerItems(int slot1, int slot2) {
        Minecraft mc = Minecraft.getMinecraft();

        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot1, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot2, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot1, 0, ClickType.PICKUP, mc.player);
        mc.playerController.updateController();
    }

    public static void swapInventoryItems(int slot1, int slot2) {
        Minecraft mc = Minecraft.getMinecraft();

        short short1 = mc.player.inventoryContainer.getNextTransactionID(mc.player.inventory);

        ItemStack itemstack = mc.player.inventoryContainer.slotClick(slot1, 0, ClickType.PICKUP, mc.player);
        mc.player.connection.sendPacket(new CPacketClickWindow(mc.player.inventoryContainer.windowId, slot1, 0, ClickType.PICKUP, itemstack, short1));

        itemstack = mc.player.inventoryContainer.slotClick(slot2, 0, ClickType.PICKUP, mc.player);
        mc.player.connection.sendPacket(new CPacketClickWindow(mc.player.inventoryContainer.windowId, slot2, 0, ClickType.PICKUP, itemstack, short1));

        itemstack = mc.player.inventoryContainer.slotClick(slot1, 0, ClickType.PICKUP, mc.player);
        mc.player.connection.sendPacket(new CPacketClickWindow(mc.player.inventoryContainer.windowId, slot1, 0, ClickType.PICKUP, itemstack, short1));

        mc.playerController.updateController();
    }
}