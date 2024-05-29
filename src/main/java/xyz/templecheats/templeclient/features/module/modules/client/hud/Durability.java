package xyz.templecheats.templeclient.features.module.modules.client.hud;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;
import xyz.templecheats.templeclient.features.gui.font.TempleIcon;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.features.module.modules.client.HUD;
import xyz.templecheats.templeclient.util.math.Vec2d;
import xyz.templecheats.templeclient.util.render.shader.impl.RectBuilder;

import java.awt.*;

import static xyz.templecheats.templeclient.features.gui.font.Fonts.font18;
import static xyz.templecheats.templeclient.features.gui.font.Fonts.icon26;
import static xyz.templecheats.templeclient.util.color.ColorUtil.lerpColor;

public class Durability extends HUD.HudElement {
    public Durability() {
        super("Durability", "Shows the durability of the item you're holding in the HUD");
        registerSettings(fill, outline, blur, color, outlineColor, outlineWidth, blurRadius);
    }

    @Override
    public void renderElement(ScaledResolution sr) {
        ItemStack heldItem = mc.player.getHeldItemMainhand();
        if (!heldItem.isEmpty() && heldItem.isItemStackDamageable()) {
            String itemName = heldItem.getDisplayName();
            int durability = heldItem.getMaxDamage() - heldItem.getItemDamage();

            String durabilityText = itemName + durability;

            new RectBuilder(new Vec2d(getX(), getY()), new Vec2d(getX() + getWidth(), getY() + getHeight()))
                    .outlineColor(outlineColor.getColor())
                    .width(outline.booleanValue() ? outlineWidth.doubleValue() : 0)
                    .color(fill.booleanValue() ? color.getColor() : new Color(0, 0, 0, 0))
                    .radius(2.5)
                    .blur(blur.booleanValue() ? blurRadius.doubleValue() : 0)
                    .drawBlur()
                    .draw();

            double iconOffset = (!this.isLeftOfCenter() ? this.getWidth() - icon26.getStringWidth(TempleIcon.DISCONNECT.getIcon()) : -1);
            double textOffset = (!this.isLeftOfCenter() ? 1 : 13);

            if (HUD.INSTANCE.icon.booleanValue()) {
                icon26.drawIcon(TempleIcon.DISCONNECT.getIcon(), (float) (this.getX() + iconOffset), (float) (this.getY() + 5), lerpColor(Color.RED, Color.GREEN, durability), false);
            } else {
                textOffset = getWidth() / 2 - font18.getStringWidth(itemName + durability) / 2;
            }

            int textColor = HUD.INSTANCE.sync.booleanValue() ? ClickGUI.INSTANCE.getClientColor((int) getY()) : Color.WHITE.getRGB();

            font18.drawString(itemName, this.getX() + textOffset, this.getY() + 5, textColor, true);
            font18.drawString(String.valueOf(durability), this.getX() + textOffset + font18.getStringWidth(itemName + " "), this.getY() + 5, 0xFFFFFF, true);

            this.setWidth(font18.getStringWidth(durabilityText) + 20);
            this.setHeight(font18.getFontHeight() + 8);
        }
    }
}