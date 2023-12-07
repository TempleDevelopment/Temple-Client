package xyz.templecheats.templeclient.features.modules.client;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import org.lwjgl.Sys;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.features.modules.Module;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.gui.clickgui.setting.Setting;
import xyz.templecheats.templeclient.gui.clickgui.setting.SettingsManager;

import java.awt.*;

public class ClickGUI extends Module {
    private Setting Red;
    private Setting Green;
    private Setting Blue;
    private Setting Alpha;

    public static Color RGBColor;

    public ClickGUI() {
        super("ClickGUI", Keyboard.KEY_NONE, Category.CLIENT);

        SettingsManager settingsManager = TempleClient.instance.settingsManager;

        int defaultRedMain = 173;
        int defaultBlueMain = 216;
        int defaultGreenMain = 230;
        int defaultAlphaMain = 255;

        Red = new Setting("Red", this, defaultRedMain, 0, 255, true);
        Green = new Setting("Green", this, defaultBlueMain, 0, 255, true);
        Blue = new Setting("Blue", this, defaultGreenMain, 0, 255, true);
        Alpha = new Setting("Alpha", this, defaultAlphaMain, 0, 255, true);

        settingsManager.rSetting(Red);
        settingsManager.rSetting(Green);
        settingsManager.rSetting(Blue);
        settingsManager.rSetting(Alpha);

        RGBColor = new Color(defaultRedMain, defaultGreenMain, defaultBlueMain, defaultAlphaMain);
    }


    @Override
    public void onUpdate() {
        super.onUpdate();
        RGBColor = new Color(Red.getValInt(), Green.getValInt(), Blue.getValInt(), Alpha.getValInt());
    }
}