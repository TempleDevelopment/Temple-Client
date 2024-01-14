package xyz.templecheats.templeclient.impl.modules.client;

import xyz.templecheats.templeclient.ModuleManager;
import xyz.templecheats.templeclient.impl.modules.Module;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

public class Panic extends Module {
    public static boolean isPanic = false;

    public Panic() {
        super("Panic", Keyboard.KEY_NONE, Category.CLIENT);
    }

    @Override
    public void onEnable() {
        isPanic = true;

        Display.setTitle("Minecraft 1.12.2");

        for (Module m : ModuleManager.modules) {
            if (m != this) {
                m.setToggled(false);
            }
        }
    }

    @Override
    public void onDisable() {
        isPanic = false;

        Display.setTitle(name);
    }
}