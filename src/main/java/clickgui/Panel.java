package clickgui;

import com.example.examplemod.Client;
import com.example.examplemod.Module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Panel {
    public Minecraft mc = Minecraft.getMinecraft();

    public int x, y, width, height, dragY, dragX;
    public boolean extended, dragging;
    public Module.Category category;

    public List<clickgui.Button> buttons = new ArrayList<>();

    public Panel(int x, int y, int width, int height, Module.Category category) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.category = category;

        int y1 = y + height;

        for (Module module : Client.modules) {
            if (module.category == category) {
                buttons.add(new clickgui.Button(x, y1, width, height, module));
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
        Gui.drawRect(x, y, x + width, y + height, new Color(0x000000).hashCode());
        mc.fontRenderer.drawStringWithShadow(category.name(), x + width / 2 - mc.fontRenderer.getStringWidth(category.name()) / 2, y + height / 2 - 9 / 2, -1);

        if (extended) {
            int y1 = y + height;
            for (clickgui.Button button : buttons) {
                button.x = x;
                button.y = y1;

                y1 += height;

                button.drawScreen(mouseX, mouseY, partialTicks);
            }
        }
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (extended) {
            for (clickgui.Button button : buttons) {
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
            for (clickgui.Button button : buttons) {
                button.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    protected void mouseReleased(int mouseX, int mouseY, int state) {
        dragging = false;

        if (extended) {
            for (clickgui.Button button : buttons) {
                button.mouseReleased(mouseX, mouseY, state);
            }
        }
    }
}
