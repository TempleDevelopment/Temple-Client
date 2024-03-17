package xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import xyz.templecheats.templeclient.features.gui.clickgui.csgo.Button;
import xyz.templecheats.templeclient.features.gui.clickgui.csgo.ClientCsgoGuiScreen;
import xyz.templecheats.templeclient.features.gui.font.FontUtils;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;

import java.io.IOException;

public class BindButton extends Button {
    private final Module module;
    private boolean listening;
    
    public BindButton(Module module) {
        super("Keybind");
        this.module = module;
        this.height = 10;
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.x+3, this.y, 0);
        GlStateManager.enableDepth(); // Enable depth testing
        GlStateManager.depthFunc(GL11.GL_LEQUAL); // Configure depth function

        if(this.getState()) { // if selected
            RenderUtil.drawRect(0, 4, 6, 10, ClickGUI.INSTANCE.getStartColor());
            
        } else { // if not selected
            RenderUtil.drawRect(0, 4, 6, 10, 0x88555555);
        }
        
        if(this.isHovering(mouseX, mouseY)) {
            RenderUtil.drawRect(0, 4, 6, 10, 0x22000000);
        }
        RenderUtil.drawGradientRect(0, 4, 6, 10, 0x33555555, 0xAA333333);
        
        GlStateManager.scale(0.6, 0.6, 1);
        FontUtils.drawString("Enable", 10/0.6, 8, 0xFFD2D2D2, false);
        String s;
        if (listening) {
            s = " . . . "; // Set s to some default string if listening is true
        } else {
            String keyName = Keyboard.getKeyName(module.getKey());
            s = (module.getKey() != Keyboard.KEY_NONE) ? keyName : "-"; // If keyName is not null, use it. Otherwise, set s to "[-]"
        }
        FontUtils.drawString(TextFormatting.DARK_GRAY + "["+ s +"]", 35/0.6, 8, 0xFFFFFFFF, false);
        GlStateManager.disableDepth(); // Disable depth testing after drawing
        GlStateManager.popMatrix();
    }
    
    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0 && this.x <= mouseX && mouseX <= this.x + 30 && this.y <= mouseY && mouseY <= this.y+10) {
            this.toggle();
        }

        if (mouseButton == 0) {
            if (this.x+35 <= mouseX && mouseX <= this.x + this.getWidth() && this.y <= mouseY && mouseY <= this.y+10) {
                listening = true;
            } else {
                listening = false;
            }
        }
    }
    
    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if(listening) {
            if(keyCode != Keyboard.KEY_ESCAPE && keyCode != Keyboard.KEY_DELETE && keyCode != Keyboard.KEY_BACK) {
                module.setKey(keyCode);
            } else {
                module.setKey(Keyboard.KEY_NONE);
            }
            listening = false;
        }
    }
    
    @Override
    public int getHeight() {
        return this.height;
    }
    
    @Override
    public void toggle() {
        module.toggle();
    }
    
    @Override
    public boolean getState() {
        return module.isEnabled();
    }

    @Override
    public ClientCsgoGuiScreen getClientScreen() {
        return ClientCsgoGuiScreen.getInstance();
    }
}
