package xyz.templecheats.templeclient.features.module.modules.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;

public class AutoGapple extends Module {
    private final IntSetting healthThreshold = new IntSetting("Health", this, 1, 20, 10);
    private boolean isEatingGapple = false;
    private int originalSlot = -1;

    public AutoGapple() {
        super("AutoGapple", "Automatically swaps & eats a (notch) apple when health is below the set threshold or not fully recovered", Keyboard.KEY_NONE, Category.Combat);
        TempleClient.settingsManager.rSetting(healthThreshold);
    }

    @Override
    public void onUpdate() {
        if (!isEatingGapple && (Minecraft.getMinecraft().player.getHealth() <= this.healthThreshold.intValue() || (isEatingGapple && Minecraft.getMinecraft().player.getHealth() < Minecraft.getMinecraft().player.getMaxHealth()))) {
            final int gappleSlot = this.getGappleSlot();

            if (gappleSlot == -999) {
                return;
            }

            if (gappleSlot != -1) {
                if (this.originalSlot == -1) {
                    this.originalSlot = Minecraft.getMinecraft().player.inventory.currentItem;
                }
                Minecraft.getMinecraft().player.inventory.currentItem = gappleSlot;
            }

            KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindUseItem.getKeyCode(), true);
            isEatingGapple = true;
        } else if (Minecraft.getMinecraft().player.getHealth() >= Minecraft.getMinecraft().player.getMaxHealth() && isEatingGapple) {
            stopEating();
        }
    }

    private void stopEating() {
        KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindUseItem.getKeyCode(), false);
        if (this.originalSlot != -1) {
            Minecraft.getMinecraft().player.inventory.currentItem = this.originalSlot;
            this.originalSlot = -1;
        }
        isEatingGapple = false;
    }

    @Override
    public void onEnable() {
        isEatingGapple = false;
        originalSlot = -1;
    }

    @Override
    public void onDisable() {
        stopEating();
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