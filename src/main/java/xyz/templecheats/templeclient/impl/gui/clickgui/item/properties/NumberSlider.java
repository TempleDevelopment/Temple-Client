package xyz.templecheats.templeclient.impl.gui.clickgui.item.properties;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import xyz.templecheats.templeclient.api.setting.Setting;
import xyz.templecheats.templeclient.api.util.render.RenderUtil;
import xyz.templecheats.templeclient.impl.gui.clickgui.Panel;
import xyz.templecheats.templeclient.impl.gui.clickgui.item.Button;
import xyz.templecheats.templeclient.impl.gui.clickgui.item.Item;
import xyz.templecheats.templeclient.impl.gui.font.FontUtils;
import xyz.templecheats.templeclient.impl.modules.client.ClickGUI;

public class NumberSlider extends Item {
    private final Button parentButton;
    private final Setting property;
    private boolean dragging, useDoubles;
    private double min, max;
    private double value;
    
    public NumberSlider(Setting property, Button parentButton) {
        super(property.getName());
        this.property = property;
        this.parentButton = parentButton;
        this.width = 15;
        this.useDoubles = !property.onlyInt();
        this.value = this.useDoubles ? property.getValDouble() : property.getValInt();
        this.min = property.getMin();
        this.max = property.getMax();
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        final double val = this.useDoubles ? this.value : (int) this.value;
        double percentBar = (val - min) / (max - min);
        
        RenderUtil.drawRect(x, y, (float) (this.x + (percentBar * (this.width + 7.4f))), y + height, ClickGUI.INSTANCE.getStartColor());
        
        if(this.isHovering(mouseX, mouseY)) {
            RenderUtil.drawRect(x, y, (float) (this.x + (percentBar * (this.width + 7.4f))), y + height, 0x22000000);
        }
        
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.x + 2.3, this.y + 4, 0);
        GlStateManager.scale(0.8, 0.8, 0);
        FontUtils.drawString(getLabel() + TextFormatting.GRAY + " " + (this.useDoubles ? Math.round(val * 100D) / 100D : String.valueOf((int) val)), 0, 0, 0xFFFFFFFF, false);
        GlStateManager.popMatrix();
        
        if(this.dragging) {
            this.value = min + (MathHelper.clamp((mouseX - x) / (this.width + 7.4f), 0, 1)) * (max - min);
            
            if(!(this.property.getParentMod() instanceof ClickGUI) || !this.getLabel().equals("Scale")) {
                this.property.setValDouble(this.useDoubles ? this.value : (int) this.value);
            }
        } else {
            this.value = this.useDoubles ? property.getValDouble() : property.getValInt();
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
            this.property.setValDouble(this.useDoubles ? this.value : (int) this.value);
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
