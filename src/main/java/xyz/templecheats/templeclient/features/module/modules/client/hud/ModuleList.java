package xyz.templecheats.templeclient.features.module.modules.client.hud;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.gui.ScaledResolution;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.features.module.modules.client.HUD;
import xyz.templecheats.templeclient.manager.ModuleManager;
import xyz.templecheats.templeclient.util.color.impl.RectBuilder;
import xyz.templecheats.templeclient.util.math.Vec2d;
import xyz.templecheats.templeclient.util.render.Easing;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.ColorSetting;

import java.awt.*;
import java.util.ArrayList;

import static xyz.templecheats.templeclient.features.gui.font.Fonts.font18;
import static xyz.templecheats.templeclient.util.color.ColorUtil.setAlpha;

public class ModuleList extends HUD.HudElement {
    /**
     * Settings
     */
    private final ColorSetting color = new ColorSetting("Color", this, new Color(25, 25, 25, 255));
    private final BooleanSetting sideLine = new BooleanSetting("Side Line", this, false);
    private final BooleanSetting metadata = new BooleanSetting("Metadata", this, true);
    private float progress = 0.0f;

    public ModuleList() {
        super("ModuleList", "Shows active module in the HUD");
        this.registerSettings(color, sideLine, metadata);

        this.setEnabled(true);
        this.setX(2);
        this.setY(font18.getFontHeight() + 4);
    }

    @Override
    public void renderElement(ScaledResolution sr) {
        final ArrayList<Module> mods = ModuleManager.getActiveModules();
        mods.removeIf(mod -> mod.getCategory() == Module.Category.Client || mod.submodule);

        mods.sort((Module mod1, Module mod2) -> {
            final String text1 = this.getModText(mod1);
            final String text2 = this.getModText(mod2);
            return Double.compare(font18.getStringWidth(this.isTopOfCenter() ? text2 : text1), font18.getStringWidth(this.isTopOfCenter() ? text1 : text2));
        });

        double y = 0;
        float p = getSmoothedProgress();

        for (Module mod: mods) {
            Color fontColor = setAlpha(new Color(ClickGUI.INSTANCE.getClientColor((int) y)) , (int) (p * 255));

            final String text = this.getModText(mod);
            double textOffsetX = (!this.isLeftOfCenter() ? this.getWidth() - font18.getStringWidth(text) - 4 : 4);
            double rectOffsetX = (!this.isLeftOfCenter() ? this.getWidth() - 2 : 0);

            double offsetY = this.getY() + y + font18.getFontHeight() - 0.5;
            double rectY = offsetY - (font18.getFontHeight() + 1);
            double rectWidth = (!this.isLeftOfCenter() ? -font18.getStringWidth(text) - 4 : font18.getStringWidth(text) + 4);


            new RectBuilder(new Vec2d(this.getX() + rectOffsetX , rectY) , new Vec2d(this.getX() + 2 + rectOffsetX + rectWidth , offsetY))
                    .color(color.getColor()).radius(1.0).draw();
            if (sideLine.booleanValue()) {
                new RectBuilder(new Vec2d(this.getX() + rectOffsetX , rectY) , new Vec2d(this.getX() + 2 + rectOffsetX , offsetY))
                        .color(fontColor).radius(1.0).draw();
            }
            font18.drawString(text , this.getX() + textOffsetX , this.getY() + y , fontColor.getRGB() , true);

            y += font18.getFontHeight() + 2D;
        }

        this.setWidth(!mods.isEmpty() ? font18.getStringWidth(this.getModText(this.isTopOfCenter() ? mods.get(0) : mods.get(mods.size() - 1))) + 4 : -8); // I forgor how to negative width :skull:
        this.setHeight(y);
        this.update();
    }

    private String getModText(Module module) {
        if (module.getHudInfo().isEmpty() || !this.metadata.booleanValue()) {
            return module.getName();
        }

        return String.format("%s %s[%s%s%s]", module.getName(), ChatFormatting.DARK_GRAY, ChatFormatting.GRAY, module.getHudInfo(), ChatFormatting.DARK_GRAY);
    }

    private float getSmoothedProgress() {
        return (float) Easing.InOutCubic.inc(progress);
    }

    void update() {
        if (progress < 0f) {
            return;
        }

        float deltaProgress = 4f * mc.getRenderPartialTicks();
        if (!isEnabled()) {
            deltaProgress = -deltaProgress;
        }

        progress = Math.min(1.0f, Math.max(-1.0f, progress + deltaProgress));
    }
}