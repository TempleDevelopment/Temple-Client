package xyz.templecheats.templeclient.clickgui;

import xyz.templecheats.templeclient.Client;
import xyz.templecheats.templeclient.Module.Module;
import xyz.templecheats.templeclient.font.FontUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Panel {
    public Minecraft mc = Minecraft.getMinecraft();

    public int x, y, width, height, dragY, dragX;
    public boolean extended, dragging;
    public Module.Category category;

    public List<Button> buttons = new ArrayList<>();

    public Panel(int x, int y, int width, int height, Module.Category category) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.category = category;

        int y1 = y + height;

        for (Module module : Client.modules) {
            if (module.category == category) {
                buttons.add(new Button(x, y1, width, height, module));
                y1 += height;
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (dragging) {
            x = mouseX - dragX;
            y = mouseY - dragY;
        }


// Gui.drawRect(x, y, x + width, y + height, Hud.rainbow(300 * 20));
        Gui.drawRect(x, y, x + width, y + height, new Color(0x181818).hashCode());

        String text = category.name();
        int textWidth = (int) (FontUtils.normal.getStringWidth(text) / 0.7F);
        int textHeight = mc.fontRenderer.FONT_HEIGHT;

        int textX = x + 4;
        int textY = y + 4;

        GlStateManager.pushMatrix();
        GlStateManager.translate(textX, textY, 1);
        GlStateManager.scale(0.7F, 0.7F, 1);

        FontUtils.normal.drawString(text, 0, 0, -1);

        GlStateManager.popMatrix();




        if (extended) {
            int y1 = y + height;
            for (Button button : buttons) {
                button.x = x;
                button.y = y1;

                y1 += height;

                button.drawScreen(mouseX, mouseY, partialTicks);
            }
        }
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (extended) {
            for (Button button : buttons) {
                button.keyTyped(typedChar, keyCode);
            }
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (HoverUtils.hovered(mouseX, mouseY, x, y, x + width, y + height)) {
            if (mouseButton == 0) {
                dragX = mouseX - x;
                dragY = mouseY - y;
                dragging = true;
            } else if (mouseButton == 1) {
                extended = !extended;
            }
        }

        if (extended) {
            for (Button button : buttons) {
                button.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    protected void mouseReleased(int mouseX, int mouseY, int state) {
        dragging = false;

        if (extended) {
            for (Button button : buttons) {
                button.mouseReleased(mouseX, mouseY, state);
            }
        }
    }
}