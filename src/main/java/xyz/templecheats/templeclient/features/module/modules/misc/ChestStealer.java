package xyz.templecheats.templeclient.features.module.modules.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;

public class ChestStealer extends Module {
    private int counter = 0;
    private static final int THRESHOLD = 10;
    private static final long DELAY = 1000;
    public ChestStealer() {
        super("ChestStealer", "Steals everything from any container", Keyboard.KEY_NONE, Category.Miscelleaneous);
    }

    @Override
    public void onUpdate() {
        if (mc.player.openContainer == null) {
            return;
        }

        Container container = mc.player.openContainer;
        for (int i = 0; i < container.inventorySlots.size(); i++) {
            Slot slot = container.getSlot(i);

            if (slot.inventory == mc.player.inventory) {
                continue;
            }

            if (slot.getHasStack()) {
                int finalI = i;
                new Thread(() -> {
                    try {
                        Thread.sleep(DELAY);
                        Minecraft.getMinecraft().playerController.windowClick(container.windowId, finalI, 0, ClickType.QUICK_MOVE, mc.player);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }
    }
}