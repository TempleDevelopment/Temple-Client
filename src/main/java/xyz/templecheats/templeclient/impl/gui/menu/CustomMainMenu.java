package xyz.templecheats.templeclient.impl.gui.menu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import xyz.templecheats.templeclient.impl.gui.menu.tools.AltManager;

import java.awt.*;
import java.io.IOException;

public class CustomMainMenu extends GuiMainMenu {
    private GuiButton altButton;
    private static final ResourceLocation IMAGE_LOCATION = new ResourceLocation("textures/logo.png");


    @Override
    public void initGui() {
        super.initGui();
        int i = this.height / 4 + 48;
        this.buttonList.add(this.altButton = new GuiButton(5, this.width / 2 - 100, i + 72 + 36, 200, 20, "Alt Manager"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        Session session = mc.getSession();
        String username = session.getUsername();
        drawString(this.fontRenderer, "Logged in as: " + username, 0, 0, new Color(0xFFFFFF).getRGB());

        drawString(this.fontRenderer, "Made by PhilipPanda", 0, 10,  new Color(0xFFADD8E6).getRGB());

        Minecraft.getMinecraft().getTextureManager().bindTexture(IMAGE_LOCATION);
        int imageWidth = 52;
        int imageHeight = 52;
        int imageX = this.width - imageWidth - 10;
        int imageY = 0;
        drawModalRectWithCustomSizedTexture(imageX, imageY, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
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
