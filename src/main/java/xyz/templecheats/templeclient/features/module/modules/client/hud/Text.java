package xyz.templecheats.templeclient.features.module.modules.client.hud;

import net.minecraft.client.gui.ScaledResolution;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.features.module.modules.client.HUD;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;
import xyz.templecheats.templeclient.util.setting.impl.StringSetting;

import static xyz.templecheats.templeclient.features.gui.font.Fonts.font18;

public class Text extends HUD.HudElement {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    private final StringSetting string = new StringSetting("Text", this, "Hello World!");
    private final EnumSetting<Effects> mode = new EnumSetting<>("Mode", this, Effects.Static);

    /****************************************************************
     *                      Variables
     ****************************************************************/
    private int counter = 0;
    private long lastFrameTime = 0;

    public Text() {
        super("Text", "Shows custom text in the HUD");
        registerSettings(string, mode);
    }

    @Override
    public void renderElement(ScaledResolution sr) {
        final String text = string.getStringValue();
        this.setWidth(font18.getStringWidth(text));
        this.setHeight(font18.getFontHeight());

        switch (mode.value()) {
            case Static:
                font18.drawString(text, getX(), getY(), ClickGUI.INSTANCE.getStartColor().getRGB(), true);
                break;
            case Typewriter:
                String typewriterText = text.substring(0, Math.min(counter, text.length()));
                font18.drawString(typewriterText, getX(), getY(), ClickGUI.INSTANCE.getStartColor().getRGB(), true);
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastFrameTime >= 300) {
                    if (counter < text.length()) {
                        counter++;
                    } else {
                        counter = 0;
                    }
                    lastFrameTime = currentTime;
                }
                break;
        }
    }

    public enum Effects {
        Static,
        Typewriter
    }
}