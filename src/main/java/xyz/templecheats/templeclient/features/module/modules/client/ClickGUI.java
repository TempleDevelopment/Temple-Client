package xyz.templecheats.templeclient.features.module.modules.client;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.color.RainbowUtil;
import xyz.templecheats.templeclient.util.setting.impl.*;
import xyz.templecheats.templeclient.features.gui.clickgui.particles.Particle;
import xyz.templecheats.templeclient.features.gui.clickgui.particles.Snow;

import java.awt.*;

public class ClickGUI extends Module {
	/**
	 * Instance
	 */
	public static ClickGUI INSTANCE;
	public final Particle.Util particleUtil = new Particle.Util(100);
	public final Snow snow = new Snow(0, 0, 1, 1);
	private final RainbowUtil rainbowUtil = new RainbowUtil();

	/**
	 * Settings
	 */
	public final BooleanSetting gears = new BooleanSetting("Gears", this, true);
	public final BooleanSetting particles = new BooleanSetting("Particles", this, false);
	private final BooleanSetting rainbow = new BooleanSetting("Rainbow", this, false);
	private final BooleanSetting scaledResolution = new BooleanSetting("Scaled Resolution", this, false);
	private final ColorSetting startColor = new ColorSetting("Start Color", this, Color.CYAN);
	private final ColorSetting endColor = new ColorSetting("End Color", this, Color.CYAN);
	public final IntSetting scrollSpeed = new IntSetting("Scroll Speed", this, 0, 100, 10);
	public final DoubleSetting scale = new DoubleSetting("Scale", this, 0.5, 2, 0.8);
	public final EnumSetting < Theme > theme = new EnumSetting < > ("Theme", this, Theme.Default);

	/**
	 * Variables
	 */
	private int rainbowColor;

	public ClickGUI() {
		super("ClickGUI", "Screen to configure modules", Keyboard.KEY_RSHIFT, Category.Client);
		INSTANCE = this;

		this.registerSettings(gears, particles, rainbow, scaledResolution,
				endColor, startColor, scrollSpeed,
				scale,
				theme);
	}

	@Override
	public void onUpdateConstant() {
		if (this.rainbow.booleanValue()) {
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

	public Color getStartColor() {
		if (this.rainbow.booleanValue()) {
			return new Color(rainbowColor);
		}

		return startColor.getColor();
	}

	public Color getEndColor() {
		if (this.rainbow.booleanValue()) {
			return new Color(rainbowColor);
		}

		return endColor.getColor();
	}

	public enum Theme {
		Default,
		CSGO
	}
}