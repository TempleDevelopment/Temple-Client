package xyz.templecheats.templeclient.util.setting;

import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.features.module.Module;

public class SettingHolder {

    private final String name;

    /****************************************************************
     *                  Constructor
     ****************************************************************/

    public SettingHolder(String name) {
        this.name = name;
    }

    /****************************************************************
     *                  Public Methods
     ****************************************************************/

    public static Class<? extends Module>[] modules() {
        return new Class[0];
    }

    public String getName() {
        return this.name;
    }

    /****************************************************************
     *                  Protected Methods
     ****************************************************************/

    protected void registerSettings(Setting... settings) {
        for (Setting setting : settings) {
            TempleClient.settingsManager.rSetting(setting);
        }
    }
}
