package xyz.templecheats.templeclient;

import xyz.templecheats.templeclient.features.modules.combat.*;
import xyz.templecheats.templeclient.features.modules.movement.*;
import xyz.templecheats.templeclient.features.modules.render.*;
import xyz.templecheats.templeclient.features.modules.exploits.ClickTP;
import xyz.templecheats.templeclient.features.modules.exploits.FakeCreative;
import xyz.templecheats.templeclient.features.modules.client.Panic;
import xyz.templecheats.templeclient.features.modules.client.FakePlayer;
import xyz.templecheats.templeclient.features.modules.misc.Particles;
import xyz.templecheats.templeclient.features.modules.Module;
import xyz.templecheats.templeclient.features.modules.exploits.BlockReach;
import xyz.templecheats.templeclient.features.modules.hud.ArmorHUD;
import xyz.templecheats.templeclient.features.modules.hud.Coords;
import xyz.templecheats.templeclient.features.modules.blocks.Nuker;
import xyz.templecheats.templeclient.features.modules.blocks.Scaffold;
import xyz.templecheats.templeclient.gui.font.FontUtils;
import org.lwjgl.opengl.Display;
import xyz.templecheats.templeclient.gui.clickgui.ClickGuiManager;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.ArrayList;

public class Client {
    public static String name = "Temple Client 1.7.6";
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
        modules.add(new HitBox());
        modules.add(new KillAura());
        modules.add(new TpAura());
        modules.add(new TriggerBot());
        // Exploits
        modules.add(new BlockReach());
        modules.add(new ClickTP());
        modules.add(new FakeCreative());
        // Miscellaneous
        modules.add(new Particles());
        // Client
        modules.add(new FakePlayer());
        modules.add(new Panic());
        // Movement
        modules.add(new AirJump());
        modules.add(new BoatFly());
        modules.add(new BunnyHop());
        modules.add(new ElytraBoost());
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
        // Hud
        modules.add(new ArmorHUD());
        modules.add(new Coords());
        // Render
        modules.add(new FullBright());
        modules.add(new ItemESP());
        modules.add(new NameProtect());
        modules.add(new NameTags());
        modules.add(new PlayerESP());
        modules.add(new Radar());
        modules.add(new SpawnerESP());
        modules.add(new StorageESP());
        modules.add(new TargetHUD());
        modules.add(new Tracers());
        modules.add(new ViewModel());
        // Blocks
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

    public static void keyPress(int key) {
        for (Module m : modules) {
            if (m.getKey() == key) {
                m.toggle();
            }
        }
    }
}
