package xyz.templecheats.templeclient.features.module.modules.client;

import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.DiscordPresence;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.StringSetting;

public class RPC extends Module {
    /****************************************************************
     *                      Instances
     ****************************************************************/
    public static RPC INSTANCE;

    /****************************************************************
     *                      Settings
     ****************************************************************/
    public final StringSetting state = new StringSetting("State", this, "Temple Client 1.12.2");

    public RPC() {
        super("RPC", "Discord rich presence", Keyboard.KEY_NONE, Category.Client);
        INSTANCE = this;

        this.registerSettings(state);
    }

    @Override
    public void onEnable() {
        DiscordPresence.start();
    }

    @Override
    public void onDisable() {
        DiscordPresence.stop();
    }
}
