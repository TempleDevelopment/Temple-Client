package xyz.templecheats.templeclient.features.gui.clickgui.basic.panels;

import xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.items.Item;

import java.util.List;

public interface IContainer {
    List<Item> getItems();

    void setItems(List<Item> items);
}
