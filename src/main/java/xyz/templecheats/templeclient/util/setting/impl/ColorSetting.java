package xyz.templecheats.templeclient.util.setting.impl;

import com.google.gson.JsonObject;
import java.awt.Color;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.items.buttons.Button;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.items.Item;
import xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.buttons.ColorButton;
import xyz.templecheats.templeclient.util.setting.Setting;
import xyz.templecheats.templeclient.util.setting.SettingHolder;

public class ColorSetting extends Setting<Color> {
    private Color value;
    public String name;

    public ColorSetting(String name, SettingHolder parent, Color defaultValue) {
        super(name, parent);
        this.name = name;
        this.value = defaultValue;
    }

    public String getName() {
        return this.name;
    }

    public Color getColor() {
        return this.value;
    }

    public void setColor(Color value) {
        this.value = value;
    }

    public void setAlpha(int alpha) {
        this.value = new Color(this.value.getRed(), this.value.getGreen(), this.value.getBlue(), Math.min(255, Math.max(0, alpha)));
    }

    @Deprecated
    @Override
    public Color value() {
        return this.value;
    }

    @Deprecated
    @Override
    public void setValue(Color value) {
        this.value = value;
    }

    @Override
    public void serialize(JsonObject jsonObject) {
        JsonObject colorObject = new JsonObject();
        colorObject.addProperty("red", this.value.getRed());
        colorObject.addProperty("green", this.value.getGreen());
        colorObject.addProperty("blue", this.value.getBlue());
        colorObject.addProperty("alpha", this.value.getAlpha());
        jsonObject.add(this.name, colorObject);
    }

    @Override
    public void deserialize(JsonObject jsonObject) {
        JsonObject colorObject = jsonObject.getAsJsonObject(this.name);
        int red = colorObject.get("red").getAsInt();
        int green = colorObject.get("green").getAsInt();
        int blue = colorObject.get("blue").getAsInt();
        int alpha = colorObject.get("alpha").getAsInt();

        this.value = new Color(red, green, blue, alpha);
    }

    @Override
    public Item createBasicButton(Button parent) {
        return new xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.items.buttons.ColorButton(this, parent);
    }

    @Override
    public xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.Item createCsgoButton(xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.buttons.Button parent) {
        return new ColorButton(this, parent);
    }
}
