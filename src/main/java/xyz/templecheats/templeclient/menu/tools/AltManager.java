package xyz.templecheats.templeclient.menu.tools;

import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.menu.CustomMainMenu;
import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.Session;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import xyz.templecheats.templeclient.menu.CustomMainMenu;

import java.awt.*;
import java.io.IOException;
import java.net.Proxy;

public class AltManager extends GuiScreen {
    GuiTextField inputField;

    @Override
    public void initGui() {
        int i = this.height / 4 + 48;

        this.buttonList.clear();

        inputField = new GuiTextField(1, fontRenderer, this.width / 2 - 100, i + 72 - 12, 200, 20);
        inputField.setText("Username");

        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, i + 72 + 12, 200, 20, "Login"));
    }

    public static void changeName(String name) {
        YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
        YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication)service.createUserAuthentication(Agent.MINECRAFT);
        auth.logOut();

        Session session = new Session(name, name, "0", "legacy");

        try {
            TempleClient.setSession(session);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 1) {
            changeName(inputField.getText());
        }
    }

    @Override
    public void updateScreen() {
        inputField.updateCursorCounter();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        switch (keyCode) {
            case Keyboard.KEY_ESCAPE:
                mc.displayGuiScreen(new CustomMainMenu());
                break;
            default:
                inputField.textboxKeyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        inputField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GL11.glColor4f(1, 1, 1, 1);
        drawDefaultBackground();

        for (int i = 0; i < this.buttonList.size(); i++) {
            ((GuiButton) this.buttonList.get(i)).drawButton(this.mc, mouseX, mouseY, partialTicks);
        }

        inputField.drawTextBox();
        mc.fontRenderer.drawStringWithShadow("Your Username: " + mc.getSession().getUsername(), this.width / 2 - 100, this.height / 4 + 48 + 110, Color.yellow.getRGB());
    }
}