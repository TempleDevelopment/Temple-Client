package xyz.templecheats.templeclient.features.module.modules.combat;

import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayer;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.manager.ModuleManager;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;

import java.util.Arrays;

public  class Offhand extends Module {
    /*
     * Settings
     */
    private final DoubleSetting health = new DoubleSetting("Health", this, 0.0d, 36.0d, 14.0d);
    private final DoubleSetting defaultHealthVal = new DoubleSetting("DHV", this, 0.0f, 36.0f, 14.0f);

    public Offhand() {
        super("Offhand", "Puts items in you're offhand", Keyboard.KEY_NONE, Category.Combat);
        registerSettings(health, defaultHealthVal);
    }

    @Override
    public void onUpdate() {
        final int slot = slot();
        if (slot != -1) {
            swapItem(slot);
        }
        if (mc.player.getHeldItemOffhand().isEmpty()) {
            inventorySlot(Items.END_CRYSTAL);
        }
    }
    private int slot() {
        if (mc.currentScreen != null) {
            return -1;
        }
        final int totem = inventorySlot(Items.TOTEM_OF_UNDYING);
        if (totem == -1) {
            health.setDoubleValue(0.1f);
        } else {
            health.setDoubleValue(defaultHealthVal.doubleValue());
        }
        if (mc.player.getHealth() + mc.player.getAbsorptionAmount() <= health.doubleValue()) {
            return totem;
        }
        if (mc.player.getHeldItemMainhand().getItem().equals(Items.DIAMOND_SWORD)) {
            if (mc.gameSettings.keyBindUseItem.isKeyDown()) {
                return inventorySlot(Items.GOLDEN_APPLE);
            }

        }
        final int crystal = inventorySlot(Items.END_CRYSTAL);
        if (crystal != -1) {
            return inventorySlot(Items.END_CRYSTAL);
        }
        return totem;
    }

    private void swapItem(final int i) {
        final Item item = mc.player.inventory.getStackInSlot(i).getItem();
        if (!mc.player.getHeldItemOffhand().getItem().equals(item)) {
            int slot = i < 9 ? i + 36 : i;
            swap(new int[]{slot, 45, slot});
            mc.playerController.updateController();
        }
    }

    private void swap(final int[] slots) {
        if (mc.getConnection() != null) {
            Arrays.stream(slots).forEach(i -> mc.playerController.windowClick(0, i, 0, ClickType.PICKUP, mc.player));
            mc.getConnection().sendPacket(new CPacketPlayer());
        }
    }

    public int inventorySlot(final Item item) {
        int itemSlot = -1;
        for (int i = 45; i > 0; --i) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem().equals(item)) {
                itemSlot = i;
                break;
            }
        }
        return itemSlot;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        AutoTotem autoTotem = (AutoTotem) ModuleManager.getModuleByName("AutoTotem");
        if (autoTotem.isEnabled()) {
            autoTotem.disable();
        }
    }
}

