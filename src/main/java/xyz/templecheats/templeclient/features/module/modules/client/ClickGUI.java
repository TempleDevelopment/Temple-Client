package xyz.templecheats.templeclient.features.module.modules.client;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.color.RainbowUtil;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;
import xyz.templecheats.templeclient.features.gui.clickgui.particles.Particle;
import xyz.templecheats.templeclient.features.gui.clickgui.particles.Snow;

public class ClickGUI extends Module {
	/**
	 * Instance
	 */
	public static ClickGUI INSTANCE;
	public final Particle.Util particleUtil = new Particle.Util(100);
	public final Snow snow = new Snow(0, 0, 1, 1);
	private RainbowUtil rainbowUtil = new RainbowUtil();

	/**
	 * Settings
	 */
	public final DoubleSetting scale = new DoubleSetting("Scale", this, 0.5, 2, 0.8);
	public final IntSetting scrollSpeed = new IntSetting("Scroll Speed", this, 0, 100, 10);
	private final BooleanSetting rainbow = new BooleanSetting("Rainbow", this, false);
	public final BooleanSetting particles = new BooleanSetting("Particles", this, false);
	public final BooleanSetting scaledResolution = new BooleanSetting("Scaled Resolution", this, false);
	public final EnumSetting<Theme> theme = new EnumSetting<>("Theme", this, Theme.Default);
	public final BooleanSetting gears = new BooleanSetting("Gears", this, true);
	private final IntSetting startRed = new IntSetting("Start Red", this, 255, 0, 0);
	private final IntSetting startGreen = new IntSetting("Start Green", this, 0, 255, 123);
	private final IntSetting startBlue = new IntSetting("Start Blue", this, 0, 255, 230);
	private final IntSetting startAlpha = new IntSetting("Start Alpha", this, 0, 255, 255);
	private final IntSetting endRed = new IntSetting("End Red", this, 0, 255, 74);
	private final IntSetting endGreen = new IntSetting("End Green", this, 0, 255, 216);
	private final IntSetting endBlue = new IntSetting("End Blue", this, 0, 255, 230);
	private final IntSetting endAlpha = new IntSetting("End Alpha", this, 0, 255, 255);

	/**
	 * Variables
	 */
	private int rainbowColor;

	public ClickGUI() {
		super("ClickGUI", "Customize your ClickGUI", Keyboard.KEY_RSHIFT, Category.Client);
		INSTANCE = this;

		this.registerSettings(rainbow, particles, gears, scaledResolution, scrollSpeed, scale, startRed, endRed, startGreen, endGreen, startBlue, endBlue, startAlpha, endAlpha, theme);
	}


	@Override
	public void onUpdateConstant() {
		if(this.rainbow.booleanValue()) {
			rainbowUtil.updateRainbow();
			rainbowColor = rainbowUtil.getRainbowColor();
		}
		if (this.scaledResolution.booleanValue()) {
			double scaleFactor = Display.getHeight() / 1080.0;

			scale.setDoubleValue(scaleFactor);
		}
	}

	public int getStartColor() {
		if(this.rainbow.booleanValue()) {
			return startAlpha.intValue() << 24 | (rainbowColor >> 16 & 0xFF) << 16 | (rainbowColor >> 8 & 0xFF) << 8 | (rainbowColor & 0xFF);
		}

		return startAlpha.intValue() << 24 | startRed.intValue() << 16 | startGreen.intValue() << 8 | startBlue.intValue();
	}

	public int getEndColor() {
		if(this.rainbow.booleanValue()) {
			return endAlpha.intValue() << 24 | (rainbowColor >> 16 & 0xFF) << 16 | (rainbowColor >> 8 & 0xFF) << 8 | (rainbowColor & 0xFF);
		}

		return endAlpha.intValue() << 24 | endRed.intValue() << 16 | endGreen.intValue() << 8 | endBlue.intValue();
	}

	public enum Theme {
		Default,
		CSGO
	}
}