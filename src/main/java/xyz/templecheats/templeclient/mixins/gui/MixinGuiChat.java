package xyz.templecheats.templeclient.mixins.gui;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.manager.CommandManager;

import static org.lwjgl.opengl.GL11.*;
import static xyz.templecheats.templeclient.util.Globals.mc;

@Mixin(value = {
        GuiChat.class
})
public abstract class MixinGuiChat {

    private boolean shouldDrawOutline;

    @Shadow
    protected GuiTextField inputField;

    @Inject(method = "keyTyped(CI)V", at = @At("RETURN"))
    public void keyTypedHook(char typedChar, int keyCode, CallbackInfo info) {
        if ((mc.currentScreen instanceof GuiChat)) {
            String commandPrefix = ".";
            shouldDrawOutline = inputField.getText().startsWith(commandPrefix);

            if (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER) {
                String text = inputField.getText();

                if (text.startsWith(commandPrefix)) {
                    text = text.substring(commandPrefix.length());

                    CommandManager commandManager = new CommandManager();
                    commandManager.executeCommand(text);

                    inputField.setText("");
                }
            }
        } else {
            shouldDrawOutline = false;
        }
    }

    @Inject(method = "drawScreen", at = @At("TAIL"))
    public void drawScreenHook(int mouseX, int mouseY, float partialTicks, CallbackInfo info) {

        if (shouldDrawOutline) {

            boolean blend = glIsEnabled(GL_BLEND);
            boolean texture2D = glIsEnabled(GL_TEXTURE_2D);

            glDisable(GL_BLEND);
            glDisable(GL_TEXTURE_2D);

            float red = (ClickGUI.INSTANCE.getStartColor().getRGB() >> 16 & 0xFF) / 255.0f;
            float green = (ClickGUI.INSTANCE.getStartColor().getRGB() >> 8 & 0xFF) / 255.0f;
            float blue = (ClickGUI.INSTANCE.getStartColor().getRGB() & 0xFF) / 255.0f;

            GL11.glColor3f(red, green, blue);

            glLineWidth(1.5f);
            glBegin(GL_LINES);

            int x = inputField.x - 2;
            int y = inputField.y - 2;
            int width = inputField.width;
            int height = inputField.height;

            glVertex2d(x, y);
            glVertex2d(x + width, y);
            glVertex2d(x + width, y);
            glVertex2d(x + width, y + height);
            glVertex2d(x + width, y + height);
            glVertex2d(x, y + height);
            glVertex2d(x, y + height);
            glVertex2d(x, y);

            glEnd();

            if (blend) {
                glEnable(GL_BLEND);
            }

            if (texture2D) {
                glEnable(GL_TEXTURE_2D);
            }
        }
    }
}