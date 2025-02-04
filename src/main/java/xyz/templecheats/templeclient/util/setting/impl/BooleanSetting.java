package xyz.templecheats.templeclient.util.setting.impl;

import com.google.gson.JsonObject;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.items.Item;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.items.buttons.BooleanButton;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.items.buttons.Button;
import xyz.templecheats.templeclient.util.setting.Setting;
import xyz.templecheats.templeclient.util.setting.SettingHolder;

public class BooleanSetting extends Setting<Boolean> {

    private boolean value;

    /****************************************************************
     *                  Constructor
     ****************************************************************/

    public BooleanSetting(String name, SettingHolder parent, boolean defaultValue) {
        super(name, parent);
        this.value = defaultValue;
    }

    /****************************************************************
     *                  Public Methods
     ****************************************************************/

    public boolean booleanValue() {
        return this.value;
    }

    public void setBooleanValue(boolean value) {
        this.value = value;
    }

    @Deprecated
    @Override
    public Boolean value() {
        return this.value;
    }

    @Deprecated
    @Override
    public void setValue(Boolean value) {
        this.value = value;
    }

    @Override
    public void serialize(JsonObject jsonObject) {
        jsonObject.addProperty(this.name, this.value);
    }

    @Override
    public void deserialize(JsonObject jsonObject) {
        this.value = jsonObject.getAsJsonPrimitive(this.name).getAsBoolean();
    }

    @Override
    public Item createBasicButton(Button parent) {
        return new BooleanButton(this, parent);
    }

    @Override
    public xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.Item createCsgoButton(
            xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.buttons.Button parent) {
        return new xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.buttons.BooleanButton(this, parent);
    }
}
