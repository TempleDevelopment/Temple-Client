package xyz.templecheats.templeclient.features.gui.menu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.util.Session;
import net.minecraft.util.SoundCategory;
import xyz.templecheats.templeclient.features.gui.menu.alt.GuiAccountSelector;

import java.awt.*;
import java.io.IOException;

public class CustomMainMenu extends GuiMainMenu {
    private GuiButton altButton;

    @Override
    public void initGui() {
        super.initGui();
        Minecraft.getMinecraft().gameSettings.setSoundLevel(SoundCategory.MUSIC, 0.0F);
        int i = this.height / 4 + 48;
        this.buttonList.add(this.altButton = new GuiButton(5, this.width / 2 - 100, i + 72 + 36, 200, 20, "Alt Manager"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        Session session = mc.getSession();
        String username = session.getUsername();
        drawString(this.fontRenderer, "templecheats.xyz", 0, 0, new Color(0xFFFFFF).getRGB());
        drawString(this.fontRenderer, "Logged in as: " + username, 0, 10, new Color(0xFFFFFF).getRGB());
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button == altButton) {
            this.mc.displayGuiScreen(new GuiAccountSelector());
            return;
        }
        super.actionPerformed(button);
    }
}