package xyz.templecheats.templeclient.features.gui.font;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.InputStream;

public class FontUtils {
	public static MinecraftFontRenderer normal;
	public static MinecraftFontRenderer csgoNormal;
	public static MinecraftFontRenderer csgoBoldNormal;

	private static Font smoothFont;
	private static Font boldFont;
	private static Font csgoFont;
	private static Font csgoBoldFont;

	static {
		bootstrap();
	}

	public static void bootstrap() {
		smoothFont = loadFont("smooth.ttf", 23);
		boldFont = loadFont("bold.ttf", 25);
		csgoFont = loadFont("FSEX300.ttf", 20);
		csgoBoldFont = loadFont("calibri-bold.ttf", 20);

		normal = new MinecraftFontRenderer(boldFont, true, true);
		csgoNormal = new MinecraftFontRenderer(csgoFont, true, true);
		csgoBoldNormal = new MinecraftFontRenderer(csgoBoldFont, true, true);
	}

	private static Font loadFont(String location, int size) {
		Font font = null;
		try(InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(location)).getInputStream()) {
			font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.PLAIN, size);
		} catch(Exception e) {
			e.printStackTrace();
			font = new Font("default", Font.PLAIN, size);
		}
		return font;
	}

	public static void setSmoothFont() {
		normal = new MinecraftFontRenderer(smoothFont, true, true);
	}
	public static void setBoldFont() {
		normal = new MinecraftFontRenderer(boldFont, true, true);
	}

	public static void drawString(String text, double x, double y, int color, boolean shadow) {
		drawString(text, x, y, color, shadow, xyz.templecheats.templeclient.features.module.modules.client.Font.INSTANCE.isEnabled());
	}

	public static void drawString(String text, double x, double y, int color, boolean shadow, boolean customFont) {
		if(customFont) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(x, y, 0);
			GlStateManager.scale(0.8, 0.8, 0);
			if(shadow) {
				FontUtils.normal.drawStringWithShadow(text, 0, 0, color);
			} else {
				FontUtils.normal.drawString(text, 0, 0, color);
			}
			GlStateManager.popMatrix();
		} else {
			Minecraft.getMinecraft().fontRenderer.drawString(text, (float) x, (float) y, color, shadow);
		}
	}

	public static double getStringWidth(String text) {
		return getStringWidth(text, xyz.templecheats.templeclient.features.module.modules.client.Font.INSTANCE.isEnabled());
	}

	public static double getStringWidth(String text, boolean customFont) {
		if(customFont) {
			return FontUtils.normal.getStringWidth(text) * 0.8;
		} else {
			return Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
		}
	}

	public static double getFontHeight() {
		return getFontHeight(xyz.templecheats.templeclient.features.module.modules.client.Font.INSTANCE.isEnabled());
	}

	public static double getFontHeight(boolean customFont) {
		if(customFont) {
			return FontUtils.normal.getHeight() * 0.8;
		} else {
			return 9;
		}
	}
}