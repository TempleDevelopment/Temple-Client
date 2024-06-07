package xyz.templecheats.templeclient.features.gui.menu.alt;

import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import xyz.templecheats.templeclient.manager.AltManager;

public class GuiAccountSelector extends GuiScreen {
    public static GuiAccountSelector INSTANCE;
    public static final int BUTTON_WIDTH = 100;
    public static final int BUTTON_HEIGHT = 20;
    GuiTextField username;
    GuiTextField token;
    GuiTextField password;

    private boolean isUsernameFocused = false;
    private boolean isTokenFocused = false;
    private boolean isPasswordFocused = false;

    public void initGui() {
        super.initGui();
        this.username = new GuiTextField(0, this.fontRenderer, (this.width - 100) / 2, (this.height - 20) / 2, 100, 20);
        this.token = new GuiTextField(1, this.fontRenderer, (this.width - 100) / 2, (this.height - 20) / 2 + 20, 100, 20);
        this.password = new GuiTextField(2, this.fontRenderer, (this.width - 100) / 2, (this.height - 20) / 2 + 40, 100, 20);
        this.username.setText("Username");
        this.token.setText("Token");
        this.password.setText("Password");
        this.addButton(new GuiButton(3, (this.width - 100) / 2, (this.height - 20) / 2 + 60, 100, 20, "Login"));
        this.addButton(new GuiButton(4, (this.width - 100) / 2, (this.height - 20) / 2 + 80, 100, 20, "Exit"));
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (!isUsernameFocused && this.username.getText().isEmpty()) {
            this.fontRenderer.drawString("Username", this.username.x, this.username.y + 6, 0xAAAAAA);
        }
        if (!isTokenFocused && this.token.getText().isEmpty()) {
            this.fontRenderer.drawString("Token", this.token.x, this.token.y + 6, 0xAAAAAA);
        }
        if (!isPasswordFocused && this.password.getText().isEmpty()) {
            this.fontRenderer.drawString("Password", this.password.x, this.password.y + 6, 0xAAAAAA);
        }

        this.username.drawTextBox();
        this.token.drawTextBox();
        this.password.drawTextBox();
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.username.mouseClicked(mouseX, mouseY, mouseButton);
        this.token.mouseClicked(mouseX, mouseY, mouseButton);
        this.password.mouseClicked(mouseX, mouseY, mouseButton);

        if (this.username.isFocused()) {
            if (!isUsernameFocused) {
                this.username.setText("");
                isUsernameFocused = true;
            }
        } else {
            if (this.username.getText().isEmpty()) {
                this.username.setText("Username");
                isUsernameFocused = false;
            }
        }

        if (this.token.isFocused()) {
            if (!isTokenFocused) {
                this.token.setText("");
                isTokenFocused = true;
            }
        } else {
            if (this.token.getText().isEmpty()) {
                this.token.setText("Token");
                isTokenFocused = false;
            }
        }

        if (this.password.isFocused()) {
            if (!isPasswordFocused) {
                this.password.setText("");
                isPasswordFocused = true;
            }
        } else {
            if (this.password.getText().isEmpty()) {
                this.password.setText("Password");
                isPasswordFocused = false;
            }
        }
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        this.username.textboxKeyTyped(typedChar, keyCode);
        this.token.textboxKeyTyped(typedChar, keyCode);
        this.password.textboxKeyTyped(typedChar, keyCode);
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (button.id == 3) {
            AltManager.login(this.username.getText(), this.token.getText());
        }
        if (button.id == 4) {
            this.mc.displayGuiScreen(null);
        }
    }
}
