package xyz.templecheats.templeclient.impl.modules.client;

import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.impl.gui.clickgui.setting.Setting;
import xyz.templecheats.templeclient.impl.gui.font.FontUtils;
import xyz.templecheats.templeclient.impl.modules.Module;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

public class Font extends Module {
    private String lastMode = "";

    public Font() {
        super("Font", Keyboard.KEY_NONE, Category.CLIENT);
        ArrayList<String> options = new ArrayList<>();
        options.add("Custom");
        options.add("Default");
        TempleClient.settingsManager.rSetting(new Setting("Mode", this, options, "Custom"));
    }

    @Override
    public void onEnable() {
        FontUtils.setCustomFont();
    }

    @Override
    public void onUpdate() {
        String currentMode = TempleClient.settingsManager.getSettingByName(this.name, "Mode").getValString();
        if (!currentMode.equals(lastMode)) {
            switch (currentMode) {
                case "Custom":
                    FontUtils.setCustomFont();
                    break;
                case "Default":
                    FontUtils.setDefaultFont();
                    break;
            }
            lastMode = currentMode;
        }
    }
}
