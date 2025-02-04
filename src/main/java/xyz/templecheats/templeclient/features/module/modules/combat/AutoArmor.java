package xyz.templecheats.templeclient.features.module.modules.combat;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;

import java.util.Arrays;

public class AutoArmor extends Module {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    private final BooleanSetting pickupIfFull = new BooleanSetting("Pickup If Full", this, false);
    private final BooleanSetting preserve = new BooleanSetting("Preserve Damaged", this, false);
    private final BooleanSetting replace = new BooleanSetting("Replace Empty", this, false);
    private final IntSetting preserveDmg = new IntSetting("Damage %", this, 0, 100, 5);
    private final IntSetting delay = new IntSetting("Delay", this, 0, 20, 10);

    /****************************************************************
     *                      Variables
     ****************************************************************/
    private int[] bestArmorDamage;
    private int[] bestArmorSlots;
    private int timer;

    public AutoArmor() {
        super("AutoArmor", "Automatically equips your best armor", Keyboard.KEY_NONE, Category.Combat);

        registerSettings(pickupIfFull, preserve, replace, preserveDmg, delay);
    }

    @Override
    public void onUpdate() {
        if (AutoArmor.mc.currentScreen instanceof GuiContainer && !(AutoArmor.mc.currentScreen instanceof InventoryEffectRenderer)) {
            return;
        }
        timer++;
        this.searchSlots();
        for (int i = 0; i < 4; ++i) {
            if (this.bestArmorSlots[i] != -1) {
                int bestSlot = this.bestArmorSlots[i];
                if (bestSlot < 9) {
                    bestSlot += 36;
                }
                if (!AutoArmor.mc.player.inventory.armorItemInSlot(i).isEmpty()) {
                    if (AutoArmor.mc.player.inventory.getFirstEmptyStack() == -1 && pickupIfFull.booleanValue()) {
                        AutoArmor.mc.playerController.windowClick(AutoArmor.mc.player.inventoryContainer.windowId, 8 - i, 0, ClickType.PICKUP, (EntityPlayer) AutoArmor.mc.player);
                        AutoArmor.mc.playerController.windowClick(AutoArmor.mc.player.inventoryContainer.windowId, bestSlot, 0, ClickType.PICKUP, (EntityPlayer) AutoArmor.mc.player);
                        AutoArmor.mc.playerController.windowClick(AutoArmor.mc.player.inventoryContainer.windowId, 8 - i, 0, ClickType.PICKUP, (EntityPlayer) AutoArmor.mc.player);
                        continue;
                    }
                    AutoArmor.mc.playerController.windowClick(AutoArmor.mc.player.inventoryContainer.windowId, 8 - i, 0, ClickType.QUICK_MOVE, (EntityPlayer) AutoArmor.mc.player);
                    if (timer < delay.intValue()) return;
                }
                AutoArmor.mc.playerController.windowClick(AutoArmor.mc.player.inventoryContainer.windowId, bestSlot, 0, ClickType.QUICK_MOVE, (EntityPlayer) AutoArmor.mc.player);
                timer = 0;
            }
        }
    }

    private void searchSlots() {
        this.bestArmorDamage = new int[4];
        this.bestArmorSlots = new int[4];
        Arrays.fill(this.bestArmorDamage, -1);
        Arrays.fill(this.bestArmorSlots, -1);
        for (int i = 0; i < this.bestArmorSlots.length; ++i) {
            final ItemStack itemStack = AutoArmor.mc.player.inventory.armorItemInSlot(i);
            if (itemStack.getItem() instanceof ItemArmor) {
                final ItemArmor armor = (ItemArmor) itemStack.getItem();
                if (this.preserve.booleanValue()) {
                    final float dmg = (itemStack.getMaxDamage() - (float) itemStack.getItemDamage()) / itemStack.getMaxDamage();
                    final int percent = (int) (dmg * 100.0f);
                    if (percent > this.preserveDmg.intValue()) {
                        this.bestArmorDamage[i] = armor.damageReduceAmount;
                    }
                } else {
                    this.bestArmorDamage[i] = armor.damageReduceAmount;
                }
            } else if (itemStack.isEmpty() && !this.replace.booleanValue()) {
                this.bestArmorDamage[i] = Integer.MAX_VALUE;
            }
        }
        for (int i = 0; i < 36; ++i) {
            final ItemStack itemStack = AutoArmor.mc.player.inventory.getStackInSlot(i);
            if (itemStack.getCount() <= 1) {
                if (itemStack.getItem() instanceof ItemArmor) {
                    final ItemArmor armor = (ItemArmor) itemStack.getItem();
                    final int armorType = armor.armorType.ordinal() - 2;
                    if (this.bestArmorDamage[armorType] < armor.damageReduceAmount) {
                        this.bestArmorDamage[armorType] = armor.damageReduceAmount;
                        this.bestArmorSlots[armorType] = i;
                    }
                }
            }
        }
    }
}