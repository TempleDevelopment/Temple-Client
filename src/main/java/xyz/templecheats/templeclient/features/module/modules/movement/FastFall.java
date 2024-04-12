package xyz.templecheats.templeclient.features.module.modules.movement;

import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;

public class FastFall extends Module {
    public final IntSetting fallSpeed = new IntSetting("Fall Speed", this, 1, 50, 2);

    public FastFall() {
        super("FastFall", "allows you to go down blocks fast", Keyboard.KEY_NONE, Category.Movement);
        registerSettings(fallSpeed);
    }
    @Override
    public void onUpdate() {
        if (mc.player.isElytraFlying() || mc.player.isOnLadder() || mc.player.capabilities.isFlying || mc.player.motionY > 0.0 || mc.gameSettings.keyBindJump.isKeyDown()) {
            return;
        }
        if (mc.player.onGround) {
            mc.player.motionY = -fallSpeed.intValue();
        }
    }
}
