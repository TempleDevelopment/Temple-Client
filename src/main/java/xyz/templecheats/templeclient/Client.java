package xyz.templecheats.templeclient;

import net.minecraft.client.Minecraft;
import xyz.templecheats.templeclient.features.modules.client.*;
import xyz.templecheats.templeclient.features.modules.combat.*;
import xyz.templecheats.templeclient.features.modules.movement.*;
import xyz.templecheats.templeclient.features.modules.render.*;
import xyz.templecheats.templeclient.features.modules.movement.ClickTP;
import xyz.templecheats.templeclient.features.modules.misc.FakeCreative;
import xyz.templecheats.templeclient.features.modules.render.FakePlayer;
import xyz.templecheats.templeclient.features.modules.misc.Particles;
import xyz.templecheats.templeclient.features.modules.Module;
import xyz.templecheats.templeclient.features.modules.world.BlockReach;
import xyz.templecheats.templeclient.features.modules.world.Nuker;
import xyz.templecheats.templeclient.features.modules.world.Scaffold;
import xyz.templecheats.templeclient.gui.font.FontUtils;
import org.lwjgl.opengl.Display;
import xyz.templecheats.templeclient.gui.clickgui.ClickGuiManager;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.ArrayList;

public class Client {
    public static String name = "Temple Client 1.7.7";
    public static String cName = "Temple Client";
    public static CopyOnWriteArrayList<Module> modules = new CopyOnWriteArrayList<Module>();

    public static ClickGuiManager clickGuiManager;

    public static void startup() {
        Display.setTitle(name);

        /*
        Module Imports
        */

        // Combat
        modules.add(new AimBot());
        modules.add(new AntiBot());
        modules.add(new AutoArmor());
        modules.add(new AutoCrystal());
        modules.add(new HitBox());
        modules.add(new KillAura());
        modules.add(new TriggerBot());
        // Miscellaneous
        modules.add(new Particles());
        modules.add(new FakeCreative());
        // Client
        modules.add(new ArmorHUD());
        modules.add(new ClickGUI());
        modules.add(new Coords());
        modules.add(new Panic());
        // Movement
        modules.add(new AirJump());
        modules.add(new BoatFly());
        modules.add(new BunnyHop());
        modules.add(new ClickTP());
        modules.add(new ElytraPlus());
        modules.add(new FastFall());
        modules.add(new Fly());
        modules.add(new Glide());
        modules.add(new InvWalk());
        modules.add(new Jesus());
        modules.add(new Speed());
        modules.add(new Spider());
        modules.add(new Sprint());
        modules.add(new Yaw());
        modules.add(new Velocity());
        // Render
        modules.add(new FakePlayer());
        modules.add(new FullBright());
        modules.add(new ItemESP());
        modules.add(new NameProtect());
        modules.add(new NameTags());
        modules.add(new PlayerESP());
        modules.add(new PlayerModel());
        modules.add(new Radar());
        modules.add(new SpawnerESP());
        modules.add(new StorageESP());
        modules.add(new TargetHUD());
        modules.add(new Tracers());
        modules.add(new ViewModel());
        // World
        modules.add(new BlockReach());
        modules.add(new Nuker());
        modules.add(new Scaffold());

        clickGuiManager = new ClickGuiManager();

        FontUtils.bootstrap();
    }

    public static ArrayList<Module> getModulesInCategory(Module.Category c) {
        ArrayList<Module> mods = new ArrayList<Module>();
        for (Module m : modules) {
            if (m.getCategory().name().equalsIgnoreCase(c.name())) {
                mods.add(m);
            }
        }
        return mods;
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

    public static void keyPress(int key) {
        for (Module m : modules) {
            if (m.getKey() == key) {
                m.toggle();
            }
        }
    }

    public static void onPlayerTick() {
        for (Module module : modules) {
            if (module.isEnabled() && Minecraft.getMinecraft().player != null) {
                module.onUpdate();
            }
            module.onUpdateConstant();
        }
    }
}
