package xyz.templecheats.templeclient.features.module.modules.client.hud;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import xyz.templecheats.templeclient.features.gui.font.FontUtils;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.features.module.modules.client.HUD;

import java.util.ArrayList;
import java.util.List;

public class PotionHUD extends HUD.HudElement {

    public static PotionHUD INSTANCE;
    public PotionHUD() {
        super("PotionHUD", "Shows active potion effects in the HUD");
        INSTANCE = this;
    }

    @Override
    protected void renderElement(ScaledResolution sr) {
        List<PotionEffect> effects = new ArrayList<>(Minecraft.getMinecraft().player.getActivePotionEffects());

        effects.sort((PotionEffect effect1, PotionEffect effect2) -> {
            String combinedText1 = getEffectText(effect1);
            String combinedText2 = getEffectText(effect2);

            return Double.compare(FontUtils.getStringWidth(this.isTopOfCenter() ? combinedText2 : combinedText1), FontUtils.getStringWidth(this.isTopOfCenter() ? combinedText1 : combinedText2));
        });

        double y = 0;
        double maxWidth = 0;
        double totalHeight = 0;

        for (PotionEffect effect : effects) {
            String combinedText = getEffectText(effect);

            double width = FontUtils.getStringWidth(combinedText) + 10;
            double height = FontUtils.getFontHeight() + 2;
            maxWidth = Math.max(maxWidth, width);
            totalHeight += height;

            FontUtils.drawString(combinedText, this.getX() + (!this.isLeftOfCenter() ? this.getWidth() - FontUtils.getStringWidth(combinedText) : 0), this.getY() + y, ClickGUI.INSTANCE.getStartColor(), true);

            y += height;
        }

        this.setWidth(!effects.isEmpty() ? FontUtils.getStringWidth(getEffectText(this.isTopOfCenter() ? effects.get(0) : effects.get(effects.size() - 1))) : 0);
        this.setHeight(totalHeight);
    }

    private String getEffectText(PotionEffect effect) {
        String effectName = I18n.format(effect.getEffectName());
        String effectDuration = Potion.getPotionDurationString(effect, 1.0F);
        return effectName + " " + ChatFormatting.WHITE + effectDuration;
    }
}