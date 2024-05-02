package xyz.templecheats.templeclient.features.gui.menu.alt;

import com.mojang.authlib.Agent;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.TempleClient;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

public class AltManager extends GuiScreen {
    private GuiTextField inputField;
    private GuiButton addButton, deleteButton, loginButton;
    private List<String> alts = new ArrayList<>();
    private int selectedAltIndex = -1;

    @Override
    public void initGui() {
        this.buttonList.clear();
        int centerX = this.width / 2;
        int centerY = this.height - 50;
        this.inputField = new GuiTextField(10, this.fontRenderer, centerX - 100, centerY - 25, 200, 20);
        this.addButton = new GuiButton(0, centerX - 100, centerY, 98, 20, "Add Account");
        this.deleteButton = new GuiButton(1, centerX + 2, centerY, 98, 20, "Delete Account");
        this.loginButton = new GuiButton(2, centerX - 49, centerY + 25, 98, 20, "Login");
        this.buttonList.add(this.addButton);
        this.buttonList.add(this.deleteButton);
        this.buttonList.add(this.loginButton);
        this.deleteButton.enabled = false;
        this.loginButton.enabled = false;
        this.alts = TempleClient.configManager.loadAlts();
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0 && !this.inputField.getText().isEmpty()) {
            alts.add(this.inputField.getText());
            TempleClient.configManager.saveAlts(alts);
            this.inputField.setText("");
        } else if (button.id == 1 && selectedAltIndex >= 0) {
            alts.remove(selectedAltIndex);
            TempleClient.configManager.saveAlts(alts);
            selectedAltIndex = -1;
            this.deleteButton.enabled = false;
            this.loginButton.enabled = false;
        } else if (button.id == 2 && selectedAltIndex >= 0) {
            String username = alts.get(selectedAltIndex);
            changeName(username);
        }
    }
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        this.inputField.textboxKeyTyped(typedChar, keyCode);
    }
    private void changeName(String name) {
        YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
        YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) service.createUserAuthentication(Agent.MINECRAFT);
        auth.logOut();
        Session session = new Session(name, name, "0", "legacy");
        try {
            Field sessionField = Minecraft.class.getDeclaredField("session");
            sessionField.setAccessible(true);
            sessionField.set(Minecraft.getMinecraft(), session);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.inputField.drawTextBox();
        int startY = 100;
        for (int i = 0; i < alts.size(); i++) {
            int posY = startY + (i * 20);
            drawRect(this.width / 2 - 100, posY - 10, this.width / 2 + 100, posY + 10, Color.BLACK.getRGB());
            if (i == selectedAltIndex) {
                drawCenteredString(fontRenderer, alts.get(i) + " [Selected]", this.width / 2, posY, 0xFFFF00);
            } else {
                drawCenteredString(fontRenderer, alts.get(i), this.width / 2, posY, 0xFFFFFF);
            }
            ResourceLocation skinTexture = new ResourceLocation("textures/entity/steve.png");
            mc.getTextureManager().bindTexture(skinTexture);
            Gui.drawScaledCustomSizeModalRect(this.width / 2 - 110, posY - 10, 8.0F, 8.0F, 8, 8, 20, 20, 64.0F, 64.0F);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.inputField.mouseClicked(mouseX, mouseY, mouseButton);
        int startY = 100;
        for (int i = 0; i < alts.size(); i++) {
            int posY = startY + (i * 20);
            if (mouseY >= posY - 10 && mouseY <= posY + 10 && mouseX >= this.width / 2 - 100 && mouseX <= this.width / 2 + 100) {
                selectedAltIndex = i;
                this.deleteButton.enabled = true;
                this.loginButton.enabled = true;
            }
        }
    }
}
