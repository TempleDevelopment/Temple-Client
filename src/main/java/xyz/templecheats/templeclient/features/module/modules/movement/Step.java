package xyz.templecheats.templeclient.features.module.modules.movement;

import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;

public class Step extends Module {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    public final IntSetting height = new IntSetting("Height", this, 0, 2, 1);

    public Step() {
        super("Step", "Quickly step on blocks", Keyboard.KEY_NONE, Module.Category.Movement);
        registerSettings(height);
    }

    @Override
    public void onDisable() {
        mc.player.stepHeight = 0.6f;
    }

    @Override
    public void onUpdate() {
        mc.player.stepHeight = height.intValue();
    }
}
