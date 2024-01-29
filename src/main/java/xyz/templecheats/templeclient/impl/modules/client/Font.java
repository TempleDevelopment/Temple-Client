package xyz.templecheats.templeclient.impl.modules.client;

import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.impl.gui.font.FontUtils;
import xyz.templecheats.templeclient.impl.gui.setting.Setting;
import xyz.templecheats.templeclient.impl.modules.Module;

import java.util.ArrayList;
import java.util.Arrays;

public class Font extends Module {
	/**
	 * Instance
	 */
	public static Font INSTANCE;
	
	/**
	 * Settings
	 */
	private final Setting mode = new Setting("Mode", this, new ArrayList<>(Arrays.asList("Arial", "Italic")), "Arial");
	
	/**
	 * Variables
	 */
	private String lastMode = "";
	
	public Font() {
		super("Font", "Changes the ClickGUI font", Keyboard.KEY_NONE, Category.CLIENT);
		INSTANCE = this;
		
		this.registerSettings(mode);
	}
	
	@Override
	public void onEnable() {
		//FontUtils.setCustomFont();
	}
	
	@Override
	public void onUpdateConstant() {
		final String currentMode = this.mode.getValString();
		if(!currentMode.equals(lastMode)) {
			switch(currentMode) {
				case "Arial":
					FontUtils.setArialFont();
					break;
				case "Italic":
					FontUtils.setItalicFont();
					break;
			}
			lastMode = currentMode;
		}
	}
}
