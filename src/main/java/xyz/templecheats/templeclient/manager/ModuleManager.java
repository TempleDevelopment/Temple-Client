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
import xyz.templecheats.templeclient.features.module.modules.render.norender.NoRender;
import xyz.templecheats.templeclient.features.module.modules.render.particle.Particle;
import xyz.templecheats.templeclient.features.module.modules.render.xray.XRay;
import xyz.templecheats.templeclient.features.module.modules.world.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class ModuleManager {

    private static final Map<Class<? extends Module>, Module> MODULES = new Reference2ReferenceOpenHashMap<>();

    public ModuleManager() {
        initMods();
    }

    /****************************************************************
     *                      Module Registration
     ****************************************************************/

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
        addMod(new TotemPopNotify());
        addMod(new ChatAppend());
        addMod(new ArmorAlert());
        addMod(new FancyChat());
        addMod(new GreenText());
        addMod(new Spammer());
        addMod(new Announcer());
        addMod(new ChatCrypt());

        // Combat
        addMod(new AutoDisconnect());
        addMod(new Replenish());
        addMod(new AutoTrap());
        addMod(new SelfTrap());
        addMod(new TriggerBot());
        addMod(new AutoCrystal());
        addMod(new AutoEXP());
        addMod(new BowAimbot());
        addMod(new BowSpam());
        addMod(new Surround());
        addMod(new AutoWeb());
        addMod(new BedAura());
        addMod(new HoleFiller());
        addMod(new AutoArmor());
        addMod(new AutoTotem());
        addMod(new AimAssist());
        addMod(new Quiver());
        addMod(new Velocity());
        addMod(new Aura());
        addMod(new Anchor());

        // Miscellaneous
        addMod(new AutoRespawn());
        addMod(new AutoClicker());
        addMod(new Log4jAlert());
        addMod(new ExtraChest());
        addMod(new AutoMount());
        addMod(new ExtraTooltips());
        addMod(new Gamemode());
        addMod(new FakePlayer());
        addMod(new SkinBlink());
        addMod(new AntiAFK());

        // Client
        addMod(new FontSettings());
        addMod(new ClickGUI());
        addMod(new Particles());
        addMod(new Panic());
        addMod(new Colors());
        addMod(new HUD());
        addMod(new RPC());

        // Movement
        addMod(new EntityControl());
        addMod(new RotationLock());
        addMod(new ReverseStep());
        addMod(new GuiWalk());
        addMod(new ElytraPlus());
        addMod(new TunnelSpeed());
        addMod(new AutoWalk());
        addMod(new Parkour());
        addMod(new NoSlow());
        addMod(new Safewalk());
        addMod(new IceSpeed());
        addMod(new Sprint());
        addMod(new NoFall());
        addMod(new Flight());
        addMod(new Speed());
        addMod(new Jesus());
        addMod(new Step());

        // Render
        addMod(new NameProtect());
        addMod(new ViewModel());
        addMod(new FullBright());
        addMod(new DeathEffect());
        addMod(new EnchantColor());
        addMod(new Hitmarker());
        addMod(new ChinaHat());
        addMod(new ViewClip());
        addMod(new NoRender());
        addMod(new PopChams());
        addMod(new Particle());
        addMod(new ItemPhysic());
        addMod(new NameTags());
        addMod(new Ambience());
        addMod(new Tracers());
        addMod(new Aspect());
        addMod(new Trail());
        addMod(new XRay());
        addMod(new ESP());

        // World
        addMod(new NoMineAnimation());
        addMod(new LiquidInteract());
        addMod(new BaseFinder());
        addMod(new ChunkAnimator());
        addMod(new MobOwner());
        addMod(new FastBreak());
        addMod(new FastPlace());
        addMod(new NewChunks());
        addMod(new Scaffold());
        addMod(new Nuker());

        // Player
        addMod(new AutoFish());
        addMod(new AutoEat());
        addMod(new Freecam());
        addMod(new FastUse());
        addMod(new MiddleClick());
        addMod(new NoSwing());
        addMod(new XCarry());
        addMod(new Blink());
        addMod(new Reach());
    }

    /****************************************************************
     *                      Module Retrieval Methods
     ****************************************************************/

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
