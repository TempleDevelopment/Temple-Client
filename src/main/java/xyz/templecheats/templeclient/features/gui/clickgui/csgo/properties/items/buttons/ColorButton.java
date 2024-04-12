package xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.Item;
import xyz.templecheats.templeclient.features.module.modules.client.hud.Notifications;
import xyz.templecheats.templeclient.util.color.ColorUtil;
import xyz.templecheats.templeclient.util.color.impl.RectBuilder;
import xyz.templecheats.templeclient.util.color.impl.RoundedTexture;
import xyz.templecheats.templeclient.util.math.Vec2d;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.util.render.StencilUtil;
import xyz.templecheats.templeclient.util.setting.impl.ColorSetting;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import static xyz.templecheats.templeclient.util.color.ColorUtil.*;
import static xyz.templecheats.templeclient.util.render.RenderUtil.*;

public class ColorButton extends Item {
    private boolean extended = false;
    private Color finalColor;
    private final Button parentButton;
    private final ColorSetting setting;
    private boolean draggingPickerBase;
    private boolean draggingRGBSlider;
    private boolean draggingAlphaSlider;
    float[] color;

    public ColorButton(ColorSetting setting, Button parentButton) {
        super(setting.name);
        this.parentButton = parentButton;
        this.setting = setting;
        this.height = getExtended() ? 108 : 15;
        this.color = new float[]{0.0f, 0.0f, 0.0f};
        this.finalColor = setting.getColor();
    }

    @Override
    public int getHeight() {
        return getExtended() ? 108 : 15;
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        new RectBuilder(new Vec2d(x + getWidth() - 16, y + 5), new Vec2d(x + getWidth() - 3, y + height - 4))
                .outlineColor(Color.WHITE).width(0.8).color(setting.getColor()).radius(2.0).draw();
        if (extended) {
            float pickerPosX = x;
            float pickerPosY = y + 16;

            // Background
            new RectBuilder(new Vec2d(pickerPosX - 2, pickerPosY - 2), new Vec2d(pickerPosX + getWidth() + 3, pickerPosY + 95))
                    .outlineColor(new Color(35, 35, 35)).width(0.8).color(new Color(0x80151515)).radius(3.0).draw();
            updateColor(pickerPosX, pickerPosY, mouseX, mouseY);
            drawColorPickerArea(pickerPosX, pickerPosY);
            drawRGBSliders(pickerPosX, pickerPosY);
            drawAlphaSlider(pickerPosX, pickerPosY);
            drawFuncButton(pickerPosX, pickerPosY, mouseX, mouseY);
        }
        GlStateManager.pushMatrix();
        parentButton.font14.drawString(this.setting.getName(), x + 2.3, y + 5, 0xFFFFFFFF, false, 1.0f);
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
        }
        catch (Exception exception) {
            Notifications.showNotification("Invalid color!");
        }
        if (this.draggingRGBSlider) {
            position = Math.min(Math.max(pickerPosX, mouseX), pickerPosX + getWidth());
            color[0] = (position - pickerPosX) / (float)getWidth();
        } else if (this.draggingAlphaSlider) {
            position = ColorUtil.getAlphaFromPosition((int) (mouseX - pickerPosX), getWidth());
            setting.setAlpha((int)position);
        } else if (this.draggingPickerBase) {
            position = Math.min(Math.max(pickerPosX, mouseX), pickerPosX + getWidth());
            float restrictedY = Math.min(Math.max(pickerPosY, mouseY), pickerPosY + 54f);
            color[1] = (position - pickerPosX) / (float)getWidth();
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
        GlStateManager.pushMatrix();
        StencilUtil.initStencilToWrite();
        new RoundedTexture().drawRoundTextured(pickerPosX, pickerPosY + 60, getWidth() - 1, 59, 1.5f, 1f);
        StencilUtil.readStencilBuffer(1);
        for (int i = 0; i < getWidth() - 1; i++) {
            Color step = Color.getHSBColor((float) i / getWidth(), 1.0f, 1.0f);
            RenderUtil.drawRect(pickerPosX + i, pickerPosY + 68, pickerPosX + i + 1, pickerPosY + 58, step.getRGB());
        }
        new RectBuilder(new Vec2d(pickerPosX, pickerPosY + 68), new Vec2d(pickerPosX + getWidth(), pickerPosY + 59)).outlineColor(Color.black).width(0.7).color(new Color(0, 0, 0, 0)).draw();
        StencilUtil.uninitStencilBuffer();
        GlStateManager.popMatrix();
        float handleRGBSliderPos = ColorUtil.getPositionFromColor(setting.getColor(), getWidth());
        RenderUtil.drawRect(pickerPosX + handleRGBSliderPos - 1, pickerPosY + 68, pickerPosX + handleRGBSliderPos + 1, pickerPosY + 59, 0xFFFFFFFF);
    }

    private void drawAlphaSlider(float pickerPosX, float pickerPosY) {
        float handleAlphaSliderPos = getPositionFromAlpha(setting.getColor().getAlpha(), getWidth());
        new RectBuilder(new Vec2d(pickerPosX, pickerPosY + 70), new Vec2d(pickerPosX + getWidth(), pickerPosY + 79))
                .color(new Color(55, 55, 55)).radius(1.5).draw();
        new RectBuilder(new Vec2d(pickerPosX, pickerPosY + 70), new Vec2d(pickerPosX + getWidth(), pickerPosY + 79))
                .outlineColor(Color.black).width(0.7)
                .colorH(new Color(0, 0, 0, 0), setAlpha(setting.getColor(), 255)).radius(1.5).draw();
        RenderUtil.drawRect(pickerPosX + handleAlphaSliderPos - 1, pickerPosY + 70, pickerPosX + handleAlphaSliderPos + 1, pickerPosY + 79, 0xFFFFFFFF);
    }

    private void drawFuncButton(float pickerPosX, float pickerPosY, final int mouseX, final int mouseY) {
        // Copy
        new RectBuilder(new Vec2d(pickerPosX + 1, pickerPosY + 82), new Vec2d(pickerPosX + parentButton.font14.getStringWidth("Copy") + 4, pickerPosY + 85 + parentButton.font14.getFontHeight()))
                .color(isCopyHover(mouseX, mouseY) ? new Color(50, 50, 50, 220) : new Color(65, 65, 65, 250))
                .radius(1.0).draw();
        parentButton.font14.drawString("copy", pickerPosX + 2.3f, pickerPosY + 84, -1, true, 1.0f);

        // Paste
        new RectBuilder(new Vec2d(pickerPosX + getWidth() - 1, pickerPosY + 82), new Vec2d(pickerPosX + getWidth() - parentButton.font14.getStringWidth("Paste") - 4, pickerPosY + 85 + parentButton.font14.getFontHeight()))
                .color(isPasteHover(mouseX, mouseY) ? new Color(50, 50, 50, 220) : new Color(65, 65, 65, 250))
                .radius(1.0).draw();
        parentButton.font14.drawString("paste", pickerPosX + getWidth() - 2.3f - parentButton.font14.getStringWidth("paste"), pickerPosY + 84, -1, true, 1.0f);
    }

    private void drawColorPickerRect(float left, float top, float right, float bottom, Color color) {
        new RectBuilder(new Vec2d(left, top), new Vec2d(right, bottom)).outlineColor(Color.BLACK).width(0.8).colorH(Color.WHITE, color).radius(1.5).draw();
        new RectBuilder(new Vec2d(left, top), new Vec2d(right, bottom)).outlineColor(Color.BLACK).width(0.8).colorV(new Color(0,0,0,0), Color.BLACK).radius(1.5).draw();
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
        Notifications.showNotification("Copied the color to your clipboard.");
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
                Notifications.showNotification("Color Pasted: " + clipboardContent + "!");
            } else {
                Notifications.showNotification("Invalid Color!");
            }
        } catch (NumberFormatException e) {
            Notifications.showNotification("Invalid color format! Use Hex (#FFFFFFFF) or RGB (255,255,255)");
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
        if (color.length >= 3) {
            setting.setColor(new Color(Integer.parseInt(color[0]), Integer.parseInt(color[1]), Integer.parseInt(color[2])));
        } else {
            Notifications.showNotification("Invalid RGB format!");
        }
    }

    public boolean isCopyHover(final int mouseX, final int mouseY) {
        float pickerPosX = x;
        float pickerPosY = y + 15;
        return mouseX >= pickerPosX + 1 &&
                mouseX <= pickerPosX + parentButton.font14.getStringWidth("copy") + 4 &&
                mouseY >= pickerPosY + 82 &&
                mouseY <= pickerPosY + 84 + parentButton.font14.getFontHeight();
    }

    public boolean isPasteHover(int mouseX, int mouseY) {
        float pickerPosX = x;
        float pickerPosY = y + 15;
        return mouseX >= pickerPosX + getWidth() - 3 - parentButton.font14.getStringWidth("paste") &&
                mouseX <= (pickerPosX + getWidth() - parentButton.font14.getStringWidth("paste")) + parentButton.font14.getStringWidth("paste")&&
                mouseY >= pickerPosY + 82 &&
                mouseY <= pickerPosY + 84 + parentButton.font14.getFontHeight();
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

    private boolean isHovering(final int mouseX, final int mouseY) {
        return mouseX >= this.getX() && mouseX <= this.getX() + this.getWidth() && mouseY >= this.getY() && mouseY <= this.getY() + this.height;
    }

    public static String readClipboard() {
        try {
            return (String)Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        }
        catch (UnsupportedFlavorException | IOException exception) {
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
        return 86;
    }
}
