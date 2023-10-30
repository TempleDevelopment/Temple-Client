package com.example.examplemod.Module.MOVEMENT;

import com.example.examplemod.Module.Module;
import com.example.examplemod.Utils.ChatUtils;
import org.lwjgl.input.Keyboard;

public class HightJump extends Module {
    public HightJump() {
        super("HightJump", Keyboard.KEY_J, Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        mc.player.motionY = 5;
        ChatUtils.sendMessage("JUUUMP!");
        toggle();
    }
}