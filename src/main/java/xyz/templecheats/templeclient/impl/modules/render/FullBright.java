package xyz.templecheats.templeclient.impl.modules.render;

import xyz.templecheats.templeclient.impl.modules.Module;
import org.lwjgl.input.Keyboard;

public class Fullbright extends Module {
    private float originalGamma = 1.0f;

    public Fullbright() {
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