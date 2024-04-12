package xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties;

import xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.Item;

import java.util.List;

public interface IContainer {
    List<Item> getItems();
    void setItems(List<Item> items);
}
