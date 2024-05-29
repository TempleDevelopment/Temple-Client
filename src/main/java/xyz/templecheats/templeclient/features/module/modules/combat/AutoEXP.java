package xyz.templecheats.templeclient.features.module.modules.combat;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.mixins.accessor.IPlayerControllerMP;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;

import java.util.List;

public class AutoEXP extends Module {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    private final BooleanSetting sneakOnly = new BooleanSetting("Sneak Only", this, true);
    private final BooleanSetting noEntityCollision = new BooleanSetting("No Collision", this, true);
    private final BooleanSetting silentSwitch = new BooleanSetting("Silent Switch", this, true);
    private final IntSetting minDamage = new IntSetting("Min Damage", this, 1, 100, 50);
    private final IntSetting maxHeal = new IntSetting("Repair To", this, 1, 100, 90);
    private final BooleanSetting predict = new BooleanSetting("Predict", this, false);

    /****************************************************************
     *                      Variables
     ****************************************************************/
    private char toMend = 0;

    public AutoEXP() {
        super("AutoEXP", "Automatically repair your armor", Keyboard.KEY_NONE, Category.Combat);
        registerSettings(sneakOnly, noEntityCollision, silentSwitch, predict, minDamage, maxHeal);
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null || mc.player.ticksExisted < 10) {
            return;
        }

        int sumOfDamage = 0;

        List<ItemStack> armor = mc.player.inventory.armorInventory;
        for (int i = 0; i < armor.size(); i++) {
            ItemStack itemStack = armor.get(i);
            if (itemStack.isEmpty()) {
                continue;
            }

            float damageOnArmor = (float) (itemStack.getMaxDamage() - itemStack.getItemDamage());
            float damagePercent = 100 - (100 * (1 - damageOnArmor / itemStack.getMaxDamage()));

            if (damagePercent <= maxHeal.intValue()) {
                if (damagePercent <= minDamage.intValue()) {
                    toMend |= 1 << i;
                }
                if (predict.booleanValue()) {
                    sumOfDamage += (itemStack.getMaxDamage() * maxHeal.intValue() / 100f) - (itemStack.getMaxDamage() - itemStack.getItemDamage());
                }
            } else {
                toMend &= ~(1 << i);
            }
        }

        if (toMend > 0) {
            if (predict.booleanValue()) {
                int totalXp = mc.world.loadedEntityList.stream()
                        .filter(entity -> entity instanceof EntityXPOrb)
                        .filter(entity -> entity.getDistanceSq(mc.player) <= 1)
                        .mapToInt(entity -> ((EntityXPOrb) entity).xpValue).sum();
                if ((totalXp * 2) < sumOfDamage) {
                    mendArmor(mc.player.inventory.currentItem);
                }
            } else {
                mendArmor(mc.player.inventory.currentItem);
            }
        }
    }

    private void mendArmor(int oldSlot) {
        if (noEntityCollision.booleanValue()) {
            for (EntityPlayer entityPlayer : mc.world.playerEntities) {
                if (entityPlayer.getDistance(mc.player) < 1 && entityPlayer != mc.player) {
                    return;
                }
            }
        }

        if (sneakOnly.booleanValue() && !mc.player.isSneaking()) {
            return;
        }

        int newSlot = findXPSlot();

        if (newSlot == -1) {
            return;
        }

        if (oldSlot != newSlot) {
            if (silentSwitch.booleanValue()) {
                mc.player.connection.sendPacket(new CPacketHeldItemChange(newSlot));
            } else {
                mc.player.inventory.currentItem = newSlot;
            }
            ((IPlayerControllerMP) mc.playerController).invokeSyncCurrentPlayItem();
        }

        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(0, 90, true));
        mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
        if (silentSwitch.booleanValue()) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
        } else {
            mc.player.inventory.currentItem = oldSlot;
        }
        ((IPlayerControllerMP) mc.playerController).invokeSyncCurrentPlayItem();
    }

    private int findXPSlot() {
        int slot = -1;

        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.EXPERIENCE_BOTTLE) {
                slot = i;
                break;
            }
        }

        return slot;
    }
}