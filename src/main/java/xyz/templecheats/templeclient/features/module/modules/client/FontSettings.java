package xyz.templecheats.templeclient.features.module.modules.client;

import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.gui.font.CFont;
import xyz.templecheats.templeclient.features.gui.font.FontUtils;
import xyz.templecheats.templeclient.features.gui.font.Fonts;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.*;

public class FontSettings extends Module {
	/**
	 * Instance
	 */
	public static FontSettings INSTANCE;

	/**
	 * Settings
	 */
	private final EnumSetting <FontUtils.Fonts> mode = new EnumSetting < > ("Font", this, FontUtils.Fonts.Default);
	private final DoubleSetting gapSize = new DoubleSetting("Gap", this, -10.0, 10.0, 1.65);
	private final DoubleSetting lineOffset = new DoubleSetting("Vertical Offset", this, -10.0, 10.0, 3.59);
	public final BooleanSetting smooth = new BooleanSetting("Smooth", this, true);
	/*
	 * Variables
	 */
	private boolean reloadFont = false;

	public FontSettings() {
		super("Font", "Change TempleClient's font settings", Keyboard.KEY_NONE, Category.Client);
		INSTANCE = this;

		this.registerSettings(smooth, gapSize, lineOffset, mode);
		this.setToggled(true);
	}

	public static CFont getFont(int size) {
		return FontSettings.INSTANCE.mode.value().setSize(size);
	}

	public static CFont getIcon(int size) {
		return FontUtils.Icons.ICON.setSize(size);
	}

	public float getOffset() {
		return this.isEnabled() ? lineOffset.floatValue() * 1.3f - 4.5f : 0;
	}

	public float getGapSize() {
		return this.isEnabled() ? gapSize.floatValue() * 0.5f - 0.8f : 0;
	}

	@Override
	public void onEnable() {
		reloadFont = true;
	}

	@Override
	public void onUpdate() {
		if (reloadFont) {
			FontUtils.setupIcons();
			FontUtils.setupFonts();
			reloadFont = false;
		}
		fontReload();
	}

	// SHIT :skull:
	private void fontReload() {
		Fonts.font12 = FontSettings.getFont(12);
		Fonts.font14 = FontSettings.getFont(14);
		Fonts.font16 = FontSettings.getFont(16);
		Fonts.font18 = FontSettings.getFont(18);
		Fonts.font20 = FontSettings.getFont(20);
		Fonts.font22 = FontSettings.getFont(22);
		Fonts.font24 = FontSettings.getFont(24);

		// Don't need this at all, but maybe will add more font icon style for this
		Fonts.icon12 = FontSettings.getIcon(12);
		Fonts.icon14 = FontSettings.getIcon(14);
		Fonts.icon16 = FontSettings.getIcon(16);
		Fonts.icon18 = FontSettings.getIcon(18);
		Fonts.icon20 = FontSettings.getIcon(20);
		Fonts.icon22 = FontSettings.getIcon(22);
		Fonts.icon24 = FontSettings.getIcon(24);
		Fonts.icon26 = FontSettings.getIcon(26);
		Fonts.icon28 = FontSettings.getIcon(28);
		Fonts.icon30 = FontSettings.getIcon(30);
		Fonts.icon32 = FontSettings.getIcon(32);
		Fonts.icon34 = FontSettings.getIcon(34);
		Fonts.icon40 = FontSettings.getIcon(40);
		Fonts.icon46 = FontSettings.getIcon(46);
	}
}