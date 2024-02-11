package xyz.templecheats.templeclient;

import xyz.templecheats.templeclient.impl.modules.Module;
import xyz.templecheats.templeclient.impl.modules.chat.GreenText;
import xyz.templecheats.templeclient.impl.modules.chat.Spammer;
import xyz.templecheats.templeclient.impl.modules.client.*;
import xyz.templecheats.templeclient.impl.modules.combat.*;
import xyz.templecheats.templeclient.impl.modules.misc.*;
import xyz.templecheats.templeclient.impl.modules.movement.*;
import xyz.templecheats.templeclient.impl.modules.player.*;
import xyz.templecheats.templeclient.impl.modules.render.*;
import xyz.templecheats.templeclient.impl.modules.world.Nuker;
import xyz.templecheats.templeclient.impl.modules.world.Scaffold;

import java.util.ArrayList;

public class ModuleManager {

    public static ArrayList<Module> modules = new ArrayList<>();

    public static void addMod(Module mod) {
        modules.add(mod);
    }

    public static void initMods() {
        // Chat
        addMod(new GreenText());
        addMod(new Spammer());
        // Combat
        addMod(new AutoDisconecct());
        addMod(new AutoCrystal());
        addMod(new AutoClicker());
        addMod(new AutoGapple());
        addMod(new AutoArmor());
        addMod(new AutoTotem());
        addMod(new Surround());
        addMod(new Velocity());
        addMod(new AntiBots());
        addMod(new AimAssist());
        addMod(new Trigger());
        addMod(new Hitbox());
        addMod(new FastXP());
        addMod(new Aura());
        // Miscellaneous
        addMod(new FakeCreative());
        addMod(new AutoRespawn());
        addMod(new FakePlayer());
        addMod(new Hitmarker());
        // Client
        addMod(new Particles());
        addMod(new ClickGUI());
        addMod(new Panic());
        addMod(new Font());
        addMod(new HUD());
        // Movement
        addMod(new ElytraPlus());
        addMod(new FastFall());
        addMod(new AutoWalk());
        addMod(new YawLock());
        addMod(new AirJump());
        addMod(new BoatFly());
        addMod(new GuiWalk());
        addMod(new Sprint());
        addMod(new Spider());
        addMod(new NoFall());
        addMod(new Speed());
        addMod(new Glide());
        addMod(new Jesus());
        addMod(new Flight());
        // Render
        addMod(new BlockHighlight());
        addMod(new DeathEffect());
        addMod(new NameProtect());
        addMod(new Fullbright());
        addMod(new SpawnerESP());
        addMod(new StorageESP());
        addMod(new PlayerESP());
        addMod(new ViewModel());
        addMod(new NoRender());
        addMod(new NameTags());
        addMod(new ItemESP());
        addMod(new Tracers());
        addMod(new Fov());
        // World
        addMod(new Scaffold());
        addMod(new Nuker());
        // Player
        addMod(new Freecam());
        addMod(new Reach());
        addMod(new Blink());
    }


    public static ArrayList<Module> getModulesInCategory(Module.Category c) {
        ArrayList<Module> mods = new ArrayList<>();
        for (Module m : modules) {
            if (m.getCategory().name().equalsIgnoreCase(c.name())) {
                mods.add(m);
            }
        }
        return mods;
    }

    public static ArrayList<Module> getModules() {
        return modules;
    }

    public static ArrayList<Module> getActiveModules() {
        ArrayList<Module> active = new ArrayList<>();
        for (Module m : modules) {
            if (m.isEnabled()) {
                active.add(m);
            }
        }
        return active;
    }

    public static Module getModuleByName(String name) {
        for (Module mod : modules) {
            if (mod.getName().equalsIgnoreCase(name)) {
                return mod;
            }
        }
        return null;
    }

    public static void keyPress(int key) {
        for (Module m : modules) {
            if (m.getKey() == key) {
                m.toggle();
            }
        }
    }

    public void onPlayerTick() {
        for (Module module : modules) {
            module.onUpdateInternal();
        }
    }
}