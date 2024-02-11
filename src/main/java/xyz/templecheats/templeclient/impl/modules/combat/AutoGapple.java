package xyz.templecheats.templeclient.impl.modules.combat;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.api.setting.Setting;
import xyz.templecheats.templeclient.impl.modules.Module;

public class AutoGapple extends Module {
    private final Setting healthThreshold = new Setting("Health", this, 10, 1, 20, true);
    private boolean eatingGapple;
    private int originalSlot = -1;

    public AutoGapple() {
        super("AutoGapple","Automatically swaps & eats a (notch) apple when health is below the set threshold", Keyboard.KEY_NONE, Category.Combat);
        TempleClient.settingsManager.rSetting(healthThreshold);
    }


    @Override
    public void onUpdate() {
        if(mc.player.getHealth() > this.healthThreshold.getValInt()) {
            if(this.eatingGapple) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                this.eatingGapple = false;
            }

            if(this.originalSlot != -1) {
                mc.player.inventory.currentItem = this.originalSlot;
                this.originalSlot = -1;
            }
            return;
        }

        final int gappleSlot = this.getGappleSlot();

        if(gappleSlot == -999) {
            return;
        }

        if(gappleSlot != -1) {
            if(this.originalSlot == -1) {
                this.originalSlot = mc.player.inventory.currentItem;
            }
            mc.player.inventory.currentItem = gappleSlot;
        }

        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
        this.eatingGapple = true;
    }

    @Override
    public void onEnable() {
        this.eatingGapple = false;
        this.originalSlot = -1;
    }

    @Override
    public void onDisable() {
        if(this.eatingGapple) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
        }

        if(this.originalSlot != -1) {
            mc.player.inventory.currentItem = this.originalSlot;
        }
    }

    private int getGappleSlot() {
        final ItemStack mainHand = mc.player.getHeldItem(EnumHand.MAIN_HAND);
        if(!mainHand.isEmpty() && mainHand.getItem() == Items.GOLDEN_APPLE) {
            return mc.player.inventory.currentItem;
        }

        final ItemStack offHand = mc.player.getHeldItem(EnumHand.OFF_HAND);
        if(!offHand.isEmpty() && offHand.getItem() == Items.GOLDEN_APPLE) {
            return -1;
        }

        int slot = -999;

        for(int i = 0; i < 9; i++) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);

            if(stack.getItem() == Items.GOLDEN_APPLE) {
                slot = i;

                if(stack.getMetadata() > 0) {
                    return slot;
                }
            }
        }

        return slot;
    }
}