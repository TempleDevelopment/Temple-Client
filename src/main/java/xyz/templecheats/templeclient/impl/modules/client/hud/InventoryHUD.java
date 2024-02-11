package xyz.templecheats.templeclient.impl.modules.client.hud;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import xyz.templecheats.templeclient.impl.modules.client.HUD;

public class InventoryHUD extends HUD.HudElement {
    public InventoryHUD() {
        super("InventoryHUD", "Shows your inventory in the HUD");
    }

    @Override
    protected void renderElement(ScaledResolution sr) {
        this.setWidth(162);
        this.setHeight(54);

        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();

        int startX = (int) this.getX();
        int startY = (int) this.getY();

        for(int i = 9; i < 36; i++) {
            final ItemStack itemStack = mc.player.inventory.mainInventory.get(i);
            if(!itemStack.isEmpty()) {
                final int x = startX + (i % 9) * 18;
                final int y = startY + ((i - 9) / 9) * 18;

                mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, x, y);
                mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, itemStack, x, y, null);
            }
        }

        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }
}