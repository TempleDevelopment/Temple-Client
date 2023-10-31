package com.example.examplemod.Module.MOVEMENT;

import com.example.examplemod.Module.Module;
import org.lwjgl.input.Keyboard;

public class Fly extends Module {
    public Fly() {
        super("Fly", Keyboard.KEY_NONE, Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        mc.player.capabilities.isFlying = true;
        mc.player.capabilities.allowFlying = true;
    }
}
