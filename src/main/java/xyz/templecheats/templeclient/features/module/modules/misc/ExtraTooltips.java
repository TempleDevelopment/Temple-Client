package xyz.templecheats.templeclient.features.module.modules.misc;

import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;

public class ExtraTooltips extends Module {
    /*
     * Settings
     */
    public final BooleanSetting shulkerPreview = new BooleanSetting("Shulker Preview", this, true);

    public ExtraTooltips() {
        super("ExtraTooltips", "Adds better item tooltips to the game", Keyboard.KEY_NONE, Category.Misc);
        registerSettings(shulkerPreview);
    }

}