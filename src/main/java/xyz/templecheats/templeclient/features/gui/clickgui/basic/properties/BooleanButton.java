package xyz.templecheats.templeclient.features.gui.clickgui.basic.properties;

import net.minecraft.client.renderer.GlStateManager;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.Button;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.ClientGuiScreen;
import xyz.templecheats.templeclient.features.gui.font.FontUtils;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;

public class BooleanButton extends Button {
    private final Button parentButton;
    private final BooleanSetting setting;
    
    public BooleanButton(BooleanSetting setting, Button parentButton) {
        super(setting.name);
        this.setting = setting;
        this.parentButton = parentButton;
        width = 15;
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        if(this.getState()) {
            RenderUtil.drawRect(x, y, x + width + 7.4F, y + height, ClickGUI.INSTANCE.getStartColor());
            
            if(this.isHovering(mouseX, mouseY)) {
                RenderUtil.drawRect(x, y, x + width + 7.4F, y + height, 0x22000000);
            }
        } else {
            RenderUtil.drawRect(x, y, x + width + 7.4F, y + height, !this.isHovering(mouseX, mouseY) ? 0x11555555 : 0x88555555);
        }
        
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.x + 2.3, this.y + 4, 0);
        GlStateManager.scale(0.8, 0.8, 0);
        FontUtils.drawString(getLabel(), 0, 0, 0xFFFFFFFF, false);
        GlStateManager.popMatrix();
    }
    
    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public int getHeight() {
        return 14;
    }
    
    @Override
    public void toggle() {
        setting.setBooleanValue(!setting.booleanValue());
    }
    
    @Override
    public boolean getState() {
        return setting.booleanValue();
    }
    
    @Override
    public ClientGuiScreen getClientScreen() {
        return this.parentButton.getClientScreen();
    }
}
