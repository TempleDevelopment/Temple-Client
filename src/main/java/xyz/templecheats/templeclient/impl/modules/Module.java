package xyz.templecheats.templeclient.impl.modules;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

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

        if (mc.player != null) {
            onEnable();
        }
    }

    public void disable() {
        this.toggled = false;

        if (mc.player != null) {
            onDisable();
        }
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

    public void setToggled(boolean toggled) {
        //dont do anything if the toggled state is the same
        if (toggled == this.toggled) return;

        if (toggled) {
            enable();
        } else {
            disable();
        }
    }
}