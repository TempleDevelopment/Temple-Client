package xyz.templecheats.templeclient.features.gui.font;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.apache.commons.lang3.tuple.MutablePair;
import org.lwjgl.opengl.GL11;
import xyz.templecheats.templeclient.features.module.modules.client.FontSettings;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import static org.lwjgl.opengl.GL11.*;

public class CFont implements AbstractFont {
    private static final Tessellator tessellator = Tessellator.getInstance();
    private static final BufferBuilder buffer = tessellator.getBuffer();
    private static int[] colorCode;
    private static final String colorcodeIdentifiers = "0123456789abcdefklmnor";
    private final FontData regular = new FontData(Font.PLAIN), italic = new FontData(Font.ITALIC);
    private final Font font;
    private int fontHeight;
    private CFont boldFont;
    private static final float kerning = 8.3f;

    public CFont(Font font) {
        generateColorCodes();
        this.font = font;
        setupTexture(regular);
        setupTexture(italic);
    }

    public boolean hasBold() {
        return this.boldFont != null;
    }

    private void setupTexture(FontData fontData) {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = (Graphics2D) image.getGraphics();

        Font currentFont = fontData.textType == 0 ? font : font.deriveFont(fontData.textType);
        graphics.setFont(currentFont);
        handleSprites(fontData, currentFont, graphics);
    }

    @Override
    public void drawStringWithShadow(String text, float x, float y, int color, float scale) {
        drawString(text, x, y, color, true, scale);
    }

    @Override
    public void drawStringWithShadow(String text, float x, float y, Color color, float scale) {
        drawStringWithShadow(text, x, y, color.getRGB(), scale);
    }

    @Override
    public void drawString(String text, float x, float y, int color, boolean shadow, float scale) {
        if (FontSettings.INSTANCE.isEnabled()) {
            if (shadow) drawString(text, x + 0.5f, y + 0.5f, color, true, scale, false);
            drawString(text, x, y, color, false, scale, false);
        } else {
            Minecraft.getMinecraft().fontRenderer.drawString(text, x, y, color, shadow);
        }
    }

    public void drawString(String text, double x, double y, int color, boolean shadow, float scale) {
        drawString(text, (float) x, (float) y, color, shadow, scale);
    }

    @Override
    public void drawString(String text, float x, float y, Color color, boolean shadow, float scale) {
        drawString(text, x, y, color.getRGB(), shadow, scale);
    }

    @Override
    public void drawCenteredString(String text, float x, float y, Color color, boolean shadow, float scale) {
        drawString(text, x - (getStringWidth(text) / 2), y, color.getRGB(), shadow, scale);
    }

    @Override
    public String trimStringToWidth(String text, int width) {
        return this.trimStringToWidth(text, width, false);
    }

    @Override
    public String trimStringToWidth(String text, int width, boolean reverse) {
        if (text == null) return "";
        StringBuilder buffer = new StringBuilder();
        float lineWidth = 0.0F;
        int offset = reverse ? text.length() - 1 : 0;
        int increment = reverse ? -1 : 1;
        boolean var8 = false;
        boolean var9 = false;


        for (int index = offset; index >= 0 && index < text.length() && lineWidth < (float) width; index += increment) {
            char character = text.charAt(index);
            float charWidth = this.getCharWidthFloat(character);

            if (var8) {
                var8 = false;

                if (character != 108 && character != 76) {
                    if (character == 114 || character == 82) {
                        var9 = false;
                    }
                } else {
                    var9 = true;
                }
            } else if (charWidth < 0) {
                var8 = true;
            } else {
                lineWidth += charWidth;

                if (var9) {
                    ++lineWidth;
                }
            }

            if (lineWidth > (float) width) {
                break;
            }

            if (reverse) {
                buffer.insert(0, character);
            } else {
                buffer.append(character);
            }
        }

        return buffer.toString();
    }

    public float getCharWidthFloat(char c) {
        if (c == 167) {
            return -1;
        } else if (c == 32) {
            return 2;
        } else {
            int var2 = ("\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000").indexOf(c);

            if (c > 0 && var2 != -1) {
                return ((regular.chars[var2].width / 2.f) - 4.f);
            } else if (c < regular.chars.length && ((regular.chars[c].width / 2.f) - 4.f) != 0) {
                int var3 = ((int) ((regular.chars[c].width / 2.f) - 4.f)) >>> 4;
                int var4 = ((int) ((regular.chars[c].width / 2.f) - 4.f)) & 15;
                var3 &= 15;
                ++var4;
                return (float) ((var4 - var3) / 2 + 1);
            } else {
                return 0;
            }
        }
    }

    public float drawString(String text, float x, float y, int color, boolean shadow, float scale, boolean smooth) {
        if (text == null) {
            return 0;
        }
        if (color == 0x20FFFFFF) {
            color = 0xFFFFFF;
        }
        if ((color & 0xFC000000) == 0) {
            color |= 0xFF000000;
        }
        if (shadow) {
            color = (color & 0xFCFCFC) >> 2 | color & 0xFF000000;
        }
        float alpha = (float) (color >> 24 & 255) / 255f;

        GlStateManager.pushMatrix();
        GlStateManager.scale(0.5, 0.5, 0.5);
        GlStateManager.translate(0.0, FontSettings.INSTANCE.getOffset(), 0.0);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.resetColor();
        GlStateManager.color((float) (color >> 16 & 255) / 255f, (float) (color >> 8 & 255) / 255f, (float) (color & 255) / 255f, alpha);
        GlStateManager.enableTexture2D();
        GlStateManager.bindTexture(this.regular.texture.getGlTextureId());
        if (smooth) {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        } else {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        }

        float returnVal = drawCustomChars(text, x, y, color, shadow);

        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_DONT_CARE);
        GlStateManager.popMatrix();
        GlStateManager.resetColor();
        GlStateManager.bindTexture(0);

        return returnVal;
    }

    private float drawCustomChars(String text, double x, double y, int color, boolean shadow) {
        x = (x - 1) * 2;
        y = (y - 3) * 2;
        FontData currentData = this.regular;
        float alpha = (float) (color >> 24 & 255) / 255f;
        boolean bold = false,
                italic = false,
                strikethrough = false,
                underline = false;

        for (int index = 0; index < text.length(); index++) {
            char character = text.charAt(index);
            if (character == '\u00a7') {
                int colorIndex = 21;

                try {
                    colorIndex = colorcodeIdentifiers.indexOf(text.charAt(index + 1));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (colorIndex < 16) {
                    bold = false;
                    italic = false;
                    underline = false;
                    strikethrough = false;
                    GlStateManager.bindTexture(this.regular.texture.getGlTextureId());
                    currentData = this.regular;

                    if (colorIndex < 0) {
                        colorIndex = 15;
                    }
                    if (shadow) {
                        colorIndex += 16;
                    }
                    int colorcode = colorCode[colorIndex];
                    GlStateManager.color((float) (colorcode >> 16 & 255) / 255f, (float) (colorcode >> 8 & 255) / 255f, (float) (colorcode & 255) / 255f, alpha);
                } else {
                    switch (colorIndex) {
                        case 17:
                            if (hasBold()) {
                                bold = true;
                                if (italic) {
                                    GlStateManager.bindTexture(this.boldFont.italic.texture.getGlTextureId());
                                    currentData = this.boldFont.italic;
                                } else {
                                    GlStateManager.bindTexture(this.boldFont.regular.texture.getGlTextureId());
                                    currentData = this.boldFont.regular;
                                }
                            }
                            break;
                        case 18:
                            strikethrough = true;
                            break;
                        case 19:
                            underline = true;
                            break;
                        case 20:
                            italic = true;
                            if (bold && hasBold()) {
                                GlStateManager.bindTexture(this.boldFont.italic.texture.getGlTextureId());
                                currentData = this.boldFont.italic;
                            } else {
                                GlStateManager.bindTexture(this.italic.texture.getGlTextureId());
                                currentData = this.italic;
                            }
                            break;
                        default:
                            bold = false;
                            italic = false;
                            underline = false;
                            strikethrough = false;
                            GlStateManager.color((float) (color >> 16 & 255) / 255f, (float) (color >> 8 & 255) / 255f, (float) (color & 255) / 255f, alpha);
                            GlStateManager.bindTexture(this.regular.texture.getGlTextureId());
                            currentData = this.regular;
                            break;
                    }
                }
                ++index;
            } else if (character < currentData.chars.length) {
                CharData charData = currentData.chars[character];
                drawQuad((float) x, (float) y, charData.width, charData.height, charData.storedX, charData.storedY, currentData.imageSize.getLeft(), currentData.imageSize.getRight());

                if (strikethrough) {
                    this.drawLine(x, y + (double) (charData.height / 2), x + (double) charData.width - 8, y + (double) (charData.height / 2));
                }
                if (underline) {
                    this.drawLine(x + 2.5f, y + (double) charData.height - 1, x + charData.width - 6, y + (double) charData.height - 1);
                }
                x += Math.round((currentData.chars[character].width - kerning + FontSettings.INSTANCE.getGapSize()) * 2) / 2.0;
            }
        }
        return (float) (x / 2);
    }

    protected void drawQuad(float x2, float y2, float width, float height, float srcX, float srcY, float imgWidth, float imgHeight) {
        float renderSRCX = srcX / imgWidth;
        float renderSRCY = srcY / imgHeight;
        float renderSRCWidth = width / imgWidth;
        float renderSRCHeight = height / imgHeight;

        buffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(x2 + width, y2, 0).tex(renderSRCX + renderSRCWidth, renderSRCY).endVertex();
        buffer.pos(x2, y2, 0).tex(renderSRCX, renderSRCY).endVertex();
        buffer.pos(x2, y2 + height, 0).tex(renderSRCX, renderSRCY + renderSRCHeight).endVertex();
        buffer.pos(x2, y2 + height, 0).tex(renderSRCX, renderSRCY + renderSRCHeight).endVertex();
        buffer.pos(x2 + width, y2 + height, 0).tex(renderSRCX + renderSRCWidth, renderSRCY + renderSRCHeight).endVertex();
        buffer.pos(x2 + width, y2, 0).tex(renderSRCX + renderSRCWidth, renderSRCY).endVertex();

        tessellator.draw();
    }

    private void drawLine(double x2, double y2, double x1, double y1) {
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        buffer.pos(x2, y2, 0).endVertex();
        buffer.pos(x1, y1, 0).endVertex();
        tessellator.draw();
    }

    @Override
    public float getStringWidth(String text) {
        if (FontSettings.INSTANCE.isEnabled()) {
            return (float) getStringWidth(text, kerning);
        } else {
            return Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
        }
    }

    public double getStringWidth(String text, float kerning) {
        if (text == null) {
            return 0;
        }
        float width = 0;
        CharData[] currentData = regular.chars;
        for (int index = 0; index < text.length(); index++) {
            char character = text.charAt(index);

            if (character == '\u00a7') {
                int colorIndex = colorcodeIdentifiers.indexOf(text.charAt(index + 1));
                switch (colorIndex) {
                    case 17:
                        if (hasBold()) {
                            currentData = this.boldFont.regular.chars;
                        }
                        break;
                    case 20:
                        currentData = this.regular.chars;
                        break;
                    default:
                        currentData = regular.chars;
                        break;
                }
                ++index;
            } else if (character < currentData.length) {
                width += currentData[character].width - kerning + FontSettings.INSTANCE.getGapSize();
            }
        }

        return width / 2;
    }

    @Override
    public float getFontHeight() {
        return (float) (this.fontHeight - 8) / 2;
    }

    void generateColorCodes() {
        if (colorCode == null) {
            colorCode = new int[32];
            for (int index = 0; index < 32; ++index) {
                final int noClue = (index >> 3 & 0x1) * 85;
                int red = (index >> 2 & 0x1) * 170 + noClue;
                int green = (index >> 1 & 0x1) * 170 + noClue;
                int blue = (index & 0x1) * 170 + noClue;
                if (index == 6) {
                    red += 85;
                }
                if (index >= 16) {
                    red /= 4;
                    green /= 4;
                    blue /= 4;
                }
                colorCode[index] = (red & 255) << 16 | (green & 255) << 8 | blue & 255;
            }
        }
    }

    private void handleSprites(FontData fontData, Font currentFont, Graphics2D graphics2D) {
        handleSprites(fontData, currentFont, graphics2D, false);
    }

    private void handleSprites(FontData fontData, Font currentFont, Graphics2D graphics2D, boolean drawString) {
        int charHeight = 0;
        int positionX = 0;
        int positionY = 1;
        int index = 0;
        FontMetrics fontMetrics = graphics2D.getFontMetrics();

        if (drawString) {
            BufferedImage image = new BufferedImage(fontData.imageSize.getLeft(), fontData.imageSize.getRight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = (Graphics2D) image.getGraphics();

            graphics.setFont(currentFont);
            graphics.setColor(new Color(255, 255, 255, 0));
            graphics.fillRect(0, 0, fontData.imageSize.getLeft(), fontData.imageSize.getRight());
            graphics.setColor(Color.WHITE);
            graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (CharData data : fontData.chars) {
                char c = (char) index;
                graphics.drawString(String.valueOf(c), data.storedX + 2, data.storedY + fontMetrics.getAscent());
                index++;
            }

            fontData.texture = new DynamicTexture(image);
        } else {
            while (index < fontData.chars.length) {
                char c = (char) index;
                CharData charData = new CharData();
                Rectangle2D dimensions = fontMetrics.getStringBounds(String.valueOf(c), graphics2D);
                charData.width = dimensions.getBounds().width + kerning;
                charData.height = dimensions.getBounds().height;

                if (positionX + charData.width >= fontData.imageSize.getLeft()) {
                    positionX = 0;
                    positionY += charHeight;
                    charHeight = 0;
                }

                if (charData.height > charHeight) {
                    charHeight = charData.height;
                }

                charData.storedX = positionX;
                charData.storedY = positionY;

                if (charData.height > this.fontHeight) {
                    this.fontHeight = charData.height;
                }

                fontData.chars[index] = charData;
                positionX += (int) charData.width;
                fontData.imageSize.setRight(positionY + fontMetrics.getAscent());
                ++index;
            }
            handleSprites(fontData, currentFont, graphics2D, true);
        }
    }

    private static class FontData {
        private final CharData[] chars = new CharData[256];
        private final int textType;
        private DynamicTexture texture;
        private final MutablePair<Integer, Integer> imageSize = MutablePair.of(512, 0);

        public FontData(int textType) {
            this.textType = textType;
        }
    }

    private static class CharData {
        private float width;
        private int height, storedX, storedY;
    }
}
