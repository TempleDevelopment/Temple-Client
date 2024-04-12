package xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.items.buttons;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.items.Item;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.Panel;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;

public class IntSlider extends Item {
    private final Button parentButton;
    private final IntSetting setting;
    private boolean dragging;
    private int value;

    public IntSlider(IntSetting setting, Button parentButton) {
        super(setting.name);
        this.setting = setting;
        this.parentButton = parentButton;
        this.width = 15;
        this.value = setting.intValue();
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        double range = setting.max - setting.min;
        double percentBar = (value - setting.min) / range;

        RenderUtil.drawRect(x, y, (float) (this.x + (percentBar * (this.width + 7.4f))), y + height, ClickGUI.INSTANCE.getStartColor().getRGB());

        if(this.isHovering(mouseX, mouseY)) {
            RenderUtil.drawRect(x, y, (float) (this.x + (percentBar * (this.width + 7.4f))), y + height, 0x22000000);
        }

        parentButton.font16.drawString(getLabel() + TextFormatting.GRAY + " " + value, this.x + 2.3, this.y + 4, 0xFFFFFFFF, false, 1.0f);

        if(this.dragging) {
            int offset = (int) (MathHelper.clamp((mouseX - x) / (this.width + 7.4f), 0, 1) * range);
            this.value = setting.min + offset;

            if(!(this.setting.parent instanceof ClickGUI) || !this.setting.name.equals("Scale")) {
                this.setting.setIntValue(this.value);
            }
        } else {
            this.value = setting.intValue();
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
            this.setting.setIntValue(this.value);
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
