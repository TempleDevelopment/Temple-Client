package xyz.templecheats.templeclient.features.module.modules.client;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import xyz.templecheats.templeclient.features.gui.clickgui.particles.Particle;
import xyz.templecheats.templeclient.features.gui.clickgui.particles.Snow;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.render.shader.RainbowUtil;
import xyz.templecheats.templeclient.util.setting.impl.*;

import java.awt.*;

public class ClickGUI extends Module {
    /****************************************************************
     *                      Instances
     ****************************************************************/
    public static ClickGUI INSTANCE;
    public final Particle.Util particleUtil = new Particle.Util(100);
    public final Snow snow = new Snow(0, 0, 1, 1);
    private final RainbowUtil rainbowUtil = new RainbowUtil();

    /****************************************************************
     *                      Settings
     ****************************************************************/
    public final BooleanSetting gears = new BooleanSetting("Gears", this, true);
    public final BooleanSetting particles = new BooleanSetting("Particles", this, false);
    public final BooleanSetting tint = new BooleanSetting("Tint", this, true);
    public final BooleanSetting blur = new BooleanSetting("Blur", this, false);
    public final DoubleSetting radius = new DoubleSetting("Radius", this, 0.0, 10.0, 1.0);
    public final DoubleSetting compression = new DoubleSetting("Compression", this, 0.0, 10.0, 1.0);
    private final BooleanSetting scaledResolution = new BooleanSetting("Scaled Resolution", this, false);
    public final EnumSetting<ColorMode> colorMode = new EnumSetting<>("Color Mode", this, ColorMode.Default);
    public final EnumSetting<Way> way = new EnumSetting<>("Way", this, Way.Horizontal);
    private final ColorSetting startColor = new ColorSetting("Start Color", this, Color.CYAN);
    private final ColorSetting endColor = new ColorSetting("End Color", this, Color.CYAN);
    public final IntSetting scrollSpeed = new IntSetting("Scroll Speed", this, 0, 100, 10);
    public final DoubleSetting scale = new DoubleSetting("Scale", this, 0.5, 2, 0.8);
    public final EnumSetting<Theme> theme = new EnumSetting<>("Theme", this, Theme.Default);
    public final BooleanSetting showKey = new BooleanSetting("Show Bind", this, true);
    public final BooleanSetting description = new BooleanSetting("Description", this, true);

    /****************************************************************
     *                      Variables
     ****************************************************************/
    private int rainbowColor;

    public ClickGUI() {
        super("ClickGUI", "Screen to configure modules", Keyboard.KEY_RSHIFT, Category.Client);
        INSTANCE = this;

        this.registerSettings(
                description, gears, particles, tint, blur, scaledResolution, showKey,
                scrollSpeed,
                radius, compression, scale,
                startColor, endColor,
                colorMode, way, theme
        );
    }

    @Override
    public void onUpdateConstant() {
        if (colorMode.value() == ColorMode.Rainbow) {
            rainbowUtil.updateRainbow();
            rainbowColor = rainbowUtil.getRainbowColor();
        }
        if (this.scaledResolution.booleanValue()) {
            double scaleFactor = Display.getHeight() / 1080.0;

            scale.setDoubleValue(scaleFactor);
        }
    }

    public int getClientColor(int offset) {
        return rainbowUtil.rainbowProgress(5, offset * 200, getStartColor().getRGB(), getEndColor().getRGB());
    }

    public int getClientColor(int speed, int offset) {
        return rainbowUtil.rainbowProgress(speed, offset * 200, getStartColor().getRGB(), getEndColor().getRGB());
    }

    public Color getStartColor() {
        if (colorMode.value() == ColorMode.Rainbow) {
            return new Color(rainbowColor);
        }

        return startColor.getColor();
    }

    public Color getEndColor() {
        if (colorMode.value() == ColorMode.Rainbow) {
            return new Color(rainbowColor);
        }

        return endColor.getColor();
    }

    public enum ColorMode {
        Default,
        Animated,
        Gradient,
        Rainbow
    }

    public enum Way {
        Horizontal,
        Vertical
    }

    public enum Theme {
        Default,
        CSGO
    }
}
