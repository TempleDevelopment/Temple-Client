package com.example.examplemod.Module.RENDER;

import com.example.examplemod.Module.Module;
import org.lwjgl.input.Keyboard;

public class FullBright extends Module {
    private float originalGamma;

    public FullBright() {
        super("FullBright[B]", Keyboard.KEY_B, Category.RENDER);
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
