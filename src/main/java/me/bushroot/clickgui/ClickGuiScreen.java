package me.bushroot.clickgui;

import com.example.examplemod.Module.Module;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClickGuiScreen extends GuiScreen {
    public List<Panel> panels = new ArrayList<>();

    public ClickGuiScreen() {
        int y = 10;

        for (Module.Category value : Module.Category.values()) {
            panels.add(new Panel(10, y, 110, 15, value));
            y += 20;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        for (Panel panel : panels) {
            panel.drawScreen(mouseX, mouseY, partialTicks);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        for (Panel panel : panels) {
            panel.keyTyped(typedChar, keyCode);
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (Panel panel : panels) {
            panel.mouseClicked(mouseX, mouseY, mouseButton);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        for (Panel panel : panels) {
            panel.mouseReleased(mouseX, mouseY, state);
        }
        super.mouseReleased(mouseX, mouseY, state);
    }
}
