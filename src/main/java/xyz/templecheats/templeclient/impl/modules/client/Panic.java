package xyz.templecheats.templeclient.impl.modules.client;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import xyz.templecheats.templeclient.ModuleManager;
import xyz.templecheats.templeclient.impl.modules.Module;

public class Panic extends Module {
    public static boolean isPanic = false;
    
    public Panic() {
        super("Panic", "Disables everything so you wont get caught ;)", Keyboard.KEY_NONE, Category.Client);
    }
    
    @Override
    public void onEnable() {
        isPanic = true;
        
        Display.setTitle("Minecraft 1.12.2");
        
        for(Module m : ModuleManager.modules) {
            if(m != this) {
                m.setToggled(false);
            }
        }
    }
    
    @Override
    public void onDisable() {
        isPanic = false;
        
        Display.setTitle("TempleClient 1.12.2 | User: " + Minecraft.getMinecraft().getSession().getUsername());
    }
}