package xyz.templecheats.templeclient.features.module.modules.render;

import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;

public class FullBright extends Module {
    private float originalGamma = 1.0f;

    public FullBright() {
        super("Fullbright", "Makes your world brighter <3", Keyboard.KEY_NONE, Category.Render);
    }

    @Override
    public void onEnable() {
        originalGamma = mc.gameSettings.gammaSetting;
        mc.gameSettings.gammaSetting = 12.0f;
    }

    @Override
    public void onDisable() {
        mc.gameSettings.gammaSetting = originalGamma;
    }
}