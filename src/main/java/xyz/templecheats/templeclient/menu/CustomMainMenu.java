package xyz.templecheats.templeclient.menu;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.resources.I18n;
import xyz.templecheats.templeclient.menu.tools.AltManager;

import java.io.IOException;

public class CustomMainMenu extends GuiMainMenu {
    private GuiButton altButton;

    @Override
    public void initGui() {
        super.initGui();
        int i = this.height / 4 + 48;
        this.buttonList.add(this.altButton = new GuiButton(5, this.width / 2 - 100, i + 72 + 36, 200, 20, "Alt Manager"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawString(this.fontRenderer, "Made by PhilipPanda", 0, 0, 0xFFFFFFFF);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button == altButton) {
            this.mc.displayGuiScreen(new AltManager());
            return;
        }
        super.actionPerformed(button);
    }
}