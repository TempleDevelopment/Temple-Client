package xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.items.buttons;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatAllowedCharacters;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.Panel;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.items.Item;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.util.setting.impl.StringSetting;
import xyz.templecheats.templeclient.util.time.TimerUtil;

import static xyz.templecheats.templeclient.features.gui.font.Fonts.font16;

public class StringButton extends Item {
    private final Button parentButton;
    private final StringSetting setting;
    private boolean typing;
    private CurrentString currentString = new CurrentString("");
    private final TimerUtil idleTimer = new TimerUtil();
    private boolean idling;

    public StringButton(String label, Button parentButton, StringSetting setting) {
        super(label);
        this.parentButton = parentButton;
        this.setting = setting;
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        RenderUtil.drawRect(x, y, x + width + 7.4F, y + height, !isHovering(mouseX, mouseY) ? 0x11555555 : 0x88555555);

        GlStateManager.pushMatrix();
        if(this.typing) {
            font16.drawString(this.currentString.getString() + typingIcon(), this.x + 2.3f, this.y + 4, -1, false);
        } else {
            font16.drawString(this.setting.getName() + ": " + this.setting.value(), this.x + 2.3f, this.y + 4, -1,false);
        }
        GlStateManager.popMatrix();
    }

    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        if(this.isHovering(mouseX, mouseY) && mouseButton == 0) {
            toggle();
            return;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (typing) {
            switch (keyCode) {
                case 1: {
                    return;
                }
                case 28: {
                    enterString();
                }
                case 14: {
                    setString(StringButton.removeLastChar(currentString.getString()));
                }
            }
            if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                setString(currentString.getString() + typedChar);
            }
        }
    }

    private void enterString() {
        if (this.currentString.getString().isEmpty()) {
            this.setting.setValue(this.setting.getStringValue());
        } else {
            this.setting.setValue(this.currentString.getString());
        }
        this.setString("");
        toggle();
    }

    public void setString(String newString) {
        this.currentString = new CurrentString(newString);
    }

    public static String removeLastChar ( String str ) {
        String output = "";
        if ( str != null && !str.isEmpty()) {
            output = str.substring ( 0 , str.length ( ) - 1 );
        }
        return output;
    }

    public void toggle() {
        typing = !typing;
    }

    public boolean getState() {
        return !typing;
    }

    @Override
    protected boolean isHovering(final int mouseX, final int mouseY) {
        for(final Panel panel : this.parentButton.getClientScreen().getPanels()) {
            if(panel.drag) {
                return false;
            }
        }
        return mouseX >= this.getX() && mouseX <= this.getX() + this.getWidth() && mouseY >= this.getY() && mouseY <= this.getY() + this.height;
    }

    public String typingIcon() {
        if (idleTimer.passedMs(500L)) {
            idling = !idling;
            idleTimer.resetNT();
        }
        if (idling) {
            return "_";
        }
        return "";
    }

    public static class CurrentString {
        private final String string;

        public
        CurrentString(String string) {
            this.string = string;
        }

        public
        String getString() {
            return this.string;
        }
    }
}
