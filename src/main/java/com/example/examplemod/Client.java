package com.example.examplemod;

import com.example.examplemod.Module.COMBAT.*;
import com.example.examplemod.Module.EXPLOITS.ClickTP;
import com.example.examplemod.Module.MISC.Panic;
import com.example.examplemod.Module.MISC.Particles;
import com.example.examplemod.Module.MOVEMENT.*;
import com.example.examplemod.Module.Module;
import clickgui.ClickGuiScreen;
import com.example.examplemod.Module.EXPLOITS.BlockReach;
import com.example.examplemod.Module.OTHER.Coords;
import com.example.examplemod.Module.RENDER.*;
import com.example.examplemod.Module.WORLD.Nuker;
import com.example.examplemod.Module.WORLD.Scaffold;
import org.lwjgl.opengl.Display;

import java.util.concurrent.CopyOnWriteArrayList;

public class Client {
    public static String name = "Temple Client 1.12.2";
    public static CopyOnWriteArrayList<Module> modules = new CopyOnWriteArrayList<Module>();

    public static ClickGuiScreen clickGui;
    public static String cName;

    public static void startup(){
        Display.setTitle(name);

        // Combat
        modules.add(new AimBot());
        modules.add(new AntiBot());
        modules.add(new HitBox());
        modules.add(new KillAura());
        modules.add(new TriggerBot());
        modules.add(new TpAura());
        // Exploits
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
        modules.add(new Coords());
        // Render
        modules.add(new AttackTrace());
        modules.add(new ChestESP());
        modules.add(new FullBright());
        modules.add(new GlowESP());
        modules.add(new ItemsESP());
        modules.add(new NameTags());
        modules.add(new Radar());
        modules.add(new SpawnerESP());
        modules.add(new TargetHUD());
        modules.add(new Tracers());
        modules.add(new ViewModel());
        // World
        modules.add(new Nuker());
        modules.add(new Scaffold());







        clickGui = new ClickGuiScreen();
    }

    public static void keyPress(int key) {
        for (Module m : modules) {
            if (m.getKey() == key) {
                m.toggle();
            }
        }
    }
}
