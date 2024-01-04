package xyz.templecheats.templeclient.impl.modules;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import xyz.templecheats.templeclient.TempleClient;

public class Module {
    public String name;
    public boolean toggled;
    public int KeyCode;
    public Category category;
    public static Minecraft mc = Minecraft.getMinecraft();

    public Module(String name, int keyCode, Category c) {
        this.name = name;
        this.KeyCode = keyCode;
        this.category = c;
    }

    public boolean isEnabled() {
        return toggled;
    }

    public int getKey() {
        return KeyCode;
    }

    public void onUpdate() {
        return;
    }

    public void onUpdateConstant() {
    }

    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    public void enable() {
        this.toggled = true;
        onEnable();
    }

    public void disable() {
        this.toggled = false;
        onDisable();
    }

    public void toggle() {
        if (toggled) {
            disable();
        } else {
            enable();
        }
    }

    public boolean isToggled() {
        return toggled;
    }

    public void setKey(int key) {
        this.KeyCode = key;
    }

    public Category getCategory() {
        return category;
    }

    public String getName() {
        return this.name;
    }

    public enum Category {

        CHAT,
        COMBAT,
        MISC,
        MOVEMENT,
        RENDER,
        WORLD,
        CLIENT;
    }

    private int setting1;
    private boolean setting2;

    public int getSetting1() {
        return setting1;
    }

    public void setSetting1(int setting1) {
        this.setting1 = setting1;
    }

    public boolean getSetting2() {
        return setting2;
    }

    public void setSetting2(boolean setting2) {
        this.setting2 = setting2;
    }


    public void setToggled(boolean toggled) {
        //dont do anything if the toggled state is the same
        if(toggled == this.toggled) return;
        
        this.toggled = toggled;
        if (toggled) {
            onEnable();
        } else {
            onDisable();
        }
    }
}