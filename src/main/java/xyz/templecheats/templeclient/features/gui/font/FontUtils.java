package xyz.templecheats.templeclient.features.gui.font;

import java.awt.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class FontUtils {
    private static final HashMap<Fonts, Map<Integer, CFont>> customFontMap = new HashMap<>();
    private static final HashMap<Icons, Map<Integer, CFont>> customIconMap = new HashMap<>();

    static {
        setupFonts();
        setupIcons();
    }

    public static void setupIcons() {
        for (Icons icon : Icons.values()) {
            icon.reloadFonts();
            HashMap<Integer, CFont> fontSize = new HashMap<>();
            for (int size : icon.sizes) {
                fontSize.put(size, new CFont(icon.fromSize(size)));
            }
            customIconMap.put(icon, fontSize);
        }
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


    public enum Icons {
        ICON("temple", 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34, 40, 46, 80);

        private final String location;
        private Font font;
        private final int[] sizes;

        Icons(String fontName, int... sizes) {
            this.location = ("/assets/minecraft/font/" + fontName + ".ttf");
            this.sizes = sizes;
        }

        public void reloadFonts() {
            font = loadFont(location);
        }

        public Font fromSize(int size) {
            return font.deriveFont(Font.PLAIN, size);
        }

        public CFont setSize(int size) {
            return customIconMap.get(this).computeIfAbsent(size, k -> null);
        }
    }

    public enum Fonts {
        Default("cpmono", 12, 14, 16, 18, 20, 22, 24, 26, 28, 32, 40, 80),
        GreyCliff("greycliff-medium", 12, 14, 16, 18, 20, 22, 24, 26, 28, 32, 40, 80),
        GreyCliffBold("greycliff-bold", 12, 14, 16, 18, 20, 22, 24, 26, 28, 32, 40, 80),
        Montserrat("montserrat", 12, 14, 16, 18, 20, 22, 24, 26, 28, 32, 40, 80),
        Rubik("rubik", 12, 14, 16, 18, 20, 22, 24, 26, 28, 32, 40, 80),
        RubikBold("rubik-bold", 12, 14, 16, 18, 20, 22, 24, 26, 28, 32, 40, 80);
        private final String location;
        private Font font;
        private final int[] sizes;

        Fonts(String fontName, int... sizes) {
            this.location = ("/assets/minecraft/font/" + fontName + ".ttf");
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

    private static Font loadFont(String location) {
        try {
            InputStream inputStream = FontUtils.class.getResourceAsStream(location);
            assert inputStream != null;
            Font clientFont = Font.createFont(0, inputStream);
            clientFont = clientFont.deriveFont(Font.PLAIN, +10);
            inputStream.close();
            return clientFont;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading font.");
            return new Font("SansSerif", Font.PLAIN, 64);
        }
    }
}

