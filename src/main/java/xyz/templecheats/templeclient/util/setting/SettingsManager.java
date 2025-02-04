package xyz.templecheats.templeclient.util.setting;

import it.unimi.dsi.fastutil.objects.ReferenceArrayList;

import javax.annotation.Nullable;
import java.util.List;

public class SettingsManager {

    private final List<Setting<?>> settings = new ReferenceArrayList<>();

    /****************************************************************
     *                      Public Methods
     ****************************************************************/

    /**
     * Registers a new setting.
     *
     * @param setting The setting to register.
     */
    public void rSetting(Setting<?> setting) {
        this.settings.add(setting);
    }

    /**
     * Returns the list of all settings.
     *
     * @return The list of settings.
     */
    public List<Setting<?>> getSettings() {
        return this.settings;
    }

    /**
     * Returns the list of settings for a specific module.
     *
     * @param mod The module to get settings for.
     * @return The list of settings for the module.
     */
    public List<Setting<?>> getSettingsByMod(SettingHolder mod) {
        List<Setting<?>> out = new ReferenceArrayList<>();
        for (Setting<?> setting : getSettings()) {
            if (setting.parent == mod) {
                out.add(setting);
            }
        }
        return out;
    }

    /****************************************************************
     *                      Deprecated Methods
     ****************************************************************/

    /**
     * @deprecated Access the setting field directly instead.
     */
    @Deprecated
    @Nullable
    public Setting<?> getSetting(SettingHolder mod, String name) {
        for (Setting<?> setting : getSettings()) {
            if (setting.parent == mod && setting.name.equals(name)) {
                return setting;
            }
        }
        return null;
    }

    /**
     * @deprecated Access the setting field directly instead.
     */
    @Deprecated
    @Nullable
    public Setting<?> getSettingByName(String mod, String name) {
        for (Setting<?> setting : getSettings()) {
            if (setting.name.equalsIgnoreCase(name) && setting.parent.getName().equalsIgnoreCase(mod)) {
                return setting;
            }
        }
        return null;
    }
}
