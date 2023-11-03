package xyz.templecheats.templeclient.Module.RENDER;

import xyz.templecheats.templeclient.Module.Module;
import org.lwjgl.input.Keyboard;

public class FullBright extends Module {
    private float originalGamma;

    public FullBright() {
        super("FullBright[G]", Keyboard.KEY_G, Category.RENDER);
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
