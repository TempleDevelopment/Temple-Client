package xyz.templecheats.templeclient;

import xyz.templecheats.templeclient.modules.COMBAT.*;
import xyz.templecheats.templeclient.modules.EXPLOITS.ClickTP;
import xyz.templecheats.templeclient.modules.EXPLOITS.FakeCreative;
import xyz.templecheats.templeclient.modules.CLIENT.Panic;
import xyz.templecheats.templeclient.modules.MISC.FakePlayer;
import xyz.templecheats.templeclient.modules.MISC.Particles;
import xyz.templecheats.templeclient.modules.MOVEMENT.*;
import xyz.templecheats.templeclient.modules.Module;
import xyz.templecheats.templeclient.gui.clickgui.ClickGuiScreen;
import xyz.templecheats.templeclient.modules.EXPLOITS.BlockReach;
import xyz.templecheats.templeclient.modules.HUD.ArmorHUD;
import xyz.templecheats.templeclient.modules.HUD.Coords;
import xyz.templecheats.templeclient.modules.RENDER.*;
import xyz.templecheats.templeclient.modules.BLOCKS.Nuker;
import xyz.templecheats.templeclient.modules.BLOCKS.Scaffold;
import xyz.templecheats.templeclient.font.FontUtils;
import org.lwjgl.opengl.Display;

import java.util.concurrent.CopyOnWriteArrayList;

public class Client {
    public static String name = "Temple Client 1.7.3";
    public static String cName = "Temple Client";
    public static CopyOnWriteArrayList<Module> modules = new CopyOnWriteArrayList<Module>();

    public static ClickGuiScreen clickGui;

    public static void startup(){
        Display.setTitle(name);

        /*
        Module Imports
        */

        // Combat
        modules.add(new AimBot());
        modules.add(new AntiBot());
        modules.add(new HitBox());
        modules.add(new KillAura());
        modules.add(new TriggerBot());
        modules.add(new TpAura());
        // Exploits
        modules.add(new FakeCreative());
        modules.add(new BlockReach());
        modules.add(new ClickTP());
        // Miscellaneous
        modules.add(new FakePlayer());
        modules.add(new Particles());
        // Client
        modules.add(new Panic());
        // Movement
        modules.add(new AirJump());
        modules.add(new BoatFly());
        modules.add(new FastFall());
        modules.add(new Fly());
        modules.add(new BunnyHop());
        modules.add(new Glide());
        modules.add(new HightJump());
        modules.add(new Jesus());
        modules.add(new Speed());
        modules.add(new Spider());
        modules.add(new Sprint());
        modules.add(new Yaw());
        // Hud
        modules.add(new ArmorHUD());
        modules.add(new Coords());
        // Render
        modules.add(new ChestESP());
        modules.add(new PlayerESP());
        modules.add(new FullBright());
        modules.add(new ItemESP());
        modules.add(new NameProtect());
        modules.add(new NameTags());
        modules.add(new Radar());
        modules.add(new SpawnerESP());
        modules.add(new TargetHUD());
        modules.add(new Tracers());
        modules.add(new ViewModel());
        // Blocks
        modules.add(new Nuker());
        modules.add(new Scaffold());







        clickGui = new ClickGuiScreen();

        FontUtils.bootstrap();
    }

    public static void keyPress(int key) {
        for (Module m : modules) {
            if (m.getKey() == key) {
                m.toggle();
            }
        }
    }
}
