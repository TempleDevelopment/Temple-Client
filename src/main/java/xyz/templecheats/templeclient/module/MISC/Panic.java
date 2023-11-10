package xyz.templecheats.templeclient.module.MISC;

import xyz.templecheats.templeclient.Client;
import xyz.templecheats.templeclient.module.Module;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

public class Panic extends Module {
    public static boolean isPanic = false;

    public Panic() {
        super("Panic[F4]", Keyboard.KEY_F4, Category.MISC);
    }

    @Override
    public void onEnable() {
        isPanic = true;

        Display.setTitle("Minecraft 1.12.2");

        for (Module m : Client.modules) {
            if (m != this) {
                m.setToggled(false);
            }
        }
    }

    @Override
    public void onDisable() {
        isPanic = false;

        Display.setTitle(Client.name);
    }
}