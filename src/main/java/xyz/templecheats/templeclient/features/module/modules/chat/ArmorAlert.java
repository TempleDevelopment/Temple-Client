package xyz.templecheats.templeclient.features.module.modules.chat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;
import xyz.templecheats.templeclient.util.world.EntityUtil;

import java.util.HashMap;
import java.util.Map;

public class ArmorAlert extends Module {
    /*
     * Settings
     */
    public final IntSetting armorThreshold = new IntSetting("Armor %", this, 1, 100, 20);

    /*
     * Variables
     */
    private final Map<String, Boolean> warningSent = new HashMap<>();
    public ArmorAlert() {
        super("ArmorAlert", "Alerts you when your armor durability is low", Keyboard.KEY_NONE, Module.Category.Chat);
        registerSettings(armorThreshold);
    }

    @Override
    public void onUpdate() {
        EntityPlayer player = mc.player;
        if (player.isDead) return;

        for (ItemStack stack : player.inventory.armorInventory) {
            if (stack == ItemStack.EMPTY) continue;

            String armorPieceName = getArmorPieceName(stack);
            int percent = EntityUtil.getDamagePercent(stack);

            if (percent <= armorThreshold.intValue()) {
                if (!warningSent.getOrDefault(armorPieceName, false)) {
                    sendMessage(TextFormatting.RED + "[Temple] " + TextFormatting.WHITE + "Your " + armorPieceName + " is low!");
                    warningSent.put(armorPieceName, true);
                }
            } else {
                warningSent.put(armorPieceName, false);
            }
        }
    }

    protected void sendMessage(String message) {
        if (mc.player != null) {
            mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString(message));
        }
    }

    private String getArmorPieceName(ItemStack stack) {
        if (stack.getItem() == Items.DIAMOND_HELMET
                || stack.getItem() == Items.GOLDEN_HELMET
                || stack.getItem() == Items.IRON_HELMET
                || stack.getItem() == Items.CHAINMAIL_HELMET
                || stack.getItem() == Items.LEATHER_HELMET) {

            return "helmet";
        }

        if (stack.getItem() == Items.DIAMOND_CHESTPLATE
                || stack.getItem() == Items.GOLDEN_CHESTPLATE
                || stack.getItem() == Items.IRON_CHESTPLATE
                || stack.getItem() == Items.CHAINMAIL_CHESTPLATE
                || stack.getItem() == Items.LEATHER_CHESTPLATE) {

            return "chestplate";
        }

        if (stack.getItem() == Items.DIAMOND_LEGGINGS
                || stack.getItem() == Items.GOLDEN_LEGGINGS
                || stack.getItem() == Items.IRON_LEGGINGS
                || stack.getItem() == Items.CHAINMAIL_LEGGINGS
                || stack.getItem() == Items.LEATHER_LEGGINGS) {

            return "leggings";
        }

        return "boots";
    }
}