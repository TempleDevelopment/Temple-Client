package xyz.templecheats.templeclient.features.module.modules.client.hud;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import xyz.templecheats.templeclient.features.module.modules.client.HUD;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;

public class Armor extends HUD.HudElement {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    private final EnumSetting<DisplayMode> displayMode = new EnumSetting<>("Display", this, DisplayMode.Horizontal);

    public Armor() {
        super("Armor", "Shows your Armor in the HUD");
        registerSettings(displayMode);
    }

    @Override
    public void renderElement(ScaledResolution sr) {
        if (displayMode.value() == DisplayMode.Vertical) {
            this.setWidth(16);
            this.setHeight(84);
        } else {
            this.setWidth(84);
            this.setHeight(16);
        }

        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();

        int startX = (int) this.getX();
        int startY = (int) this.getY();

        for (ItemStack stack : Lists.reverse(mc.player.inventory.armorInventory)) {
            if (!stack.isEmpty()) {
                mc.getRenderItem().renderItemAndEffectIntoGUI(stack, startX, startY);
                mc.getRenderItem().renderItemOverlays(mc.fontRenderer, stack, startX, startY);
            }

            if (displayMode.value() == DisplayMode.Vertical) {
                startY += 21;
            } else {
                startX += 21;
            }
        }

        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    private enum DisplayMode {
        Vertical,
        Horizontal
    }
}