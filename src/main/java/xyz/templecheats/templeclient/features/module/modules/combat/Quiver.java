package xyz.templecheats.templeclient.features.module.modules.combat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.potion.PotionUtils;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.manager.InventoryManager;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;

import java.util.List;
import java.util.Objects;

public class Quiver extends Module {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    private final IntSetting tickDelay = new IntSetting("Tick Delay", this, 0, 8, 3);

    public Quiver() {
        super("Quiver", "Shoots yourself with positive potion effects", Keyboard.KEY_NONE, Category.Combat);
        registerSettings(tickDelay);
    }

    @Override
    public void onUpdate() {
        if (Quiver.mc.player != null) {
            List<Integer> arrowSlots;
            if (Quiver.mc.player.inventory.getCurrentItem().getItem() instanceof ItemBow && Quiver.mc.player.isHandActive() && Quiver.mc.player.getItemInUseMaxCount() >= this.tickDelay.intValue()) {
                Quiver.mc.player.connection.sendPacket((Packet) new CPacketPlayer.Rotation(Quiver.mc.player.cameraYaw, -90.0f, Quiver.mc.player.onGround));
                Quiver.mc.playerController.onStoppedUsingItem((EntityPlayer) Quiver.mc.player);
            }
            if ((arrowSlots = InventoryManager.getItemInventory(Items.TIPPED_ARROW)).get(0) == -1) {
                return;
            }
            int speedSlot = -1;
            int strengthSlot = -1;
            for (Integer slot : arrowSlots) {
                if (PotionUtils.getPotionFromItem((ItemStack) Quiver.mc.player.inventory.getStackInSlot(slot.intValue())).getRegistryName().getPath().contains("swiftness")) {
                    speedSlot = slot;
                    continue;
                }
                if (!Objects.requireNonNull(PotionUtils.getPotionFromItem((ItemStack) Quiver.mc.player.inventory.getStackInSlot(slot.intValue())).getRegistryName()).getPath().contains("strength"))
                    continue;
                strengthSlot = slot;
            }
        }
    }

    @Override
    public void onEnable() {
    }

    private int findBow() {
        return InventoryManager.getItemHotbar((Item) Items.BOW);
    }
}