package xyz.templecheats.templeclient.util.setting;

import com.google.gson.JsonObject;

public abstract class Setting<T> {
    public final String name;
    public final SettingHolder parent;
    public boolean visible;

    public Setting(String name, SettingHolder parent) {
        this.name = name;
        this.parent = parent;
    }

    public abstract T value();
    public abstract void setValue(T value);

    public abstract void serialize(JsonObject jsonObject);
    public abstract void deserialize(JsonObject jsonObject);

    public abstract xyz.templecheats.templeclient.features.gui.clickgui.basic.Item createBasicButton(xyz.templecheats.templeclient.features.gui.clickgui.basic.Button parent);
    public abstract xyz.templecheats.templeclient.features.gui.clickgui.csgo.Item createCsgoButton(xyz.templecheats.templeclient.features.gui.clickgui.csgo.Button parent);
}
