package xyz.templecheats.templeclient.impl.gui.font;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.InputStream;

public class FontUtils {
    public static MinecraftFontRenderer normal;
    private static Font customFont;
    private static Font defaultFont;

    static {
        bootstrap();
    }

    public static void bootstrap() {
        customFont = loadFont("font.otf", 25);
        defaultFont = loadFont("minecraft.otf", 24);

        normal = new MinecraftFontRenderer(customFont, true, true);
    }

    private static Font loadFont(String location, int size) {
        Font font = null;
        try (InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(location)).getInputStream()) {
            font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.PLAIN, size);
        } catch (Exception e) {
            e.printStackTrace();
            font = new Font("default", Font.PLAIN, size);
        }
        return font;
    }

    public static void setCustomFont() {
        normal = new MinecraftFontRenderer(customFont, true, true);
    }

    public static void setDefaultFont() {
        normal = new MinecraftFontRenderer(defaultFont, true, true);
    }
}
