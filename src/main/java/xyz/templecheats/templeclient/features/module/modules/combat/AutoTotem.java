/*
 * This AutoTotem was made by GameSense, and was modified.
 */
package xyz.templecheats.templeclient.features.module.modules.combat;

import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.manager.InventoryManager;
import xyz.templecheats.templeclient.util.player.DamageUtil;
import xyz.templecheats.templeclient.util.player.PlayerUtil;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AutoTotem extends Module {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    private final EnumSetting<DefaultItem> defaultItem = new EnumSetting<>("Default", this, DefaultItem.Totem);
    private final EnumSetting<NonDefaultItem> nonDefaultItem = new EnumSetting<>("Non Default", this, NonDefaultItem.Crystal);
    private final EnumSetting<NoPlayerItem> noPlayerItem = new EnumSetting<>("No Player", this, NoPlayerItem.Gapple);
    private final EnumSetting<PotionChoose> potionChoose = new EnumSetting<>("Potion", this, PotionChoose.First);
    private final IntSetting healthSwitch = new IntSetting("Health Switch", this, 0, 36, 14);
    private final IntSetting tickDelay = new IntSetting("Tick Delay", this, 0, 20, 20);
    private final IntSetting fallDistance = new IntSetting("Fall Distance", this, 0, 30, 12);
    private final IntSetting maxSwitchPerSecond = new IntSetting("Max Switch", this, 2, 10, 6);
    private final DoubleSetting biasDamage = new DoubleSetting("Bias Dmg", this, 0.0, 3.0, 1.0);
    private final DoubleSetting playerDistance = new DoubleSetting("Player Dist", this, 0.0, 30.0, 0.0);
    private final BooleanSetting pickObby = new BooleanSetting("Pick Obby", this, false);
    private final BooleanSetting pickObbyShift = new BooleanSetting("Pick Obby Shift", this, false);
    private final BooleanSetting crystObby = new BooleanSetting("Crystal + Obby", this, false);
    private final BooleanSetting rightGap = new BooleanSetting("R-Click Gap", this, false);
    private final BooleanSetting shiftPot = new BooleanSetting("Shift Pot", this, false);
    private final BooleanSetting swordCheck = new BooleanSetting("Sword Check", this, true);
    private final BooleanSetting swordCrystal = new BooleanSetting("Sword + Crystal", this, false);
    private final BooleanSetting pickCrystal = new BooleanSetting("Pick + Crystal", this, false);
    private final BooleanSetting fallDistanceBol = new BooleanSetting("Fall Dist", this, true);
    private final BooleanSetting crystalCheck = new BooleanSetting("Crystal Check", this, false);
    private final BooleanSetting noHotBar = new BooleanSetting("No HotBar", this, false);
    private final BooleanSetting onlyHotBar = new BooleanSetting("Only HotBar", this, false);
    private final BooleanSetting antiWeakness = new BooleanSetting("Anti Weakness", this, false);
    private final BooleanSetting hotBarTotem = new BooleanSetting("HotBar Totem", this, false);

    /****************************************************************
     *                      Variables
     ****************************************************************/
    private int prevSlot, tickWaited, totems;
    private boolean returnBack, stepChanging, firstChange;
    private static boolean activeT = false;
    private static int forceObby;
    private static int forceSkull;

    /****************************************************************
     *                      Lists
     ****************************************************************/
    private final ArrayList<Long> switchDone = new ArrayList<>();
    private final ArrayList<Item> ignoreNoSword = new ArrayList<Item>() {
        {
            add(Items.GOLDEN_APPLE);
            add(Items.EXPERIENCE_BOTTLE);
            add(Items.BOW);
            add(Items.POTIONITEM);
        }
    };

    private final Map<String, Item> allowedItemsItem = new HashMap<String, Item>() {{
        put("Totem", Items.TOTEM_OF_UNDYING);
        put("Crystal", Items.END_CRYSTAL);
        put("Gapple", Items.GOLDEN_APPLE);
        put("Pot", Items.POTIONITEM);
        put("Exp", Items.EXPERIENCE_BOTTLE);
        put("String", Items.STRING);
    }};

    private final Map<String, Block> allowedItemsBlock = new HashMap<String, Block>() {
        {
            put("Plates", Blocks.WOODEN_PRESSURE_PLATE);
            put("Skull", Blocks.SKULL);
            put("Obby", Blocks.OBSIDIAN);
        }
    };

    public AutoTotem() {
        super("AutoTotem", "Automatically places a totem in your offhand", Keyboard.KEY_NONE, Category.Combat);
        this.registerSettings(
                pickObby, pickObbyShift, crystObby, rightGap, shiftPot,
                swordCheck, swordCrystal, pickCrystal,
                fallDistanceBol, crystalCheck, noHotBar, onlyHotBar, antiWeakness, hotBarTotem,
                healthSwitch, tickDelay, fallDistance, maxSwitchPerSecond,
                biasDamage, playerDistance,
                defaultItem, nonDefaultItem, noPlayerItem, potionChoose);
    }


    @Override
    public void onEnable() {
        activeT = firstChange = true;
        forceObby = 0;
        returnBack = false;
    }

    @Override
    public void onDisable() {
        activeT = false;
        forceObby = forceSkull = 0;
    }

    public static boolean isActive() {
        return activeT;
    }

    public static void requestObsidian() {
        forceObby++;
    }
    public static void removeObsidian() {
        if (forceObby != 0) forceObby--;
    }

    @Override
    public void onUpdate() {
        if (mc.currentScreen instanceof GuiContainer) return;
        if (stepChanging) {
            if (tickWaited++ >= tickDelay.intValue()) {
                tickWaited = 0;
                stepChanging = false;
                mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
                switchDone.add(System.currentTimeMillis());
            } else return;
        }

        totems = mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();

        if (returnBack) {
            if (tickWaited++ >= tickDelay.intValue()) {
                changeBack();
            } else return;
        }

        String itemCheck = getItem();

        if (offHandSame(itemCheck)) {
            boolean done = false;
            if (hotBarTotem.booleanValue() && itemCheck.equals("Totem")) {
                done = switchItemTotemHot();
            }
            if (!done) {
                switchItemNormal(itemCheck);
            }
        }
    }

    private void changeBack() {
        if (prevSlot == -1 || !mc.player.inventory.getStackInSlot(prevSlot).isEmpty())
            prevSlot = findEmptySlot();
        if (prevSlot != -1) {
            mc.playerController.windowClick(0, prevSlot < 9 ? prevSlot + 36 : prevSlot, 0, ClickType.PICKUP, mc.player);
        } else {
            System.out.println("Your inventory is full. The item that was on your offhand is going to be dropped. Open your inventory and choose where to put it.");
        }
        returnBack = false;
        tickWaited = 0;
    }

    private boolean switchItemTotemHot() {
        int slot = InventoryManager.findTotemSlot(0, 8);
        if (slot != -1) {
            if (mc.player.inventory.currentItem != slot)
                mc.player.inventory.currentItem = slot;
            return true;
        }
        return false;
    }

    private void switchItemNormal(String itemCheck) {
        int t = getInventorySlot(itemCheck);
        if (t == -1) return;
        if (!itemCheck.equals("Totem") && canSwitch())
            return;
        toOffHand(t);
    }

    private String getItem() {
        String itemCheck = "";
        boolean normalOffHand = true;

        if ((fallDistanceBol.booleanValue() && mc.player.fallDistance >= fallDistance.intValue() && mc.player.prevPosY != mc.player.posY && !mc.player.isElytraFlying())
                || (crystalCheck.booleanValue() && crystalDamage())) {
            normalOffHand = false;
            itemCheck = "Totem";
        }
        if (forceSkull == 1) {
            itemCheck = "Skull";
            normalOffHand = false;
        }
        Item mainHandItem = mc.player.getHeldItemMainhand().getItem();
        if (forceObby > 0
                || (normalOffHand && (
                (crystObby.booleanValue() && mc.gameSettings.keyBindSneak.isKeyDown()
                        && mainHandItem == Items.END_CRYSTAL)
                        || (pickObby.booleanValue() && mainHandItem == Items.DIAMOND_PICKAXE)
                        || (pickObbyShift.booleanValue() && mc.gameSettings.keyBindSneak.isKeyDown())))) {
            itemCheck = "Obby";
            normalOffHand = false;
        }

        if (swordCrystal.booleanValue() && (mainHandItem == Items.DIAMOND_SWORD)) {
            itemCheck = "Crystal";
            normalOffHand = false;
        }
        if (pickCrystal.booleanValue() && (mainHandItem == Items.DIAMOND_PICKAXE)) {
            itemCheck = "Crystal";
            normalOffHand = false;
        }

        if (normalOffHand && mc.gameSettings.keyBindUseItem.isKeyDown() && (!swordCheck.booleanValue() || mainHandItem == Items.DIAMOND_SWORD)) {
            if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                if (shiftPot.booleanValue()) {
                    itemCheck = "Pot";
                    normalOffHand = false;
                }
            } else if (rightGap.booleanValue() && !ignoreNoSword.contains(mainHandItem)) {
                itemCheck = "Gapple";
                normalOffHand = false;
            }
        }

        if (normalOffHand && antiWeakness.booleanValue() && mc.player.isPotionActive(MobEffects.WEAKNESS)) {
            normalOffHand = false;
            itemCheck = "Crystal";
        }

        if (normalOffHand && !nearPlayer()) {
            normalOffHand = false;
            itemCheck = noPlayerItem.value().toString();
        }

        itemCheck = getItemToCheck(itemCheck);
        return itemCheck;
    }

    private boolean canSwitch() {
        long now = System.currentTimeMillis();
        switchDone.removeIf(switchTime -> now - switchTime > 1000);
        if (switchDone.size() / 2 >= maxSwitchPerSecond.intValue()) {
            return true;
        }
        switchDone.add(now);
        return false;
    }

    private boolean nearPlayer() {
        if (playerDistance.doubleValue() == 0)
            return true;
        for (EntityPlayer pl : mc.world.playerEntities) {
            if (pl != mc.player && mc.player.getDistance(pl) < playerDistance.doubleValue())
                return true;
        }
        return false;
    }

    private boolean crystalDamage() {
        for (Entity t : mc.world.loadedEntityList) {
            if (t instanceof EntityEnderCrystal && mc.player.getDistance(t) <= 12) {
                if (DamageUtil.calculateDamage(t.posX, t.posY, t.posZ, mc.player) * biasDamage.doubleValue() >= mc.player.getHealth()) {
                    return true;
                }
            }
        }
        return false;
    }

    private int findEmptySlot() {
        for (int i = 35; i > -1; i--) {
            if (mc.player.inventory.getStackInSlot(i).isEmpty())
                return i;
        }
        return -1;
    }

    private boolean offHandSame(String itemCheck) {
        Item offHandItem = mc.player.getHeldItemOffhand().getItem();
        if (allowedItemsBlock.containsKey(itemCheck)) {
            Block item = allowedItemsBlock.get(itemCheck);
            if (offHandItem instanceof ItemBlock)
                return ((ItemBlock) offHandItem).getBlock() != item;
            else if (offHandItem instanceof ItemSkull && item == Blocks.SKULL)
                return true;
        } else {
            Item item = allowedItemsItem.get(itemCheck);
            return item != offHandItem;
        }
        return true;
    }

    private String getItemToCheck(String str) {
        return (PlayerUtil.getHealth() > healthSwitch.intValue())
                ? (str.equals("") ? nonDefaultItem.value().toString() : str)
                : defaultItem.value().toString();
    }

    private int getInventorySlot(String itemName) {
        Object item;
        boolean blockBool = false;
        if (allowedItemsItem.containsKey(itemName)) {
            item = allowedItemsItem.get(itemName);
        } else {
            item = allowedItemsBlock.get(itemName);
            blockBool = true;
        }
        int res;
        if (!firstChange) {
            if (prevSlot != -1) {
                res = isCorrect(prevSlot, blockBool, item, itemName);
                if (res != -1)
                    return res;
            }
        }
        for (int i = (onlyHotBar.booleanValue() ? 8 : 35); i > (noHotBar.booleanValue() ? 9 : -1); i--) {
            res = isCorrect(i, blockBool, item, itemName);
            if (res != -1)
                return res;
        }
        return -1;
    }

    private int isCorrect(int i, boolean blockBool, Object item, String itemName) {
        ItemStack stack = mc.player.inventory.getStackInSlot(i);
        Item temp = stack.getItem();

        if (blockBool) {
            if (temp instanceof ItemBlock) {
                if (((ItemBlock) temp).getBlock() == item)
                    return i;
            } else if (temp instanceof ItemSkull && item == Blocks.SKULL) {
                return i;
            }
        } else {
            if (item == temp) {
                if (itemName.equals("Pot")) {
                    NBTTagCompound tagCompound = stack.getTagCompound();
                    if (tagCompound != null) {
                        String potionType = tagCompound.getString("Potion");
                        if (!(potionChoose.value().toString().equalsIgnoreCase("first") || potionType.contains(potionChoose.value().toString()))) {
                            return -1;
                        }
                    }
                }
                return i;
            }
        }
        return -1;
    }

    private void toOffHand(int t) {
        if (!mc.player.getHeldItemOffhand().isEmpty()) {
            if (firstChange)
                prevSlot = t;
            returnBack = true;
            firstChange = !firstChange;
        } else prevSlot = -1;

        mc.playerController.windowClick(0, t < 9 ? t + 36 : t, 0, ClickType.PICKUP, mc.player);
        stepChanging = true;
        tickWaited = 0;
    }

    @Override
    public String getHudInfo() {
        return String.valueOf(totems);
    }

    public enum DefaultItem {
        Totem,
        Crystal,
        Gapple,
        Plates,
        Obby,
        Pot,
        Exp
    }

    public enum NonDefaultItem {
        Totem,
        Crystal,
        Gapple,
        Obby,
        Pot,
        Exp,
        Plates,
        String,
        Skull
    }

    public enum NoPlayerItem {
        Totem,
        Crystal,
        Gapple,
        Plates,
        Obby,
        Pot,
        Exp
    }

    public enum PotionChoose {
        First,
        Strength,
        Swiftness
    }

}