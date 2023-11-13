package xyz.templecheats.templeclient;

import xyz.templecheats.templeclient.module.COMBAT.*;
import xyz.templecheats.templeclient.module.EXPLOITS.ClickTP;
import xyz.templecheats.templeclient.module.EXPLOITS.FakeCreative;
import xyz.templecheats.templeclient.module.CLIENT.Panic;
import xyz.templecheats.templeclient.module.MISC.Particles;
import xyz.templecheats.templeclient.module.MOVEMENT.*;
import xyz.templecheats.templeclient.module.Module;
import xyz.templecheats.templeclient.gui.clickgui.ClickGuiScreen;
import xyz.templecheats.templeclient.module.EXPLOITS.BlockReach;
import xyz.templecheats.templeclient.module.HUD.ArmorDisplay;
import xyz.templecheats.templeclient.module.HUD.Coords;
import xyz.templecheats.templeclient.module.RENDER.*;
import xyz.templecheats.templeclient.module.BLOCKS.Nuker;
import xyz.templecheats.templeclient.module.BLOCKS.Scaffold;
import xyz.templecheats.templeclient.font.FontUtils;
import org.lwjgl.opengl.Display;

import java.util.concurrent.CopyOnWriteArrayList;

public class Client {
    public static String name = "Temple Client 1.7.2";
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
        modules.add(new Particles());
        modules.add(new Panic());
        // Movement
        modules.add(new AirJump());
        modules.add(new BoatFly());
        modules.add(new FastFall());
        modules.add(new Fly());
        modules.add(new BHOP());
        modules.add(new Glide());
        modules.add(new HightJump());
        modules.add(new Jesus());
        modules.add(new Speed());
        modules.add(new Spider());
        modules.add(new Sprint());
        modules.add(new Yaw());
        // Other
        modules.add(new ArmorDisplay());
        modules.add(new Coords());
        // Render
        modules.add(new PlayerESP());
        modules.add(new ChestESP());
        modules.add(new ItemsESP());
        modules.add(new SpawnerESP());
        modules.add(new TargetHUD());
        modules.add(new Tracers());
        modules.add(new FullBright());
        modules.add(new NameProtect());
        modules.add(new NameTags());
        modules.add(new Radar());
        modules.add(new ViewModel());
        // World
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
