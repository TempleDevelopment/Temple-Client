package xyz.templecheats.templeclient.impl.modules.client.hud;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.api.setting.Setting;
import xyz.templecheats.templeclient.impl.modules.client.HUD;

import java.util.ArrayList;

public class ArmorHUD extends HUD.HudElement {
    private Setting displayMode;

    public ArmorHUD() {
        super("ArmorHUD", "Shows your Armor in the HUD");

        ArrayList<String> options = new ArrayList<>();
        options.add("Vertical");
        options.add("Horizontal");

        TempleClient.settingsManager.rSetting(displayMode = new Setting("Display", this, options, "Horizontal"));
    }

    @Override
    protected void renderElement(ScaledResolution sr) {
        if(displayMode.getValString().equals("Vertical")) {
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

        for(ItemStack stack : Lists.reverse(mc.player.inventory.armorInventory)) {
            if(!stack.isEmpty()) {
                mc.getRenderItem().renderItemAndEffectIntoGUI(stack, startX, startY);
                mc.getRenderItem().renderItemOverlays(mc.fontRenderer, stack, startX, startY);
            }

            if(displayMode.getValString().equals("Vertical")) {
                startY += 21;
            } else {
                startX += 21;
            }
        }

        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }
}
