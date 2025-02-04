package xyz.templecheats.templeclient.features.module.modules.render;

import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.manager.ModuleManager;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.ColorSetting;

import java.awt.*;

public class EnchantColor extends Module {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    public final ColorSetting enchantColor = new ColorSetting("Enchant Color", this, Color.WHITE);
    public final BooleanSetting rainbow = new BooleanSetting("Rainbow", this, false);

    public EnchantColor() {
        super("EnchantColor", "Makes the enchant glint colorful", Keyboard.KEY_NONE, Category.Render);
        this.registerSettings(rainbow, enchantColor);
    }

    public static Color getColor() {
        EnchantColor enchantColorModule = (EnchantColor) ModuleManager.getModule(EnchantColor.class);
        if (enchantColorModule.rainbow.booleanValue()) {
            return Color.getHSBColor(System.currentTimeMillis() % 10000L / 10000.0f, 0.8f, 0.8f);
        } else {
            Color color = enchantColorModule.enchantColor.getColor();
            return new Color(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);
        }
    }
}