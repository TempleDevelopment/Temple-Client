package xyz.templecheats.templeclient.features.module.modules.client;

import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.gui.font.FontUtils;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;

public class Font extends Module {
	/**
	 * Instance
	 */
	public static Font INSTANCE;

	/**
	 * Settings
	 */
	private final EnumSetting<FontValue> mode = new EnumSetting<>("Font", this, FontValue.Smooth);

	/**
	 * Variables
	 */
	private FontValue lastMode = null;

	public Font() {
		super("Font", "Changes the ClickGUI font", Keyboard.KEY_NONE, Category.Client);
		INSTANCE = this;

		this.registerSettings(mode);
	}

	@Override
	public void onEnable() {
		//FontUtils.setCustomFont();
	}

	@Override
	public void onUpdateConstant() {
		if (mode.value() != lastMode) {
			switch (mode.value()) {
				case Smooth:
					FontUtils.setSmoothFont();
					break;
				case Bold:
					FontUtils.setBoldFont();
					break;
			}
			lastMode = mode.value();
		}
	}

	private enum FontValue {
		Smooth,
		Bold,
	}
}
