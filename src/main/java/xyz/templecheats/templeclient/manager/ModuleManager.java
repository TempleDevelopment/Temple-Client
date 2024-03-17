package xyz.templecheats.templeclient.manager;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.chat.ChatCrypt;
import xyz.templecheats.templeclient.features.module.modules.chat.FancyChat;
import xyz.templecheats.templeclient.features.module.modules.chat.GreenText;
import xyz.templecheats.templeclient.features.module.modules.chat.Spammer;
import xyz.templecheats.templeclient.features.module.modules.client.*;
import xyz.templecheats.templeclient.features.module.modules.combat.*;
import xyz.templecheats.templeclient.features.module.modules.misc.*;
import xyz.templecheats.templeclient.features.module.modules.movement.*;
import xyz.templecheats.templeclient.features.module.modules.player.Blink;
import xyz.templecheats.templeclient.features.module.modules.player.Reach;
import xyz.templecheats.templeclient.features.module.modules.render.*;
import xyz.templecheats.templeclient.features.module.modules.world.Nuker;
import xyz.templecheats.templeclient.features.module.modules.world.Scaffold;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class ModuleManager {

    public ModuleManager() {
        initMods();
    }

    private static final Map<Class<? extends Module>, Module> MODULES = new Reference2ReferenceOpenHashMap<>();

    public static void addMod(Module mod) {
        MODULES.put(mod.getClass(), mod);
    }

    public static void initMods() {
        // Chat
        addMod(new ChatCrypt());
        addMod(new FancyChat());
        addMod(new GreenText());
        addMod(new Spammer());
        // Combat
        addMod(new AutoDisconnect());
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
        addMod(new HitBox());
        addMod(new FastXP());
        addMod(new Aura());
        // Miscellaneous
        addMod(new FakeCreative());
        addMod(new ChestStealer());
        addMod(new AutoRespawn());
        addMod(new FakePlayer());
        addMod(new Log4jAlert());
        addMod(new Hitmarker());
        // Client
        addMod(new MiddleClick());
        addMod(new Particles());
        addMod(new ClickGUI());
        addMod(new Panic());
        addMod(new Font());
        addMod(new HUD());
        // Movement
        addMod(new ElytraPlus());
        addMod(new AutoWalk());
        addMod(new YawLock());
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
        addMod(new BlockOverlay());
        addMod(new DeathEffect());
        addMod(new NameProtect());
        addMod(new FullBright());
        addMod(new SpawnerESP());
        addMod(new StorageESP());
        addMod(new PlayerESP());
        addMod(new ViewModel());
        addMod(new NoRender());
        addMod(new NameTags());
        addMod(new ItemESP());
        addMod(new Tracers());
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
        for (Module m : MODULES.values()) {
            if (m.getCategory().name().equalsIgnoreCase(c.name())) {
                mods.add(m);
            }
        }
        return mods;
    }

    public static Collection<Module> getModules() {
        return MODULES.values();
    }

    public static ArrayList<Module> getActiveModules() {
        ArrayList<Module> active = new ArrayList<>();
        for (Module m : MODULES.values()) {
            if (m.isEnabled()) {
                active.add(m);
            }
        }
        return active;
    }

    public static Module getModuleByName(String name) {
        for (Module mod : MODULES.values()) {
            if (mod.getName().equalsIgnoreCase(name)) {
                return mod;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Module> T getModule(Class<T> moduleClass) {
        return (T) MODULES.get(moduleClass);
    }

    public static void keyPress(int key) {
        for (Module m : MODULES.values()) {
            if (m.getKey() == key) {
                m.toggle();
            }
        }
    }

    public void onPlayerTick() {
        for (Module module : MODULES.values()) {
            module.onUpdateInternal();
        }
    }
}