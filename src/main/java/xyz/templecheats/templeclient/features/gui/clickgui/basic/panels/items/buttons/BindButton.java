package xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.items.buttons;

import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.ClickGuiScreen;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.ClientGuiScreen;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.render.RenderUtil;

import java.io.IOException;

import static xyz.templecheats.templeclient.features.gui.font.Fonts.font16;

public class BindButton extends Button {
    private final Module module;
    private boolean listening;

    public BindButton(Module module) {
        super("Keybind");
        this.module = module;
        height = 15;
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        RenderUtil.drawRect(x, y, x + getWidth() + 7.4F, y + height, !isHovering(mouseX, mouseY) ? 0x11555555 : 0x88555555);
        String s = listening ? "..." : Keyboard.getKeyName(module.getKey());
        font16.drawString(getLabel() + " " + TextFormatting.GRAY + s, this.x + 2.3, this.y + 4, 0xFFFFFFFF, false);
    }

    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if(this.isHovering(mouseX, mouseY)) {
            if(mouseButton == 0) {
                listening = true;
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
        return 14;
    }
    
    @Override
    public void toggle() {}
    
    @Override
    public boolean getState() {
        return false;
    }
    
    @Override
    public ClientGuiScreen getClientScreen() {
        return ClickGuiScreen.getInstance();
    }
}
