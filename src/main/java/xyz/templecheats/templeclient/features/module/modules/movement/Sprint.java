package xyz.templecheats.templeclient.features.module.modules.movement;

import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;

public class Sprint extends Module {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    private final BooleanSetting rage = new BooleanSetting("Rage", this, false);

    public Sprint() {
        super("Sprint", "Automatically sprint", Keyboard.KEY_NONE, Category.Movement);
        registerSettings(rage);
    }

    @Override
    public void onUpdate() {
        if (rage.booleanValue())
            if (mc.player.movementInput.moveForward != 0.0f || mc.player.movementInput.moveStrafe != 0.0f)
                mc.player.setSprinting(true);
            else if (mc.player.moveForward > 0 && !mc.player.collidedHorizontally)
                mc.player.setSprinting(true);
    }
}