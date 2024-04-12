package xyz.templecheats.templeclient.features.module.modules.client.hud;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.features.module.modules.client.HUD;

public class Durability extends HUD.HudElement {
    public Durability() {
        super("Durability", "Shows the durability of the item you're holding in the HUD");
    }

    @Override
    public void renderElement(ScaledResolution sr) {
        ItemStack heldItem = mc.player.getHeldItemMainhand();
        if (!heldItem.isEmpty() && heldItem.isItemStackDamageable()) {
            String itemName = heldItem.getDisplayName();
            int durability = heldItem.getMaxDamage() - heldItem.getItemDamage();

            String durabilityText = itemName + durability;

            this.setWidth(font.getStringWidth(durabilityText));
            this.setHeight(font.getFontHeight());

            font.drawString(itemName, this.getX(), this.getY(), ClickGUI.INSTANCE.getStartColor().getRGB(), true, 1.0f);
            font.drawString(String.valueOf(durability), this.getX() + font.getStringWidth(itemName + " "), this.getY(), 0xFFFFFF, true, 1.0f);
        }
    }
}