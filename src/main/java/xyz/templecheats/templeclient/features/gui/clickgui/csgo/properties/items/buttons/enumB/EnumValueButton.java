package xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.buttons.enumB;

public class EnumValueButton<T extends Enum<T>> extends NormalButton {
    public final T value;

    public EnumValueButton(T value) {
        super(value.toString());
        this.value = value;
    }
}
