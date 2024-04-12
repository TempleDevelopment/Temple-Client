package xyz.templecheats.templeclient.util.setting.impl;

import com.google.gson.JsonObject;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.items.Item;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.items.buttons.Button;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.items.buttons.EnumButton;
import xyz.templecheats.templeclient.util.setting.Setting;
import xyz.templecheats.templeclient.util.setting.SettingHolder;

public class EnumSetting < T extends Enum < T >> extends Setting < T > {
    private T value;

    public EnumSetting(String name, SettingHolder parent, T defaultValue) {
        super(name, parent);
        this.value = defaultValue;
    }

    public T[] getValues() {
        return this.value.getDeclaringClass().getEnumConstants();
    }

    public int index() {
        T[] values = getValues();
        for (int i = 0; i < values.length; i++) {
            if (values[i] == this.value) return i;
        }
        throw new IllegalStateException();
    }

    @Override
    public T value() {
        return this.value;
    }

    @Override
    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public void serialize(JsonObject jsonObject) {
        jsonObject.addProperty(this.name, this.value.name());
    }

    @Override
    public void deserialize(JsonObject jsonObject) {
        String valueName = jsonObject.getAsJsonPrimitive(this.name).getAsString();
        for (T enumValue: this.value.getDeclaringClass().getEnumConstants()) {
            if (enumValue.name().equals(valueName)) {
                this.value = enumValue;
                return;
            }
        }
    }

    @Override
    public Item createBasicButton(Button parent) {
        return new EnumButton< >(this, parent);
    }

    @Override
    public xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.Item createCsgoButton(xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.buttons.Button parent) {
        return new xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.buttons.enumB.EnumButton< >(this, parent);
    }
}