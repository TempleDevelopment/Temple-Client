package xyz.templecheats.templeclient.features.module.modules.client;

import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.gui.font.FontUtils;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.*;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class FontSettings extends Module {
	/**
	 * Instance
	 */
	public static FontSettings INSTANCE;

	/**
	 * Settings
	 */
	private final EnumSetting <FontUtils.Fonts> mode = new EnumSetting < > ("Font", this, FontUtils.Fonts.DEFAULT);
	public final DoubleSetting shadowShift = new DoubleSetting("Shadow Shift", this, 1.0, 10.0, 4.5);
	private final DoubleSetting gapSize = new DoubleSetting("Gap", this, -10.0, 10.0, 1.65);
	private final DoubleSetting lineOffset = new DoubleSetting("Vertical Offset", this, -10.0, 10.0, 3.59);
	private final DoubleSetting lodBiasSetting = new DoubleSetting("Smoothing", this , -10.0, 10.0, -1.0);
	/*
	 * Variables
	 */
	private boolean reloadFont = false;
	public FontSettings() {
		super("Font", "Change TempleClient's font settings", Keyboard.KEY_NONE, Category.Client);
		INSTANCE = this;

		this.registerSettings(gapSize, lineOffset, mode);
	}

	public FontUtils.Fonts getFont() {
		return mode.value();
	}

	public float getOffset() {
		return this.isEnabled() ? lineOffset.floatValue() * 1.3f - 4.5f : 0;
	}

	public float getGapSize() {
		return this.isEnabled() ? gapSize.floatValue() * 0.5f - 0.8f : 0;
	}

	public static float getSize() {
		return 0.12f;
	}

	public float getGap() {
		return gapSize.floatValue() * 0.5f - 0.8f;
	}

	public float getLineSpace() {
		return (float) (getSize() * (0.8 * 0.05f + 0.77f));
	}

	public float getLodBias() {
		return lodBiasSetting.floatValue() * 0.25f - 0.5f;
	}

	public float getBaselineOffset() {
		return lineOffset.floatValue() * 2.0f - 4.5f;
	}

	@Override
	public void onEnable() {
		reloadFont = true;
	}

	@Override
	public void onUpdate() {
		if (reloadFont) {
			FontUtils.setupFonts();
			reloadFont = false;
		}
	}
}