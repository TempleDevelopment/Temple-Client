package xyz.templecheats.templeclient.features.gui.menu.alt;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.manager.AltManager;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class GuiAccountSelector extends GuiScreen {
    private GuiTextField inputField;
    private GuiButton addButton, deleteButton, loginButton;
    private AltManager altManager;

    public GuiAccountSelector() {
        this.altManager = new AltManager();
    }

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
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0 && !this.inputField.getText().isEmpty()) {
            altManager.addAlt(this.inputField.getText());
            this.inputField.setText("");
        } else if (button.id == 1 && altManager.getSelectedAltIndex() >= 0) {
            altManager.deleteAlt(altManager.getSelectedAltIndex());
            this.deleteButton.enabled = false;
            this.loginButton.enabled = false;
        } else if (button.id == 2 && altManager.getSelectedAltIndex() >= 0) {
            altManager.loginSelectedAlt();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        this.inputField.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.inputField.drawTextBox();
        List<String> alts = altManager.getAlts();
        int startY = 100;
        for (int i = 0; i < alts.size(); i++) {
            int posY = startY + (i * 20);
            drawRect(this.width / 2 - 100, posY - 10, this.width / 2 + 100, posY + 10, Color.BLACK.getRGB());
            if (i == altManager.getSelectedAltIndex()) {
                drawCenteredString(fontRenderer, alts.get(i) + " [Selected]", this.width / 2, posY, 0xFFFF00);
            } else {
                drawCenteredString(fontRenderer, alts.get(i), this.width / 2, posY, 0xFFFFFF);
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.inputField.mouseClicked(mouseX, mouseY, mouseButton);
        List<String> alts = altManager.getAlts();
        int startY = 100;
        for (int i = 0; i < alts.size(); i++) {
            int posY = startY + (i * 20);
            if (mouseY >= posY - 10 && mouseY <= posY + 10 && mouseX >= this.width / 2 - 100 && mouseX <= this.width / 2 + 100) {
                altManager.setSelectedAltIndex(i);
                this.deleteButton.enabled = true;
                this.loginButton.enabled = true;
            }
        }
    }
}
