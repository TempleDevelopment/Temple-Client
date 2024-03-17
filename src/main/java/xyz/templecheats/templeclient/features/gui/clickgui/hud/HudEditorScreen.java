package xyz.templecheats.templeclient.features.gui.clickgui.hud;

import xyz.templecheats.templeclient.features.gui.clickgui.basic.ClientGuiScreen;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.Panel;
import xyz.templecheats.templeclient.features.module.modules.client.HUD;

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

    protected double getScale() {
        return HUD.INSTANCE.hudScale.doubleValue();
    }
}
