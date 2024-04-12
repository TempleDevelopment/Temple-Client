package xyz.templecheats.templeclient.util.setting.impl;

import com.google.gson.JsonObject;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.items.Item;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.items.buttons.Button;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.items.buttons.DoubleSlider;
import xyz.templecheats.templeclient.util.setting.Setting;
import xyz.templecheats.templeclient.util.setting.SettingHolder;

public class DoubleSetting extends Setting < Double > {
    public final double min,
            max;
    private double value;

    public DoubleSetting(String name, SettingHolder parent, double min, double max, double defaultValue) {
        super(name, parent);
        this.min = min;
        this.max = max;
        this.value = defaultValue;
    }

    public double doubleValue() {
        return this.value;
    }

    public float floatValue() {
        return (float) this.value;
    }

    public void setDoubleValue(double value) {
        this.value = value;
    }

    @Deprecated
    @Override
    public Double value() {
        return this.value;
    }

    @Deprecated
    @Override
    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public void serialize(JsonObject jsonObject) {
        jsonObject.addProperty(this.name, this.value);
    }

    @Override
    public void deserialize(JsonObject jsonObject) {
        this.value = jsonObject.getAsJsonPrimitive(this.name).getAsDouble();
    }

    @Override
    public Item createBasicButton(Button parent) {
        return new DoubleSlider(this, parent);
    }

    @Override
    public xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.Item createCsgoButton(xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.buttons.Button parent) {
        return new xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.buttons.slider.DoubleSlider(this, parent);
    }
}