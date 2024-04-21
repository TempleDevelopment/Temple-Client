package xyz.templecheats.templeclient.features.module;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.features.module.modules.client.hud.notification.NotificationType;
import xyz.templecheats.templeclient.features.module.modules.client.hud.notification.Notifications;
import xyz.templecheats.templeclient.util.color.impl.GradientShader;
import xyz.templecheats.templeclient.util.setting.SettingHolder;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class Module extends SettingHolder {
    /*
     * Variables
     */
    public static final Minecraft mc = Minecraft.getMinecraft();
    public ArrayList<Module> submodules = new ArrayList<>();
    private final String description;
    public Category category;
    public int KeyCode;
    public boolean enableByDefault;
    public boolean toggled;
    public boolean submodule = false;
    public boolean parent = false;
    private boolean queueEnable;

    public Module(String name, String description, int keyCode, Category c) {
        super(name);
        this.description = description;
        this.KeyCode = keyCode;
        this.category = c;
    }

    public Module(String name, String description, Category c, boolean parent) {
        super(name);
        this.description = description;
        this.category = c;
        this.parent = parent;
    }

    public Module(String name, String description, int keyCode, Category c, boolean submodule) {
        super(name);
        this.description = description;
        this.KeyCode = keyCode;
        this.category = c;
        this.submodule = submodule;

        for (Class<? extends Module> clazz : modules()) {
            try {
                Module sub = clazz.getConstructor().newInstance();

                sub.category = category;
                submodules.add(sub);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                TempleClient.logger.error("Cant create new instance of submodule of " + name + " module!", e, e.getCause());
            }
        }
    }

    public String getDescription() {
        return this.description;
    }

    public boolean isEnabled() {
        return toggled;
    }

    public int getKey() {
        return KeyCode;
    }
    public final void onUpdateInternal() {
        if (mc.player != null) {
            if (this.queueEnable) {
                this.queueEnable = false;
                this.onEnable();
            }

            if (this.isToggled()) {
                this.onUpdate();
            }
        }

        this.onUpdateConstant();
    }

    public void onUpdate() {}

    public void onUpdateConstant() {}

    public void onRenderWorld(float partialTicks) {}
    public void onEnable() {}

    public void onDisable() {
        GradientShader.finish();
    }

    public String getHudInfo() {
        return "";
    }

    public void enable() {
        this.toggled = true;
        if (this.isEnabled() && !this.enableByDefault) {
            MinecraftForge.EVENT_BUS.register(this);
        }
        TempleClient.eventBus.addEventListener(this);

        if (mc.player != null) {
            this.onEnable();
            Notifications.addMessage(this.getName(), "Has been" + TextFormatting.GREEN + " enable", NotificationType.SUCCESS);
        } else {
            this.queueEnable = true;
        }
    }

    public void disable() {
        this.toggled = false;
        MinecraftForge.EVENT_BUS.unregister(this);
        TempleClient.eventBus.removeEventListener(this);

        if (mc.player != null) {
            this.onDisable();
            Notifications.addMessage(this.getName(), "Has been" + TextFormatting.RED + " disabled", NotificationType.ERROR);
        }
    }

    public void toggle() {
        this.setToggled(!this.isToggled());
    }

    public void setToggled(boolean toggled) {
        if (toggled == this.toggled) return;

        if (toggled) {
            this.enable();
        } else {
            this.disable();
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

    public enum Category {
        Chat,
        Combat,
        Misc,
        Movement,
        Player,
        Render,
        World,
        Client
    }
}