package xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.buttons;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatAllowedCharacters;
import xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.Item;
import xyz.templecheats.templeclient.util.math.Vec2d;
import xyz.templecheats.templeclient.util.render.shader.impl.RectBuilder;
import xyz.templecheats.templeclient.util.setting.impl.StringSetting;
import xyz.templecheats.templeclient.util.time.TimerUtil;

import java.awt.*;

import static xyz.templecheats.templeclient.features.gui.font.Fonts.font12;
import static xyz.templecheats.templeclient.features.gui.font.Fonts.font14;

public class StringButton extends Item {
    private final StringSetting setting;
    private boolean typing;
    private CurrentString currentString = new StringButton.CurrentString("");
    private final TimerUtil idleTimer = new TimerUtil();
    private boolean idling;

    public StringButton(String label, StringSetting setting) {
        super(label);
        this.setting = setting;
        this.height = 21;
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        new RectBuilder(new Vec2d(x + 1, y + 6), new Vec2d(x + width + 7.5, y + height))
                .outlineColor(new Color(0x494949)).width(0.8)
                .color(!isHovering(mouseX, mouseY) ? new Color(0x262626) : new Color(0x2A2A2A))
                .radius(2.0).draw();
        new RectBuilder(new Vec2d(x + 2, y + 5), new Vec2d(x + font12.getStringWidth(this.setting.getName()) + 2, y + 8))
                .color(new Color(25, 25, 25))
                .radius(2.0).draw();

        GlStateManager.pushMatrix();
        font12.drawString(this.setting.getName(), this.x + 2.3, this.y + 3, -1, false);
        if (this.typing) {
            font14.drawString(this.currentString.getString() + typingIcon(), this.x + 5f, this.y + 10, -1, false);
        } else {
            font14.drawString(this.setting.value(), this.x + 5f, this.y + 10, -1, false);
        }
        GlStateManager.popMatrix();
    }

    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        if (this.isHovering(mouseX, mouseY) && mouseButton == 0) {
            toggle();
            return;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    protected boolean isHovering(int mouseX, int mouseY) {
        return this.getX() <= mouseX && (float) mouseX <= this.getX() + (float) this.getWidth() && this.getY() <= (float) mouseY && (float) mouseY <= this.getY() + (float) this.height;
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
                    setString(removeLastChar(currentString.getString()));
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

    public static String removeLastChar(String str) {
        String output = "";
        if (str != null && !str.isEmpty()) {
            output = str.substring(0, str.length() - 1);
        }
        return output;
    }

    public void toggle() {
        typing = !typing;
    }

    public boolean getState() {
        return !typing;
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

        public CurrentString(String string) {
            this.string = string;
        }

        public String getString() {
            return this.string;
        }
    }
}
