package com.example.examplemod.Module;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

public class Module {
    public String name;
    public boolean toggled;
    public int KeyCode;
    public Category category;
    public Minecraft mc = Minecraft.getMinecraft();
    public int keyCode;

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

    public void onEnable(){
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void onDisable(){
        MinecraftForge.EVENT_BUS.unregister(this);
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
        COMBAT,
        MOVMENT,
        PLAYER,
        RENDER,
        MISC,
        MINIGAMES,
        EXPLOIT;
    }

    public void toggle() {
        toggled =!toggled;
        if (toggled) {
            onEnable();
        } else {
            onDisable();
        }
    }


    public void setToggled(boolean toggled) {
        this.toggled = toggled;
        if (this.toggled) {
            this.onEnable();
        } else {
            this.onDisable();
        }
    }
}
