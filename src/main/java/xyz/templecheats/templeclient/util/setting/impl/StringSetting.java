package xyz.templecheats.templeclient.util.setting.impl;

import com.google.gson.JsonObject;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.items.Item;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.items.buttons.Button;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.items.buttons.StringButton;
import xyz.templecheats.templeclient.util.setting.Setting;
import xyz.templecheats.templeclient.util.setting.SettingHolder;

public class StringSetting extends Setting<String> {
    private String value;
    private final String name;

    public StringSetting(String name, SettingHolder parent, String value) {
        super(name, parent);
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public String getStringValue() {
        return this.value;
    }

    @Override
    public String value() {
        return this.value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void serialize(JsonObject jsonObject) {
        JsonObject stringObject = new JsonObject();
        stringObject.addProperty("string", this.getStringValue().replace(" ", "_"));
        jsonObject.add(this.name, stringObject);
    }

    @Override
    public void deserialize(JsonObject jsonObject) {
        JsonObject stringObject = jsonObject.getAsJsonObject(this.name);
        this.value = stringObject.get("string").getAsString();
    }

    @Override
    public Item createBasicButton(Button parent) {
        return new StringButton(name, parent, this);
    }

    @Override
    public xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.Item createCsgoButton(xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.buttons.Button parent) {
        return new xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.buttons.StringButton(name, parent, this);
    }
}
