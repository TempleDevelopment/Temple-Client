package xyz.templecheats.templeclient.features.module.modules.render.xray.sub;

import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;

public class Ores extends Module {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    private final BooleanSetting renderDiamondOres = new BooleanSetting("Diamond Ores", this, true);
    private final BooleanSetting renderIronOres = new BooleanSetting("Iron Ores", this, true);
    private final BooleanSetting renderGoldOres = new BooleanSetting("Gold Ores", this, true);
    private final BooleanSetting renderLapisOres = new BooleanSetting("Lapis Ores", this, true);
    private final BooleanSetting renderRedstoneOres = new BooleanSetting("Redstone Ores", this, true);
    private final BooleanSetting renderEmeraldOres = new BooleanSetting("Emerald Ores", this, true);
    private final BooleanSetting renderCoalOres = new BooleanSetting("Coal Ores", this, true);
    private final BooleanSetting renderQuartzOres = new BooleanSetting("Quartz Ores", this, true);

    public Ores() {
        super("Ores", "Allows you to filter rendering of ores", Keyboard.KEY_NONE, Category.Render, true);
        registerSettings(renderDiamondOres, renderIronOres, renderGoldOres, renderLapisOres, renderRedstoneOres, renderEmeraldOres, renderCoalOres, renderQuartzOres);
    }

    public boolean shouldRenderDiamondOres() {
        return renderDiamondOres.booleanValue();
    }

    public boolean shouldRenderIronOres() {
        return renderIronOres.booleanValue();
    }

    public boolean shouldRenderGoldOres() {
        return renderGoldOres.booleanValue();
    }

    public boolean shouldRenderLapisOres() {
        return renderLapisOres.booleanValue();
    }

    public boolean shouldRenderRedstoneOres() {
        return renderRedstoneOres.booleanValue();
    }

    public boolean shouldRenderEmeraldOres() {
        return renderEmeraldOres.booleanValue();
    }

    public boolean shouldRenderCoalOres() {
        return renderCoalOres.booleanValue();
    }

    public boolean shouldRenderQuartzOres() {
        return renderQuartzOres.booleanValue();
    }
}