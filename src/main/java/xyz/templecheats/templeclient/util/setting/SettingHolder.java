package xyz.templecheats.templeclient.util.setting;

import xyz.templecheats.templeclient.TempleClient;

public class SettingHolder {
    private final String name;
    
    public SettingHolder(String name) {
        this.name = name;
    }
    
    protected void registerSettings(Setting... settings) {
        for(Setting setting : settings) {
            TempleClient.settingsManager.rSetting(setting);
        }
    }
    
    public String getName() {
        return this.name;
    }
}
