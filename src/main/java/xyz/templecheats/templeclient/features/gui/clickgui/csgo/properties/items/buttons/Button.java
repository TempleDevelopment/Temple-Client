package xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import xyz.templecheats.templeclient.features.gui.clickgui.csgo.CsgoGuiScreen;
import xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.Item;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;

import java.awt.*;

public abstract class Button extends Item {
    private boolean state;
    public Color[] color;

    public Button(String label) {
        super(label);
        this.color = new Color[]{ClickGUI.INSTANCE.getStartColor(), ClickGUI.INSTANCE.getEndColor()};
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
    }

    public void toggle() {
        this.state = !this.state;
    }

    public boolean getState() {
        return this.state;
    }

    public abstract CsgoGuiScreen getClientScreen();

    protected boolean isHovering(int mouseX, int mouseY) {
        return this.getX() <= mouseX && (float) mouseX <= this.getX() + (float) this.getWidth() && this.getY() <= (float) mouseY && (float) mouseY <= this.getY() + (float) this.height;
    }
}