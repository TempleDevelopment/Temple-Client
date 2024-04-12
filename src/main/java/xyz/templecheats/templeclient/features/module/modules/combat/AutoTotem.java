package xyz.templecheats.templeclient.features.module.modules.combat;

import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.manager.ModuleManager;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;

public class AutoTotem extends Module {
    /*
     * Settings
     */
    private final IntSetting healthThreshold = new IntSetting("Health", this, 1, 20, 10);

    /*
     * Variables
     */
    private int delay = 0;
    private int totems;
    private int totemsOffHand;
    private int totemSwitchDelay = 0;
    private ItemStack originalOffhandItem = ItemStack.EMPTY;

    public AutoTotem() {
        super("AutoTotem", "Automatically places a totem in your offhand", Keyboard.KEY_NONE, Category.Combat);

        registerSettings(healthThreshold);
    }

    @Override
    public void onUpdate() {
        if (mc.world == null) return;
        totems = mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
        totemsOffHand = mc.player.inventory.offHandInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
        int healthToActivate = healthThreshold.intValue();

        if (mc.player.getHealth() <= healthToActivate && (mc.player.getHeldItemOffhand().isEmpty() || !mc.player.getHeldItemOffhand().getItem().equals(Items.TOTEM_OF_UNDYING))) {
            if (originalOffhandItem.isEmpty()) {
                originalOffhandItem = mc.player.getHeldItemOffhand();
            }
            switchToTotem();
        } else if (mc.player.getHealth() > healthToActivate && !originalOffhandItem.isEmpty()) {
            switchBackToOriginal();
        }
    }

    private void switchToTotem() {
        totemSwitchDelay = 0;

        for (int i = 0; i < 45; i++) {
            if (totems + totemsOffHand > 0) {
                ItemStack stack = mc.player.openContainer.getSlot(i).getStack();
                if (stack == ItemStack.EMPTY) continue;
                if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
                    totemSwitchDelay++;
                    if (totemSwitchDelay >= delay) {
                        mc.playerController.windowClick(0, i, 1, ClickType.PICKUP, mc.player);
                        mc.playerController.windowClick(0, 45, 1, ClickType.PICKUP, mc.player);
                        mc.playerController.windowClick(0, i, 1, ClickType.PICKUP, mc.player);
                        break;
                    }
                }
            }
        }
    }

    private void switchBackToOriginal() {
        if (!mc.player.getHeldItemOffhand().isEmpty()) {
            mc.playerController.windowClick(0, 45, 1, ClickType.PICKUP, mc.player);
        }
        if (!originalOffhandItem.isEmpty()) {
            mc.playerController.windowClick(0, 45, 1, ClickType.PICKUP, mc.player);
            originalOffhandItem = ItemStack.EMPTY;
        }
    }

    @Override
    public void onEnable() {
        totemSwitchDelay = 0;
        originalOffhandItem = ItemStack.EMPTY;
        super.onEnable();
        Offhand offhand = (Offhand) ModuleManager.getModuleByName("Offhand");
        if (offhand.isEnabled()) {
            offhand.disable();
        }
    }
}