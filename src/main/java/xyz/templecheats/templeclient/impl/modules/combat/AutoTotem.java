package xyz.templecheats.templeclient.impl.modules.combat;

import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.impl.modules.Module;
import xyz.templecheats.templeclient.impl.gui.clickgui.setting.Setting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.ClickType;
import net.minecraft.init.Items;
import xyz.templecheats.templeclient.TempleClient;

public class AutoTotem extends Module {

    int delay = 0;
    int totems;
    int totemsOffHand;
    int totemSwitchDelay = 0;
    Setting healthThreshold;
    ItemStack originalOffhandItem = ItemStack.EMPTY;

    public AutoTotem() {
        super("AutoTotem", Keyboard.KEY_NONE, Category.COMBAT);
        healthThreshold = new Setting("Health", this, 10, 1, 20, true);
        TempleClient.settingsManager.rSetting(healthThreshold);
    }

    @Override
    public void onUpdate() {
        if (mc.world == null) return;
        totems = mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
        totemsOffHand = mc.player.inventory.offHandInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
        int healthToActivate = TempleClient.settingsManager.getSettingByName(this.getName(), "Health").getValInt();

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
        for (int i = 0; i < 45; i++) {
            if (mc.currentScreen == null && totems + totemsOffHand > 0) {
                ItemStack stack = mc.player.openContainer.getSlot(i).getStack();
                if (stack == ItemStack.EMPTY) continue;
                if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
                    totemSwitchDelay++;
                    if (totemSwitchDelay >= delay) {
                        mc.playerController.windowClick(0, i, 1, ClickType.PICKUP, mc.player);
                        mc.playerController.windowClick(0, 45, 1, ClickType.PICKUP, mc.player);
                        mc.playerController.windowClick(0, i, 1, ClickType.PICKUP, mc.player);
                        totemSwitchDelay = 0;
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
    }
}
