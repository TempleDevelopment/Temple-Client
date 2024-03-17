package xyz.templecheats.templeclient.features.gui.menu.alt;

import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.features.gui.menu.CustomMainMenu;

import java.awt.*;
import java.io.IOException;
import java.net.Proxy;

public class AltManager extends GuiScreen {
    GuiTextField inputField;
    private final int buttonWidth = 100;
    private final int buttonHeight = 20;
    private final int fieldWidth = 200;
    private final ResourceLocation background = new ResourceLocation("textures/gui/background.jpg");

    @Override
    public void initGui() {
        int centerY = this.height / 2;
        int centerX = this.width / 2;

        this.buttonList.clear();

        inputField = new GuiTextField(1, fontRenderer, centerX - fieldWidth / 2, centerY, fieldWidth, 20);
        inputField.setText("Username");

        this.buttonList.add(new GuiButton(1, centerX - buttonWidth / 2, centerY + 32, buttonWidth, buttonHeight, "Login") {
            @Override
            public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
                super.drawButton(mc, mouseX, mouseY, partialTicks);
                drawCenteredString(mc.fontRenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, Color.WHITE.getRGB());
            }
        });
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
        Minecraft.getMinecraft().getTextureManager().bindTexture(background);
        drawScaledCustomSizeModalRect(0, 0, 0, 0, this.width, this.height, this.width, this.height, this.width, this.height);

        for (Object aButtonList : this.buttonList) {
            ((GuiButton) aButtonList).drawButton(this.mc, mouseX, mouseY, partialTicks);
        }

        inputField.drawTextBox();
        String usernameText = "Your Username: " + mc.getSession().getUsername();
        int textWidth = mc.fontRenderer.getStringWidth(usernameText);
        mc.fontRenderer.drawStringWithShadow(usernameText, (this.width - textWidth) / 2.0f, this.height / 2.0f - 20, Color.yellow.getRGB());
    }

    private void drawGradientBackground() {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glColor3f(0.4f, 0.4f, 0.4f);
        GL11.glVertex2f(0, 0);
        GL11.glVertex2f(this.width, 0);
        GL11.glColor3f(0.2f, 0.2f, 0.2f);
        GL11.glVertex2f(this.width, this.height);
        GL11.glVertex2f(0, this.height);
        GL11.glEnd();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
}