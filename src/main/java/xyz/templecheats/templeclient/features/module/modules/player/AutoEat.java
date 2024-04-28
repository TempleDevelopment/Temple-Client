package xyz.templecheats.templeclient.features.module.modules.player;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import org.lwjgl.input.Keyboard;

import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.render.esp.sub.Block;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;

public class AutoEat extends Module {
    /*
     * Settings
     */
    private final IntSetting hungerThreshold = new IntSetting("Hunger", this, 0, 20, 10);
    private final BooleanSetting gapples = new BooleanSetting("Golden Apples", this, false);
    private final BooleanSetting poisonous = new BooleanSetting("Poisonous", this, false);
    private final IntSetting healthThreshold = new IntSetting("Health", this, 1, 20, 10);

    /*
     * Variables
     */
    private int previousSlot = -1;

    public AutoEat() {
        super("AutoEat", "Automatically eat when you are hungry", Keyboard.KEY_NONE, Category.Player);
        this.registerSettings(gapples, poisonous, healthThreshold, hungerThreshold);
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) return;
        RayTraceResult ray = Block.mc.objectMouseOver;
        if (ray != null && ray.typeOfHit == RayTraceResult.Type.BLOCK) {
            if (mc.world.getBlockState(ray.getBlockPos()).getBlock() == Blocks.ENDER_CHEST || mc.world.getBlockState(ray.getBlockPos()).getBlock() == Blocks.CHEST || mc.world.getBlockState(ray.getBlockPos()).getBlock() == Blocks.ANVIL)
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
            return;
        }
        if (mc.player.getHealth() <= healthThreshold.intValue() || mc.player.getFoodStats().getFoodLevel() <= hungerThreshold.intValue()) {
            for (int i = 0; i < 9; i++) {
                ItemStack stack = mc.player.inventory.getStackInSlot(i);
                Item item = stack.getItem();
                if (item instanceof ItemFood && (gapples.booleanValue() || item != Items.GOLDEN_APPLE) && (!poisonous.booleanValue() || item != Items.SPIDER_EYE && item != Items.ROTTEN_FLESH && item != Items.POISONOUS_POTATO && item != Items.CHORUS_FRUIT && !(item == Items.FISH && stack.getMetadata() == 3) && item != Items.CHICKEN)) {
                    previousSlot = mc.player.inventory.currentItem;
                    mc.player.inventory.currentItem = i;
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
                    KeyBinding.onTick(mc.gameSettings.keyBindUseItem.getKeyCode());
                    break;
                }
            }
        } else if (previousSlot != -1) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
            mc.player.inventory.currentItem = previousSlot;
            previousSlot = -1;
        }
    }
    @Override
    public void onDisable() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
    }
}