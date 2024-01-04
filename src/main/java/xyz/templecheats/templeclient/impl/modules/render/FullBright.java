package xyz.templecheats.templeclient.impl.modules.render;

import xyz.templecheats.templeclient.impl.modules.Module;
import org.lwjgl.input.Keyboard;

public class FullBright extends Module {
    private float originalGamma;

    public FullBright() {
        super("FullBright", Keyboard.KEY_G, Category.RENDER);
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
