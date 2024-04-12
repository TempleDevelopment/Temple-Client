package xyz.templecheats.templeclient.features.gui.font;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class FontUtils {
    private static final HashMap<Fonts, Map<Integer, CFont>> customFontMap = new HashMap<>();

    static {
        setupFonts();
    }

    public static void setupFonts() {
        for (Fonts font : Fonts.values()) {
            font.reloadFonts();
            HashMap<Integer, CFont> fontSizes = new HashMap<>();
            for (int size : font.sizes) {
                fontSizes.put(size, new CFont(font.fromSize(size)));
            }
            customFontMap.put(font, fontSizes);
        }
    }

    public enum Fonts {
        DEFAULT("smooth", 12, 14, 16, 18, 20, 22, 24, 26, 28, 32, 40, 80),
        DEFAULT_BOLD("bold", 12, 14, 16, 18, 20, 22, 24, 26, 28, 32, 40, 80),
        CSGO("FSEX300", 12, 14, 16, 18, 20, 22, 24, 26, 28, 32, 40, 80),
        CSGO_BOLD("calibri-bold", 12, 14, 16, 18, 20, 22, 24, 26, 28, 32, 40, 80);

        private final ResourceLocation location;
        private Font font;
        private final int[] sizes;

        Fonts(String fontName, int... sizes) {
            this.location = new ResourceLocation("minecraft", "font/" + fontName + ".ttf");
            this.sizes = sizes;
        }

        public void reloadFonts() {
            font = loadFont(location);
        }

        public Font fromSize(int size) {
            return font.deriveFont(Font.PLAIN, size);
        }

        public CFont setSize(int size) {
            return customFontMap.get(this).computeIfAbsent(size, k -> null);
        }
    }

    private static Font loadFont(ResourceLocation location) {
        try {
            InputStream inputStream = Minecraft.getMinecraft().getResourceManager().getResource(location).getInputStream();
            return Font.createFont(Font.TRUETYPE_FONT, inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading font.");
            return new Font("SansSerif", Font.PLAIN, 64);
        }
    }
}

