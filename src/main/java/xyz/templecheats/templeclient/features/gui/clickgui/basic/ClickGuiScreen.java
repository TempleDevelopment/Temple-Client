package xyz.templecheats.templeclient.features.gui.clickgui.basic;

import xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.Panel;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.items.buttons.ModuleButton;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.manager.ModuleManager;

public class ClickGuiScreen extends ClientGuiScreen {
    private static ClickGuiScreen instance;
    
    @Override
    public void load() {
        this.getPanels().clear();
        
        int x = -42;
        for(final Module.Category category : Module.Category.values()) {
            this.getPanels().add(new Panel(category.name(), x += 90, 25, true) {
                @Override
                public void setupItems() {
                    ModuleManager.getModules().forEach(module -> {
                        if(module.getCategory() == category && !module.submodule) {
                            this.addButton(new ModuleButton(module));
                        }
                    });
                }
            });
        }
        
        super.load();
    }
    
    public static ClickGuiScreen getInstance() {
        return instance == null ? (instance = new ClickGuiScreen()) : instance;
    }
}

