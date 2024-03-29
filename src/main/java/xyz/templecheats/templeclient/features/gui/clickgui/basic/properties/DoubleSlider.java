package xyz.templecheats.templeclient.features.gui.clickgui.basic.properties;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.Button;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.Item;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.Panel;
import xyz.templecheats.templeclient.features.gui.font.FontUtils;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;

public class DoubleSlider extends Item {
    private final Button parentButton;
    private final DoubleSetting setting;
    private boolean dragging;
    private double value;

    public DoubleSlider(DoubleSetting setting, Button parentButton) {
        super(setting.name);
        this.setting = setting;
        this.parentButton = parentButton;
        this.width = 15;
        this.value = setting.doubleValue();
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        double range = setting.max - setting.min;
        double percentBar = (value - setting.min) / range;

        RenderUtil.drawRect(x, y, (float) (this.x + (percentBar * (this.width + 7.4f))), y + height, ClickGUI.INSTANCE.getStartColor());

        if(this.isHovering(mouseX, mouseY)) {
            RenderUtil.drawRect(x, y, (float) (this.x + (percentBar * (this.width + 7.4f))), y + height, 0x22000000);
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(this.x + 2.3, this.y + 4, 0);
        GlStateManager.scale(0.8, 0.8, 0);
        FontUtils.drawString(getLabel() + TextFormatting.GRAY + " " + Math.round(value * 100D) / 100D, 0, 0, 0xFFFFFFFF, false);
        GlStateManager.popMatrix();

        if(this.dragging) {
            double offset = (MathHelper.clamp((mouseX - x) / (this.width + 7.4f), 0, 1) * range);
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

    private boolean isHovering(final int mouseX, final int mouseY) {
        for(final Panel panel : this.parentButton.getClientScreen().getPanels()) {
            if(panel.drag) {
                return false;
            }
        }
        return mouseX >= this.getX() && mouseX <= this.getX() + this.getWidth() && mouseY >= this.getY() && mouseY <= this.getY() + this.height;
    }
}
