package xyz.templecheats.templeclient.impl.gui.clickgui;

import xyz.templecheats.templeclient.impl.gui.clickgui.item.HudElementButton;
import xyz.templecheats.templeclient.impl.modules.client.HUD;

public class HudEditorScreen extends ClientGuiScreen {
    private static HudEditorScreen instance;
    
    @Override
    public void load() {
        this.getPanels().clear();
        
        this.getPanels().add(new Panel("Hud Editor", 200, 100, true) {
            @Override
            public void setupItems() {
                for(HUD.HudElement element : HUD.INSTANCE.getHudElements()) {
                    this.addButton(new HudElementButton(element));
                }
            }
        });
        
        super.load();
    }
    
    public static HudEditorScreen getInstance() {
        return instance == null ? (instance = new HudEditorScreen()) : instance;
    }
}
