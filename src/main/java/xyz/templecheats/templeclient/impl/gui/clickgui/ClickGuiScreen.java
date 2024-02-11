package xyz.templecheats.templeclient.impl.gui.clickgui;

import xyz.templecheats.templeclient.ModuleManager;
import xyz.templecheats.templeclient.impl.gui.clickgui.item.ModuleButton;
import xyz.templecheats.templeclient.impl.modules.Module;

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
                        if(module.getCategory() == category) {
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

