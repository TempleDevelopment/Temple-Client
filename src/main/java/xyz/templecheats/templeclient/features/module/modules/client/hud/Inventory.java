package xyz.templecheats.templeclient.features.module.modules.client.hud;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import xyz.templecheats.templeclient.features.module.modules.client.HUD;
import xyz.templecheats.templeclient.util.render.shader.impl.RectBuilder;
import xyz.templecheats.templeclient.util.math.Vec2d;

import java.awt.*;

import static xyz.templecheats.templeclient.features.gui.font.Fonts.*;

public class Inventory extends HUD.HudElement {
    public Inventory() {
        super("Inventory", "Shows your inventory in the HUD");
        registerSettings(fill, outline, blur, color, outlineColor, outlineWidth, blurRadius);
    }

    @Override
    public void renderElement(ScaledResolution sr) {
        this.setWidth(162);
        this.setHeight(56);

        int startX = (int) this.getX();
        int startY = (int) this.getY();

        double iconOffset = (!this.isLeftOfCenter() ? this.getWidth() - (font18.getStringWidth("Inventory") + 6): -2);
        double textOffset = (!this.isLeftOfCenter() ? getWidth() - font18.getStringWidth("Inventory") - 3 : 0);

        //Nice border
        if (outline.booleanValue()) {
            new RectBuilder(new Vec2d(startX + iconOffset - (outlineWidth.doubleValue()), startY - (12 + outlineWidth.doubleValue())), new Vec2d(startX + iconOffset + font18.getStringWidth("Inventory") + 8 + outlineWidth.doubleValue(), startY + 4 + outlineWidth.doubleValue()))
                    .color(outlineColor.getColor()).radius(2.0).draw();
            new RectBuilder(new Vec2d(startX - (2 + outlineWidth.doubleValue()), startY - (2 + outlineWidth.doubleValue())), new Vec2d(startX + getWidth() + 2 + outlineWidth.doubleValue(), startY + getHeight() + 2 + outlineWidth.doubleValue()))
                    .color(outlineColor.getColor()).radius(4.0).draw();
        }
        //Base
        if (fill.booleanValue()) {
            new RectBuilder(new Vec2d(startX + iconOffset, startY - 12), new Vec2d(startX + iconOffset + font18.getStringWidth("Inventory") + 8, startY + 4))
                    .color(color.getColor()).radius(2.0)
                    .blur(blur.booleanValue() ? blurRadius.doubleValue() : 0)
                    .drawBlur()
                    .draw();
            new RectBuilder(new Vec2d(startX - 2, startY - 2), new Vec2d(startX + getWidth() + 2, startY + getHeight() + 2))
                    .color(color.getColor()).radius(4.0)
                    .blur(blur.booleanValue() ? blurRadius.doubleValue() : 0)
                    .drawBlur()
                    .draw();
        }
        font18.drawString("Inventory", (float) (getX() + textOffset), (float) getY() - (font18.getFontHeight() + 2), Color.WHITE, true);

        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        RenderHelper.enableGUIStandardItemLighting();

        for (int i = 9; i < 36; i++) {
            final ItemStack itemStack = mc.player.inventory.mainInventory.get(i);
            if (!itemStack.isEmpty()) {
                final int x = startX + (i % 9) * 18;
                final int y = (startY + 2) + ((i - 9) / 9) * 18;

                mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, x, y);
                mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, itemStack, x, y, null);
            }
        }
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }
}