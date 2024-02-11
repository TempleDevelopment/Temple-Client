package xyz.templecheats.templeclient.api.setting;

import java.util.ArrayList;
import java.util.Objects;

public class SettingsManager {
    private final ArrayList<Setting> settings;
    
    public SettingsManager() {
        this.settings = new ArrayList<>();
    }
    
    public void rSetting(Setting in) {
        this.settings.add(in);
    }
    
    public ArrayList<Setting> getSettings() {
        return this.settings;
    }
    
    public ArrayList<Setting> getSettingsByMod(SettingHolder mod) {
        ArrayList<Setting> out = new ArrayList<>();
        for(Setting s : getSettings()) {
            if(s.getParentMod().equals(mod)) {
                out.add(s);
            }
        }
        if(out.isEmpty()) {
            return null;
        }
        return out;
    }
    
    public Setting getSettingByName(String mod, String name) {
        for(Setting set : getSettings()) {
            if(set.getName().equalsIgnoreCase(name) && Objects.equals(set.getParentMod().getName(), mod)) {
                return set;
            }
        }
        return null;
    }
    
}
