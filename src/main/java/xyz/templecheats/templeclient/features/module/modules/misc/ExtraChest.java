package xyz.templecheats.templeclient.features.module.modules.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;

public class ExtraChest extends Module {
    /*
     * Settings
     */
    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", this, Mode.Steal);
    private final IntSetting dropDelay = new IntSetting("Drop Delay", this, 1, 10, 1);
    private final IntSetting stealDelay = new IntSetting("Steal Delay", this, 1, 10, 1);

    public ExtraChest() {
        super("ExtraChest", "Automatically steal/fill items from containers", Keyboard.KEY_NONE, Category.Misc);
        registerSettings(dropDelay, stealDelay, mode);
    }

    @Override
    public void onUpdate() {
        if (mc.player.openContainer == null) {
            return;
        }

        Container container = mc.player.openContainer;
        switch (mode.value()) {
            case Steal:
            case Drop:
                for (int i = 0; i < container.inventorySlots.size(); i++) {
                    Slot slot = container.getSlot(i);

                    if (slot.inventory == mc.player.inventory) {
                        continue;
                    }

                    if (slot.getHasStack()) {
                        performAction(container, i, mode.value());
                    }
                }
                break;
        }
    }

    private void performAction(Container container, int slotId, Mode mode) {
        new Thread(() -> {
            try {
                Thread.sleep(getDelay());
                switch (mode) {
                    case Steal:
                        Minecraft.getMinecraft().playerController.windowClick(container.windowId, slotId, 0, ClickType.QUICK_MOVE, mc.player);
                        break;
                    case Drop:
                        Minecraft.getMinecraft().playerController.windowClick(container.windowId, slotId, 1, ClickType.THROW, mc.player);
                        break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private long getDelay() {
        switch (mode.value()) {
            case Steal:
                return stealDelay.intValue() * 100;
            case Drop:
                return dropDelay.intValue() * 100;
            default:
                return 1;
        }
    }

    private enum Mode {
        Steal,
        Drop
    }
}