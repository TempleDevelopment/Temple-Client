package xyz.templecheats.templeclient.util.setting;

import it.unimi.dsi.fastutil.objects.ReferenceArrayList;

import javax.annotation.Nullable;
import java.util.List;

public class SettingsManager {
    private final List < Setting < ? >> settings = new ReferenceArrayList < > ();

    public void rSetting(Setting < ? > in) {
        this.settings.add(in);
    }

    public List < Setting < ? >> getSettings() {
        return this.settings;
    }

    public List < Setting < ? >> getSettingsByMod(SettingHolder mod) {
        List < Setting < ? >> out = new ReferenceArrayList < > ();
        for (Setting < ? > s : getSettings()) {
            if (s.parent == mod) out.add(s);
        }
        return out;
    }

    /**
     * @deprecated access the setting field directly instead
     */
    @Deprecated
    @Nullable
    public Setting < ? > getSetting(SettingHolder mod, String name) {
        for (Setting < ? > set : getSettings()) {
            if (set.parent == mod && set.name.equals(name)) return set;
        }
        return null;
    }

    /**
     * @deprecated access the setting field directly instead
     */
    @Deprecated
    @Nullable
    public Setting < ? > getSettingByName(String mod, String name) {
        for (Setting < ? > set : getSettings()) {
            if (set.name.equalsIgnoreCase(name) && set.parent.getName().equalsIgnoreCase(mod)) {
                return set;
            }
        }
        return null;
    }
}