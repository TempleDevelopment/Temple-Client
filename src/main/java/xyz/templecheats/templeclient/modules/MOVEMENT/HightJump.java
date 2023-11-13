package xyz.templecheats.templeclient.modules.MOVEMENT;

import xyz.templecheats.templeclient.modules.Module;
import xyz.templecheats.templeclient.utils.ChatUtils;
import org.lwjgl.input.Keyboard;

public class HightJump extends Module {
    public HightJump() {
        super("HightJump", Keyboard.KEY_NONE, Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        mc.player.motionY = 5;
        ChatUtils.sendMessage("JUUUMP!");
        toggle();
    }
}