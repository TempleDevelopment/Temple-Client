package xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.items.buttons;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.items.Item;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.Panel;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;

import static xyz.templecheats.templeclient.features.gui.font.Fonts.font16;

public class DoubleSlider extends Item {
    private final Button parentButton;
    private final DoubleSetting setting;
    private boolean dragging;
    private double value;

    public DoubleSlider(DoubleSetting setting, Button parentButton) {
        super(setting.name);
        this.setting = setting;
        this.parentButton = parentButton;
        this.height = 15;
        this.value = setting.doubleValue();
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        double range = setting.max - setting.min;
        double percentBar = (value - setting.min) / range;

        RenderUtil.drawRect(x, y, (float) (this.x + (percentBar * (this.getWidth() + 7.4f))), y + height, ClickGUI.INSTANCE.getStartColor().getRGB());

        if(this.isHovering(mouseX, mouseY)) {
            RenderUtil.drawRect(x, y, (float) (this.x + (percentBar * (this.getWidth() + 7.4f))), y + height, 0x22000000);
        }
        font16.drawString(getLabel() + TextFormatting.GRAY + " " + Math.round(value * 100D) / 100D, this.x + 2.3, this.y + 4, 0xFFFFFFFF, false);

        if(this.dragging) {
            double offset = (MathHelper.clamp((mouseX - x) / (this.getWidth() + 7.4f), 0, 1) * range);
            this.value = setting.min + offset;

            if(!(this.setting.parent instanceof ClickGUI) || !this.setting.name.equals("Scale")) {
                this.setting.setDoubleValue(this.value);
            }
        } else {
            this.value = setting.doubleValue();
        }
    }

    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        if(this.isHovering(mouseX, mouseY) && mouseButton == 0) {
            this.dragging = true;
            return;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if(state == 0 && this.dragging) {
            this.setting.setDoubleValue(this.value);
            this.dragging = false;
            return;
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public int getHeight() {
        return 14;
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
}
