package xyz.templecheats.templeclient.util.setting;

import com.google.gson.JsonObject;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.items.Item;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.items.buttons.Button;

public abstract class Setting<T> {
    public final String name;
    public final SettingHolder parent;

    /****************************************************************
     *                  Constructor
     ****************************************************************/

    public Setting(String name, SettingHolder parent) {
        this.name = name;
        this.parent = parent;
    }

    /****************************************************************
     *                  Abstract Methods
     ****************************************************************/

    public abstract T value();

    public abstract void setValue(T value);

    public abstract void serialize(JsonObject jsonObject);

    public abstract void deserialize(JsonObject jsonObject);

    public abstract Item createBasicButton(Button parent);

    public abstract xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.Item createCsgoButton(xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.buttons.Button parent);
}
