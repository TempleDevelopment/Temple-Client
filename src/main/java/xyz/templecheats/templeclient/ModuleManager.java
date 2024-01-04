package xyz.templecheats.templeclient;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.Display;
import xyz.templecheats.templeclient.impl.command.CommandManager;
import xyz.templecheats.templeclient.impl.command.commands.*;
import xyz.templecheats.templeclient.impl.modules.Module;
import xyz.templecheats.templeclient.impl.modules.chat.GreenText;
import xyz.templecheats.templeclient.impl.gui.clickgui.ClickGuiManager;
import xyz.templecheats.templeclient.impl.gui.font.FontUtils;
import xyz.templecheats.templeclient.impl.modules.client.ClickGUI;
import xyz.templecheats.templeclient.impl.modules.client.HUD;
import xyz.templecheats.templeclient.impl.modules.client.Panic;
import xyz.templecheats.templeclient.impl.modules.combat.*;
import xyz.templecheats.templeclient.impl.modules.misc.Blink;
import xyz.templecheats.templeclient.impl.modules.misc.FakeCreative;
import xyz.templecheats.templeclient.impl.modules.misc.FakePlayer;
import xyz.templecheats.templeclient.impl.modules.misc.Particles;
import xyz.templecheats.templeclient.impl.modules.movement.*;
import xyz.templecheats.templeclient.impl.modules.render.*;
import xyz.templecheats.templeclient.impl.modules.world.BlockReach;
import xyz.templecheats.templeclient.impl.modules.world.Nuker;
import xyz.templecheats.templeclient.impl.modules.world.Scaffold;

import java.util.ArrayList;

import static xyz.templecheats.templeclient.TempleClient.configManager;

public class ModuleManager {

    public static ArrayList<Module> modules = new ArrayList<>();
    public static CommandManager commandManager;

    public static void addMod(Module mod) {
        modules.add(mod);
    }

    public static void initMods() {
        // Combat
        addMod(new AimBot());
        addMod(new AntiBot());
        addMod(new AutoClicker());
        addMod(new AutoArmor());
        addMod(new AutoCrystal());
        addMod(new AutoDisconnect());
        addMod(new AutoRespawn());
        addMod(new AutoTotem());
        addMod(new FastXP());
        addMod(new HitBox());
        addMod(new KillAura());
        addMod(new TriggerBot());
        // Miscellaneous
        addMod(new Blink());
        addMod(new FakeCreative());
        addMod(new FakePlayer());
        addMod(new Particles());
        // Client
        addMod(new ClickGUI());
        addMod(new HUD());
        addMod(new Panic());
        // Movement
        addMod(new AirJump());
        addMod(new AutoWalk());
        addMod(new BoatFly());
        addMod(new BunnyHop());
        addMod(new ClickTP());
        addMod(new ElytraPlus());
        addMod(new FastFall());
        addMod(new Fly());
        addMod(new Glide());
        addMod(new InvWalk());
        addMod(new Jesus());
        addMod(new Speed());
        addMod(new Spider());
        addMod(new Sprint());
        addMod(new Yaw());
        addMod(new Velocity());
        // Render
        addMod(new Fov());
        addMod(new Freecam());
        addMod(new FullBright());
        addMod(new ItemESP());
        addMod(new NameProtect());
        addMod(new NameTags());
        addMod(new NoRender());
        addMod(new PlayerESP());
        addMod(new Radar());
        addMod(new SpawnerESP());
        addMod(new StorageESP());
        addMod(new TargetHUD());
        addMod(new Tracers());
        addMod(new ViewModel());
        // World
        addMod(new BlockReach());
        addMod(new Nuker());
        addMod(new Scaffold());
        // Chat
        addMod(new GreenText());
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

            if (module.isToggled() && Minecraft.getMinecraft().player != null) {
                module.onUpdate();
            }
            module.onUpdateConstant();
        }
    }
}
