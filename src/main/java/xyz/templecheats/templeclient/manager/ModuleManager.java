package xyz.templecheats.templeclient.manager;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.chat.*;
import xyz.templecheats.templeclient.features.module.modules.client.*;
import xyz.templecheats.templeclient.features.module.modules.combat.*;
import xyz.templecheats.templeclient.features.module.modules.misc.*;
import xyz.templecheats.templeclient.features.module.modules.movement.*;
import xyz.templecheats.templeclient.features.module.modules.movement.speed.Speed;
import xyz.templecheats.templeclient.features.module.modules.player.*;
import xyz.templecheats.templeclient.features.module.modules.render.*;
import xyz.templecheats.templeclient.features.module.modules.render.esp.ESP;
import xyz.templecheats.templeclient.features.module.modules.render.particle.Particle;
import xyz.templecheats.templeclient.features.module.modules.render.xray.XRay;
import xyz.templecheats.templeclient.features.module.modules.world.MobOwner;
import xyz.templecheats.templeclient.features.module.modules.world.NewChunks;
import xyz.templecheats.templeclient.features.module.modules.world.Scaffold;
import xyz.templecheats.templeclient.features.module.modules.world.Nuker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class ModuleManager {

    public ModuleManager() {
        initMods();
    }

    private static final Map < Class < ? extends Module > , Module > MODULES = new Reference2ReferenceOpenHashMap < > ();

    public static void addMod(Module mod) {
        MODULES.put(mod.getClass(), mod);

        if (!mod.submodules.isEmpty()) {
            for (Module sub : mod.submodules) {
                addMod(sub);
            }
        }
    }

    public static void initMods() {
        // Chat
        addMod(new Announcer());
        addMod(new ArmorAlert());
        addMod(new ChatCrypt());
        addMod(new FancyChat());
        addMod(new GreenText());
        addMod(new Spammer());
        addMod(new TotemPopNotify());
        // Combat
        addMod(new AutoDisconnect());
        addMod(new AutoCrystal());
        addMod(new BowAimbot());
        addMod(new BowSpam());
        addMod(new AutoArmor());
        addMod(new AutoTotem());
        addMod(new Surround());
        addMod(new Velocity());
        addMod(new AimAssist());
        addMod(new TriggerBot());
        addMod(new Offhand());
        addMod(new Aura());
        // Miscellaneous
        addMod(new AutoClicker());
        addMod(new Gamemode());
        addMod(new ExtraChest());
        addMod(new AutoRespawn());
        addMod(new FakePlayer());
        addMod(new Log4jAlert());
        addMod(new AutoMount());
        // Client
        addMod(new MiddleClick());
        addMod(new Particles());
        addMod(new ClickGUI());
        addMod(new Panic());
        addMod(new Colors());
        addMod(new FontSettings());
        addMod(new HUD());
        // Movement
        addMod(new ElytraPlus());
        addMod(new TunnelSpeed());
        addMod(new AutoWalk());
        addMod(new RotationLock());
        addMod(new GuiWalk());
        addMod(new NoSlow());
        addMod(new FastFall());
        addMod(new Sprint());
        addMod(new NoFall());
        addMod(new Speed());
        addMod(new Flight());
        addMod(new Jesus());
        addMod(new Step());
        // Render
        addMod(new DeathEffect());
        addMod(new NameProtect());
        addMod(new FullBright());
        addMod(new ViewModel());
        addMod(new ViewClip());
        addMod(new ChinaHat());
        addMod(new Hitmarker());
        addMod(new NoRender());
        addMod(new Particle());
        addMod(new PopChams());
        addMod(new NameTags());
        addMod(new Ambience());
        addMod(new Tracers());
        addMod(new Aspect());
        addMod(new Trail());
        addMod(new XRay());
        addMod(new ESP());
        // World
        addMod(new Scaffold());
        addMod(new Nuker());
        addMod(new MobOwner());
        addMod(new NewChunks());
        // Player
        addMod(new Freecam());
        addMod(new AutoEat());
        addMod(new FastUse());
        addMod(new Reach());
        addMod(new Blink());
    }

    public static ArrayList < Module > getModulesInCategory(Module.Category c) {
        ArrayList < Module > mods = new ArrayList < > ();
        for (Module m: MODULES.values()) {
            if (m.getCategory().name().equalsIgnoreCase(c.name())) {
                mods.add(m);
            }
        }
        return mods;
    }

    public static Collection < Module > getModules() {
        return MODULES.values();
    }

    public static ArrayList < Module > getActiveModules() {
        ArrayList < Module > active = new ArrayList < > ();
        for (Module m: MODULES.values()) {
            if (m.isEnabled()) {
                active.add(m);
            }
        }
        return active;
    }

    public static Module getModuleByName(String name) {
        for (Module mod: MODULES.values()) {
            if (mod.getName().equalsIgnoreCase(name)) {
                return mod;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static < T extends Module > T getModule(Class < T > moduleClass) {
        return (T) MODULES.get(moduleClass);
    }

    public static void keyPress(int key) {
        for (Module m: MODULES.values()) {
            if (m.getKey() == key) {
                m.toggle();
            }
        }
    }

    public void onPlayerTick() {
        for (Module module: MODULES.values()) {
            module.onUpdateInternal();
        }
    }
}
