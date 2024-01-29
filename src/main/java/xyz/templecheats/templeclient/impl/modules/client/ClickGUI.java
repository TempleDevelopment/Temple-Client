package xyz.templecheats.templeclient.impl.modules.client;

import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.impl.gui.setting.Setting;
import xyz.templecheats.templeclient.impl.modules.Module;

import java.awt.*;

public class ClickGUI extends Module {
	/**
	 * Instance
	 */
	public static ClickGUI INSTANCE;
	
	/**
	 * Settings
	 */
	public final Setting scale = new Setting("Scale", this, 1, 0.5, 2, false);
	public final Setting scrollSpeed = new Setting("Scroll Speed", this, 10, 0, 100, true);
	
	private final Setting rainbow = new Setting("Rainbow", this, false);
	
	private final Setting startRed = new Setting("Start Red", this, 0, 0, 255, true);
	private final Setting startGreen = new Setting("Start Green", this, 123, 0, 255, true);
	private final Setting startBlue = new Setting("Start Blue", this, 230, 0, 255, true);
	private final Setting startAlpha = new Setting("Start Alpha", this, 255, 0, 255, true);
	
	private final Setting endRed = new Setting("End Red", this, 74, 0, 255, true);
	private final Setting endGreen = new Setting("End Green", this, 216, 0, 255, true);
	private final Setting endBlue = new Setting("End Blue", this, 230, 0, 255, true);
	private final Setting endAlpha = new Setting("End Alpha", this, 255, 0, 255, true);
	
	/**
	 * Variables
	 */
	private int rainbowColor;
	
	public ClickGUI() {
		super("ClickGUI", "Customize your ClickGUI", Keyboard.KEY_NONE, Category.CLIENT);
		INSTANCE = this;
		
		this.registerSettings(scale, scrollSpeed, rainbow, startRed, startGreen, startBlue, startAlpha, endRed, endGreen, endBlue, endAlpha);
	}
	
	@Override
	public void onUpdateConstant() {
		if(this.rainbow.getValBoolean()) {
			double rainbowState = Math.ceil((System.currentTimeMillis() + 1) / 20.0);
			rainbowState %= 360;
			rainbowColor = Color.getHSBColor((float) (rainbowState / 360.0f), 0.5f, 1f).getRGB();
		}
	}
	
	public int getStartColor() {
		if(this.rainbow.getValBoolean()) {
			return startAlpha.getValInt() << 24 | (rainbowColor >> 16 & 0xFF) << 16 | (rainbowColor >> 8 & 0xFF) << 8 | (rainbowColor & 0xFF);
		}
		
		return startAlpha.getValInt() << 24 | startRed.getValInt() << 16 | startGreen.getValInt() << 8 | startBlue.getValInt();
	}
	
	public int getEndColor() {
		if(this.rainbow.getValBoolean()) {
			return endAlpha.getValInt() << 24 | (rainbowColor >> 16 & 0xFF) << 16 | (rainbowColor >> 8 & 0xFF) << 8 | (rainbowColor & 0xFF);
		}
		
		return endAlpha.getValInt() << 24 | endRed.getValInt() << 16 | endGreen.getValInt() << 8 | endBlue.getValInt();
	}
}
