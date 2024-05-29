package xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.items.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.ClientGuiScreen;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.Panel;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.items.Item;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.util.math.Vec2d;
import xyz.templecheats.templeclient.util.render.shader.impl.RectBuilder;

import java.awt.*;

import static xyz.templecheats.templeclient.features.gui.font.Fonts.font18;
import static xyz.templecheats.templeclient.util.color.ColorUtil.setAlpha;

public abstract class Button extends Item {
    public boolean state;

    public Button(String label) {
        super(label);
        this.height = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Color c1 = ClickGUI.INSTANCE.getStartColor();
        Color c2 = ClickGUI.INSTANCE.getEndColor();

        switch (ClickGUI.INSTANCE.colorMode.value()) {
            case Static:
                c1 = new Color(ClickGUI.INSTANCE.getClientColor(10, (int) Panel.counter1[0]));
                c2 = new Color(ClickGUI.INSTANCE.getClientColor(10, (int) (Panel.counter1[0] + 1)));
                break;
            case Default:
                c1 = ClickGUI.INSTANCE.getStartColor();
                c2 = c1;
                break;
            case Gradient:
                c1 = ClickGUI.INSTANCE.getStartColor();
                c2 = ClickGUI.INSTANCE.getEndColor();
                break;
        }

        Color color1 = getState() ? isHovering(mouseX, mouseY) ? setAlpha(c1, 210) : c1 : isHovering(mouseX, mouseY) ? new Color(0x88555555, true) : new Color(0x33555555, true);
        Color color2 = getState() ? isHovering(mouseX, mouseY) ? setAlpha(c2, 210) : c2 : isHovering(mouseX, mouseY) ? new Color(0x99555555, true) : new Color(0x55555555, true);

        RectBuilder rectBuilder = new RectBuilder(new Vec2d(x, y), new Vec2d(x + getWidth(), y + height));
        if (ClickGUI.INSTANCE.way.value() == ClickGUI.Way.Horizontal) {
            rectBuilder.colorH(color1, color2);
        } else {
            rectBuilder.colorV(color1, color2);
        }
        rectBuilder.draw();
        font18.drawString(this.getLabel(), this.x + 2.0f, this.y + 4.0f, this.getState() ? -1 : -5592406, false);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
            this.toggle();
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        }
    }

    public void toggle() {
        this.state = !this.state;
    }

    public boolean getState() {
        return this.state;
    }

    public abstract ClientGuiScreen getClientScreen();

    protected boolean isHovering(int mouseX, int mouseY) {
        for (Panel panel : this.getClientScreen().getPanels()) {
            if (!panel.drag) continue;
            return false;
        }
        return (float) mouseX >= this.getX() && (float) mouseX <= this.getX() + (float) this.getWidth() && (float) mouseY >= this.getY() && (float) mouseY <= this.getY() + (float) this.height;
    }
}

