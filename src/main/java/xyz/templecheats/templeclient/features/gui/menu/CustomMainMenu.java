package xyz.templecheats.templeclient.features.gui.menu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraft.util.SoundCategory;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.features.gui.menu.alt.AltManager;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class CustomMainMenu extends GuiMainMenu {
    private GuiButton altButton;
    private static final ResourceLocation IMAGE_LOCATION = new ResourceLocation("textures/icons/discord.png");

    private void playMusic() {
        if (!mc.getSoundHandler().isSoundPlaying(TempleClient.SONG_MANAGER.getMenuSong())) {
            try {
                mc.getSoundHandler().playSound(TempleClient.SONG_MANAGER.getMenuSong());
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
                System.err.println("Value already present: " + ex.getMessage());
            }
        }
    }


    @Override
    public void initGui() {
        super.initGui();
        this.playMusic();
        Minecraft.getMinecraft().gameSettings.setSoundLevel(SoundCategory.MUSIC, 0.0F);
        int i = this.height / 4 + 48;
        this.buttonList.add(this.altButton = new GuiButton(5, this.width / 2 - 100, i + 72 + 36, 200, 20, "Alt Manager"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        Session session = mc.getSession();
        String username = session.getUsername();
        drawString(this.fontRenderer, "Logged in as: " + username, 0, 0, new Color(0xFFFFFF).getRGB());

        drawString(this.fontRenderer, "Made by PhilipPanda", 0, 10, new Color(0xFFADD8E6).getRGB());

        Minecraft.getMinecraft().getTextureManager().bindTexture(IMAGE_LOCATION);
        int imageWidth = 30;
        int imageHeight = 25;
        int imageX = this.width - imageWidth - 5;
        int imageY = 0;
        drawModalRectWithCustomSizedTexture(imageX, imageY, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        int imageWidth = 30;
        int imageHeight = 25;
        int imageX = this.width - imageWidth - 5;
        int imageY = 0;
        if (mouseX >= imageX && mouseX <= imageX + imageWidth && mouseY >= imageY && mouseY <= imageY + imageHeight) {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                try {
                    Desktop.getDesktop().browse(new URI("https://discord.gg/XZUGTpGCe8"));
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Minecraft.getMinecraft().gameSettings.setSoundLevel(SoundCategory.MUSIC, 1.0F);
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