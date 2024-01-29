package xyz.templecheats.templeclient.impl.gui.font;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.InputStream;

public class FontUtils {
	public static MinecraftFontRenderer normal;
	private static Font arialFont;
	private static Font italicFont;
	
	static {
		bootstrap();
	}
	
	public static void bootstrap() {
		arialFont = loadFont("font.otf", 25);
		italicFont = loadFont("arial-italic.otf", 24);
		
		normal = new MinecraftFontRenderer(arialFont, true, true);
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
	
	public static void setArialFont() {
		normal = new MinecraftFontRenderer(arialFont, true, true);
	}
	
	public static void setItalicFont() {
		normal = new MinecraftFontRenderer(italicFont, true, true);
	}
	
	public static void drawString(String text, double x, double y, int color, boolean shadow) {
		if(xyz.templecheats.templeclient.impl.modules.client.Font.INSTANCE.isEnabled()) {
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
		if(xyz.templecheats.templeclient.impl.modules.client.Font.INSTANCE.isEnabled()) {
			return FontUtils.normal.getStringWidth(text) * 0.8;
		} else {
			return Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
		}
	}
	
	public static double getFontHeight() {
		if(xyz.templecheats.templeclient.impl.modules.client.Font.INSTANCE.isEnabled()) {
			return FontUtils.normal.getHeight() * 0.8;
		} else {
			return 9;
		}
	}
}
