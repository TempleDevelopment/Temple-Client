package xyz.templecheats.templeclient.impl.modules.client;

import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.impl.modules.Module;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.impl.gui.clickgui.setting.Setting;
import xyz.templecheats.templeclient.impl.gui.clickgui.setting.SettingsManager;

import java.awt.*;

public class ClickGUI extends Module {
    private Setting Red;
    private Setting Green;
    private Setting Blue;
    private Setting Alpha;
    private Setting Rainbow;

    public static Color RGBColor;

    private int rainbowDelay = 0;

    public ClickGUI() {
        super("ClickGUI", Keyboard.KEY_NONE, Category.CLIENT);

        SettingsManager settingsManager = TempleClient.settingsManager;

        int defaultRedMain = 173;
        int defaultBlueMain = 216;
        int defaultGreenMain = 230;
        int defaultAlphaMain = 255;

        Rainbow = new Setting("Rainbow", this, false);
        Red = new Setting("Red", this, defaultRedMain, 0, 255, true);
        Green = new Setting("Green", this, defaultBlueMain, 0, 255, true);
        Blue = new Setting("Blue", this, defaultGreenMain, 0, 255, true);
        Alpha = new Setting("Alpha", this, defaultAlphaMain, 0, 255, true);


        settingsManager.rSetting(Rainbow);
        settingsManager.rSetting(Red);
        settingsManager.rSetting(Green);
        settingsManager.rSetting(Blue);
        settingsManager.rSetting(Alpha);


        RGBColor = new Color(defaultRedMain, defaultGreenMain, defaultBlueMain, defaultAlphaMain);
    }


    @Override
    public void onUpdate() {
        super.onUpdate();
        if (Rainbow.getValBoolean()) {
            int rainbowColor = rainbow(1);
            RGBColor = new Color((rainbowColor >> 16) & 0xFF, (rainbowColor >> 8) & 0xFF, rainbowColor & 0xFF, Alpha.getValInt());
        } else {
            RGBColor = new Color(Red.getValInt(), Green.getValInt(), Blue.getValInt(), Alpha.getValInt());
        }
    }

    public static int rainbow(int delay) {
        double rainbowState = Math.ceil((System.currentTimeMillis() + delay) / 20.0);
        rainbowState %= 360;
        return Color.getHSBColor((float) (rainbowState / 360.0f), 0.5f, 1f).getRGB();
    }
}
