package xyz.templecheats.templeclient.util.setting.impl;

import com.google.gson.JsonObject;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.items.Item;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.items.buttons.Button;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.items.buttons.IntSlider;
import xyz.templecheats.templeclient.util.setting.Setting;
import xyz.templecheats.templeclient.util.setting.SettingHolder;

public class IntSetting extends Setting<Integer> {
    public final int min;
    public final int max;
    private int value;

    /****************************************************************
     *                  Constructor
     ****************************************************************/

    public IntSetting(String name, SettingHolder parent, int min, int max, int defaultValue) {
        super(name, parent);
        this.min = min;
        this.max = max;
        this.value = defaultValue;
    }

    /****************************************************************
     *                  Public Methods
     ****************************************************************/

    public int intValue() {
        return this.value;
    }

    public void setIntValue(int value) {
        this.value = value;
    }

    @Deprecated
    @Override
    public Integer value() {
        return this.value;
    }

    @Deprecated
    @Override
    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public void serialize(JsonObject jsonObject) {
        jsonObject.addProperty(this.name, this.value);
    }

    @Override
    public void deserialize(JsonObject jsonObject) {
        this.value = jsonObject.getAsJsonPrimitive(this.name).getAsInt();
    }

    @Override
    public Item createBasicButton(Button parent) {
        return new IntSlider(this, parent);
    }

    @Override
    public xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.Item createCsgoButton(
            xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.buttons.Button parent) {
        return new xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.buttons.slider.IntSlider(this, parent);
    }
}
