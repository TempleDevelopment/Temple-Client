package xyz.templecheats.templeclient.features.module.modules.client;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.manager.ModuleManager;

import java.util.HashMap;
import java.util.Map;

public class Panic extends Module {
    /****************************************************************
     *                      Variables
     ****************************************************************/
    public static boolean isPanic = false;
    private final Map<Module, Boolean> moduleStates = new HashMap<>();

    public Panic() {
        super("Panic", "Turn off all modules and save their states", Keyboard.KEY_NONE, Category.Client);
    }

    @Override
    public void onEnable() {
        isPanic = true;
        Display.setTitle("Minecraft 1.12.2");

        for (Module m : ModuleManager.getModules()) {
            if (m != this) {
                moduleStates.put(m, m.isToggled());
                m.setToggled(false);
            }
        }
    }

    @Override
    public void onDisable() {
        isPanic = false;

        moduleStates.forEach((module, state) -> {
            if (module != this) {
                module.setToggled(state);
            }
        });

        Display.setTitle("TempleClient 1.12.2 | User: " + Minecraft.getMinecraft().getSession().getUsername());
        moduleStates.clear();
    }
}
