package xyz.templecheats.templeclient.features.modules.client.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.modules.Module;

public class InventoryHUD extends Module {
    public static final InventoryHUD INSTANCE = new InventoryHUD();

    public InventoryHUD() {
        super("InventoryHUD", Keyboard.KEY_NONE, Category.CLIENT);
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            renderInventory();
        }
    }

    private void renderInventory() {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution scaledRes = new ScaledResolution(mc);

        int startX = scaledRes.getScaledWidth() - 170;
        int startY = scaledRes.getScaledHeight() - 55;

        RenderHelper.enableGUIStandardItemLighting();

        for (int i = 9; i < 36; i++) {
            int row = (i - 9) / 9;
            int x = startX + (i % 9) * 18;
            int y = startY + row * 18;

            ItemStack itemStack = mc.player.inventory.mainInventory.get(i);
            if (!itemStack.isEmpty()) {
                mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, x, y);
                mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, itemStack, x, y, null);
            }
        }

        RenderHelper.disableStandardItemLighting();
    }
}