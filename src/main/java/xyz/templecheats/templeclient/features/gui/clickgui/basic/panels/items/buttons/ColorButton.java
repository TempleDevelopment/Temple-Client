package xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.items.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.items.Item;
import xyz.templecheats.templeclient.features.module.modules.client.hud.notification.NotificationType;
import xyz.templecheats.templeclient.features.module.modules.client.hud.notification.Notifications;
import xyz.templecheats.templeclient.util.color.ColorUtil;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.util.setting.impl.ColorSetting;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import static xyz.templecheats.templeclient.features.gui.font.Fonts.font18;
import static xyz.templecheats.templeclient.util.color.ColorUtil.*;
import static xyz.templecheats.templeclient.util.render.RenderUtil.*;

public class ColorButton extends Item {
    private boolean extended = false;
    private Color finalColor;
    private final ColorSetting setting;
    private boolean draggingPickerBase;
    private boolean draggingRGBSlider;
    private boolean draggingAlphaSlider;
    float[] color;

    public ColorButton(ColorSetting setting) {
        super(setting.name);
        this.setting = setting;
        this.color = new float[]{0.0f, 0.0f, 0.0f};
        this.finalColor = setting.getColor();
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        RenderUtil.drawGradientRect(x, y, x + getWidth() + 1, y + height, !isHovering(mouseX, mouseY) ? 0x33555555 : 0x88555555, !isHovering(mouseX, mouseY) ? 0x55555555 : 0x99555555);

        RenderUtil.drawRect(x + getWidth() - 8, y + 4, x + getWidth() - 3, y + height - 5, setting.getColor().getRGB());
        RenderUtil.drawOutBorderedRect((int) x + getWidth() - 8, (int) y + 5, (int) x + getWidth() - 3, (int) y + height - 5, 1, Color.BLACK.getRGB());

        if (extended) {
            float pickerPosX = x;
            float pickerPosY = y + 15;

            // Background
            RenderUtil.drawRect(pickerPosX - 2, pickerPosY - 1, pickerPosX + getWidth() + 3, pickerPosY + 98, new Color(0x80151515, true).getRGB());
            updateColor(pickerPosX, pickerPosY, mouseX, mouseY);
            drawColorPickerArea(pickerPosX, pickerPosY);
            drawRGBSliders(pickerPosX, pickerPosY);
            drawAlphaSlider(pickerPosX, pickerPosY);
            drawBorders(pickerPosX, pickerPosY);
            drawFuncButton(pickerPosX, pickerPosY, mouseX, mouseY);
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(this.x + 2.3, this.y + 4, 0);
        GlStateManager.scale(0.8, 0.8, 0);
        font18.drawString(this.setting.getName(), 0, 0, 0xFFFFFFFF, false);
        GlStateManager.popMatrix();
    }

    private void updateColor(float pickerPosX, float pickerPosY, int mouseX, int mouseY) {
        float position;
        try {
            color = new float[]{
                    Color.RGBtoHSB(setting.getColor().getRed(), setting.getColor().getGreen(), setting.getColor().getBlue(), null)[0],
                    Color.RGBtoHSB(setting.getColor().getRed(), setting.getColor().getGreen(), setting.getColor().getBlue(), null)[1],
                    Color.RGBtoHSB(setting.getColor().getRed(), setting.getColor().getGreen(), setting.getColor().getBlue(), null)[2]
            };
        } catch (Exception exception) {
            Notifications.addMessage("Picker", "Invalid color!", NotificationType.INFO);
        }
        if (this.draggingRGBSlider) {
            position = Math.min(Math.max(pickerPosX, mouseX), pickerPosX + getWidth());
            color[0] = (position - pickerPosX) / (float) getWidth();
        } else if (this.draggingAlphaSlider) {
            position = ColorUtil.getAlphaFromPosition((int) (mouseX - pickerPosX), getWidth());
            setting.setAlpha((int) position);
        } else if (this.draggingPickerBase) {
            position = Math.min(Math.max(pickerPosX, mouseX), pickerPosX + getWidth());
            float restrictedY = Math.min(Math.max(pickerPosY, mouseY), pickerPosY + 54f);
            color[1] = (position - pickerPosX) / (float) getWidth();
            color[2] = 1.0f - (restrictedY - pickerPosY) / 54f;
        }
        finalColor = getColor(new Color(Color.HSBtoRGB(color[0], color[1], color[2])), setting.getColor().getAlpha() / 255.f);
        setting.setColor(finalColor);
    }

    private void drawColorPickerArea(float pickerPosX, float pickerPosY) {
        Color finalColor = Color.getHSBColor(color[0], 1.0f, 1.0f);
        drawColorPickerRect(pickerPosX, pickerPosY, pickerPosX + getWidth(), pickerPosY + 57, finalColor);

        // Draw cursor
        float[] handlePickerPos = ColorUtil.getPositionFromColor(setting.getColor(), getWidth() - 2, 54);
        float cursorX = pickerPosX + handlePickerPos[0];
        float cursorY = pickerPosY + handlePickerPos[1];
        RenderUtil.drawRect(cursorX, cursorY, cursorX + 2, cursorY + 2, Color.WHITE.getRGB());
    }

    private void drawRGBSliders(float pickerPosX, float pickerPosY) {
        for (int i = 0; i < getWidth() + 1; i++) {
            Color step = Color.getHSBColor((float) i / getWidth(), 1.0f, 1.0f);
            RenderUtil.drawRect(pickerPosX + i, pickerPosY + 68, pickerPosX + i + 1, pickerPosY + 58, step.getRGB());
        }
        float handleRGBSliderPos = ColorUtil.getPositionFromColor(setting.getColor(), getWidth());
        RenderUtil.drawRect(pickerPosX + handleRGBSliderPos - 1, pickerPosY + 68, pickerPosX + handleRGBSliderPos + 1, pickerPosY + 58, 0xFFFFFFFF);
    }

    private void drawAlphaSlider(float pickerPosX, float pickerPosY) {
        boolean parallel = true;
        int squareSize = 4;
        int lastRectWidth = getWidth() % squareSize;
        int width = getWidth() - lastRectWidth;
        for (int i = -squareSize; i < width; i += squareSize) {
            if (!parallel) {
                RenderUtil.drawRect(pickerPosX + i, pickerPosY + 71, pickerPosX + i + squareSize, pickerPosY + 79, new Color(-1).getRGB());
                RenderUtil.drawRect(pickerPosX + i, pickerPosY + squareSize + 71, pickerPosX + i + squareSize, pickerPosY + 79, new Color(0x313131).getRGB());
                if (i < width - squareSize) {
                    int minX = (int) (pickerPosX + i + squareSize);
                    int maxX = (int) Math.min(pickerPosX + width, pickerPosX + i + squareSize * 2);
                    RenderUtil.drawRect(minX, pickerPosY + 71, maxX, pickerPosY + 79, new Color(0x313131).getRGB());
                    RenderUtil.drawRect(minX, pickerPosY + 71 + squareSize, maxX, pickerPosY + 79, new Color(-1).getRGB());
                }
            }
            parallel = !parallel;
        }
        if (!parallel && lastRectWidth > 0) {
            RenderUtil.drawRect(pickerPosX + width, pickerPosY + 71, pickerPosX + width + lastRectWidth, pickerPosY + 79, new Color(-1).getRGB());
            RenderUtil.drawRect(pickerPosX + width, pickerPosY + squareSize + 71, pickerPosX + width + lastRectWidth, pickerPosY + 79, new Color(0x313131).getRGB());
        }

        float handleAlphaSliderPos = getPositionFromAlpha(setting.getColor().getAlpha(), getWidth());
        RenderUtil.drawHorizontalGradientRect(pickerPosX, pickerPosY + 70, pickerPosX + getWidth(), pickerPosY + 79, setting.getColor().getTransparency(), setAlpha(setting.getColor(), 255).getRGB());
        RenderUtil.drawRect(pickerPosX + handleAlphaSliderPos - 1, pickerPosY + 70, pickerPosX + handleAlphaSliderPos + 1, pickerPosY + 79, 0xFFFFFFFF);
    }

    private void drawBorders(float pickerPosX, float pickerPosY) {
        // PickerBase border
        RenderUtil.drawOutBorderedRect((int) pickerPosX, (int) pickerPosY + 1, (int) pickerPosX + getWidth(), (int) pickerPosY + 55, 1, Color.BLACK.getRGB());
        // RGB border
        RenderUtil.drawOutBorderedRect((int) pickerPosX, (int) pickerPosY + 69, (int) pickerPosX + getWidth(), (int) pickerPosY + 58, 1, Color.BLACK.getRGB());
        // Alpha border
        RenderUtil.drawOutBorderedRect((int) pickerPosX, (int) pickerPosY + 71, (int) pickerPosX + getWidth(), (int) pickerPosY + 79, 1, Color.BLACK.getRGB());
    }

    private void drawFuncButton(float pickerPosX, float pickerPosY, final int mouseX, final int mouseY) {
        drawRect(pickerPosX + 1, pickerPosY + 82, (float) (pickerPosX + font18.getStringWidth("Copy") + 4), (float) (pickerPosY + 84 + font18.getFontHeight()), isCopyHover(mouseX, mouseY) ? new Color(50, 50, 50, 220).getRGB() : new Color(65, 65, 65, 250).getRGB());
        font18.drawString("copy", pickerPosX + 2.3f, pickerPosY + 84, -1, true);

        drawRect(pickerPosX + getWidth() - 1, pickerPosY + 82, (float) (pickerPosX + getWidth() - font18.getStringWidth("Paste") - 4), (float) (pickerPosY + 84 + font18.getFontHeight()), isPasteHover(mouseX, mouseY) ? new Color(50, 50, 50, 220).getRGB() : new Color(65, 65, 65, 250).getRGB());
        font18.drawString("paste", pickerPosX + getWidth() - 2.3f - font18.getStringWidth("paste"), pickerPosY + 84, -1, true);
    }

    private void drawColorPickerRect(float left, float top, float right, float bottom, Color color) {
        drawHorizontalGradientRect(left, top, right, bottom, 0xFFFFFFFF, color.getRGB());
        drawGradientRect(left, top, right, bottom, 0, 0xFF000000);
    }

    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        if (extended && mouseButton == 0) {
            if (isPickerHover(mouseX, mouseY)) {
                this.draggingPickerBase = true;
            } else if (isRBGHover(mouseX, mouseY)) {
                this.draggingRGBSlider = true;
            } else if (isAlphaHover(mouseX, mouseY)) {
                this.draggingAlphaSlider = true;
            }
            if (isCopyHover(mouseX, mouseY)) {
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                handleCopyAction();
            }
            if (isPasteHover(mouseX, mouseY)) {
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                handlePasteAction();
            }
        }
        if (this.isHovering(mouseX, mouseY) && mouseButton == 0) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.toggle();
        } else {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    private void handleCopyAction() {
        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        String hex = String.format("#%02x%02x%02x%02x", setting.getColor().getAlpha(), setting.getColor().getRed(), setting.getColor().getGreen(), setting.getColor().getBlue());
        StringSelection selection = new StringSelection(hex);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
        Notifications.addMessage("Picker", "Copied the color to your clipboard.", NotificationType.INFO);
    }

    private void handlePasteAction() {
        try {
            String clipboardContent = readClipboard();
            if (clipboardContent != null) {
                if (clipboardContent.startsWith("#")) {
                    handleHexPaste(clipboardContent);
                } else {
                    handleRGBPaste(clipboardContent);
                }
                Notifications.addMessage("Picker", "Color Pasted: " + clipboardContent + "!", NotificationType.INFO);
            } else {
                Notifications.addMessage("Picker", "Invalid Color!", NotificationType.INFO);
            }
        } catch (NumberFormatException e) {
            Notifications.addMessage("Picker", "Invalid color format! Use Hex (#FFFFFFFF) or RGB (255,255,255)", NotificationType.INFO);
        }
    }

    private void handleHexPaste(String hex) {
        int a = Integer.valueOf(hex.substring(1, 3), 16);
        int r = Integer.valueOf(hex.substring(3, 5), 16);
        int g = Integer.valueOf(hex.substring(5, 7), 16);
        int b = Integer.valueOf(hex.substring(7, 9), 16);

        setting.setColor(new Color(r, g, b, a));
    }

    private void handleRGBPaste(String rgb) {
        String[] color = rgb.split(",");
        setting.setColor(new Color(Integer.parseInt(color[0]), Integer.parseInt(color[1]), Integer.parseInt(color[2])));
    }

    public boolean isCopyHover(final int mouseX, final int mouseY) {
        float pickerPosX = x;
        float pickerPosY = y + 15;
        return mouseX >= pickerPosX + 1 &&
                mouseX <= pickerPosX + font18.getStringWidth("copy") + 4 &&
                mouseY >= pickerPosY + 82 &&
                mouseY <= pickerPosY + 84 + font18.getFontHeight();
    }

    public boolean isPasteHover(int mouseX, int mouseY) {
        float pickerPosX = x;
        float pickerPosY = y + 15;
        return mouseX >= pickerPosX + getWidth() - 3 - font18.getStringWidth("paste") &&
                mouseX <= (pickerPosX + getWidth() - font18.getStringWidth("paste")) + font18.getStringWidth("paste") &&
                mouseY >= pickerPosY + 82 &&
                mouseY <= pickerPosY + 84 + font18.getFontHeight();
    }

    private boolean isPickerHover(final int mouseX, final int mouseY) {
        float pickerPosX = x;
        float pickerPosY = y + 15;
        return mouseX >= pickerPosX &&
                mouseX <= pickerPosX + getWidth() &&
                mouseY >= pickerPosY &&
                mouseY <= pickerPosY + 54;
    }

    private boolean isRBGHover(final int mouseX, final int mouseY) {
        float sliderPosX = x;
        float sliderPosY = y + 15;
        return mouseX >= sliderPosX &&
                mouseX <= sliderPosX + getWidth() &&
                mouseY >= sliderPosY + 58 &&
                mouseY <= sliderPosY + 69;
    }

    private boolean isAlphaHover(final int mouseX, final int mouseY) {
        float sliderPosX = x;
        float sliderPosY = y + 15;
        return mouseX >= sliderPosX &&
                mouseX <= sliderPosX + getWidth() &&
                mouseY >= sliderPosY + 70 &&
                mouseY <= sliderPosY + 79;
    }

    public static String readClipboard() {
        try {
            return (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException | IOException exception) {
            return null;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0) {
            this.draggingPickerBase = false;
            this.draggingRGBSlider = false;
            this.draggingAlphaSlider = false;
        }
    }

    public void toggle() {
        this.extended = !this.extended;
    }

    public boolean getExtended() {
        return this.extended;
    }

    @Override
    public int getWidth() {
        return 82;
    }
}