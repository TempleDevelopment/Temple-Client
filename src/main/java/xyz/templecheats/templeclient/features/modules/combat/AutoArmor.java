package xyz.templecheats.templeclient.features.modules.combat;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.features.modules.Module;
import xyz.templecheats.templeclient.features.modules.client.Panic;
import xyz.templecheats.templeclient.gui.clickgui.setting.Setting;
import xyz.templecheats.templeclient.gui.clickgui.setting.SettingsManager;
import xyz.templecheats.templeclient.util.time.Timer;

import java.util.Arrays;


public class AutoArmor extends Module {
    private Timer timer;
    private int[] bestArmorDamage;
    private int[] bestArmorSlots;

    public Setting pif;
    public Setting replace;
    public Setting preserve;
    public Setting preserveDMG;
    public Setting ms;

    public AutoArmor() {
        super("AutoArmor", Keyboard.KEY_NONE, Category.COMBAT);
        this.timer = new Timer();

        SettingsManager settingsManager = TempleClient.instance.settingsManager;

        pif = new Setting("Pickup If Full", this, false);
        replace = new Setting("Replace Empty", this, false);
        preserve = new Setting("Preserve Damaged", this, false);

        preserveDMG = new Setting("Damage Percentage", this, 5, 0, 100, true);
        ms = new Setting("MS delay", this, 500, 0, 1000, true);

        settingsManager.rSetting(pif);
        settingsManager.rSetting(replace);
        settingsManager.rSetting(preserve);
        settingsManager.rSetting(preserveDMG);
        settingsManager.rSetting(ms);
    }

    @Override
    public void onUpdate() {
        if (AutoArmor.mc.currentScreen instanceof GuiContainer && !(AutoArmor.mc.currentScreen instanceof InventoryEffectRenderer)) {
            return;
        }
        this.searchSlots();
        for (int i = 0; i < 4; ++i) {
            if (this.bestArmorSlots[i] != -1) {
                int bestSlot = this.bestArmorSlots[i];
                if (bestSlot < 9) {
                    bestSlot += 36;
                }
                if (!AutoArmor.mc.player.inventory.armorItemInSlot(i).isEmpty()) {
                    if (AutoArmor.mc.player.inventory.getFirstEmptyStack() == -1 && this.pif.getValBoolean()) {
                        AutoArmor.mc.playerController.windowClick(AutoArmor.mc.player.inventoryContainer.windowId, 8 - i, 0, ClickType.PICKUP, (EntityPlayer) AutoArmor.mc.player);
                        AutoArmor.mc.playerController.windowClick(AutoArmor.mc.player.inventoryContainer.windowId, bestSlot, 0, ClickType.PICKUP, (EntityPlayer) AutoArmor.mc.player);
                        AutoArmor.mc.playerController.windowClick(AutoArmor.mc.player.inventoryContainer.windowId, 8 - i, 0, ClickType.PICKUP, (EntityPlayer) AutoArmor.mc.player);
                        continue;
                    }
                    AutoArmor.mc.playerController.windowClick(AutoArmor.mc.player.inventoryContainer.windowId, 8 - i, 0, ClickType.QUICK_MOVE, (EntityPlayer) AutoArmor.mc.player);
                    if (!this.timer.hasReached(this.ms.getValInt())) {
                        return;
                    }
                }
                AutoArmor.mc.playerController.windowClick(AutoArmor.mc.player.inventoryContainer.windowId, bestSlot, 0, ClickType.QUICK_MOVE, (EntityPlayer) AutoArmor.mc.player);
                this.timer.reset();
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
                final ItemArmor armor = (ItemArmor)itemStack.getItem();
                if (this.preserve.getValBoolean()) {
                    final float dmg = (itemStack.getMaxDamage() - (float)itemStack.getItemDamage()) / itemStack.getMaxDamage();
                    final int percent = (int)(dmg * 100.0f);
                    if (percent > this.preserveDMG.getValInt()) {
                        this.bestArmorDamage[i] = armor.damageReduceAmount;
                    }
                }
                else {
                    this.bestArmorDamage[i] = armor.damageReduceAmount;
                }
            }
            else if (itemStack.isEmpty() && !this.replace.getValBoolean()) {
                this.bestArmorDamage[i] = Integer.MAX_VALUE;
            }
        }
        for (int i = 0; i < 36; ++i) {
            final ItemStack itemStack = AutoArmor.mc.player.inventory.getStackInSlot(i);
            if (itemStack.getCount() <= 1) {
                if (itemStack.getItem() instanceof ItemArmor) {
                    final ItemArmor armor = (ItemArmor)itemStack.getItem();
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
