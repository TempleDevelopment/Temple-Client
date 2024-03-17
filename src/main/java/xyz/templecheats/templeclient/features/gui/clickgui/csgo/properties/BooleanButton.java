package xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import xyz.templecheats.templeclient.features.gui.clickgui.csgo.Button;
import xyz.templecheats.templeclient.features.gui.clickgui.csgo.ClientCsgoGuiScreen;
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
        this.height = 12;
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.x+3, this.y, 0);
        GlStateManager.enableDepth(); // Enable depth testing
        GlStateManager.depthFunc(GL11.GL_LEQUAL); // Configure depth function
        
        if(this.getState()) { // if selected
            RenderUtil.drawRect(0, 4, 6, 10, ClickGUI.INSTANCE.getStartColor());
            
        } else { // if not selected
            RenderUtil.drawRect(0, 4, 6, 10, 0x88555555);
        }
        
        RenderUtil.drawGradientRect(0, 4, 6, 10, 0x33555555, 0xAA333333);

        if(this.isHovering(mouseX, mouseY)) {
            RenderUtil.drawRect(0, 4, 6, 10, 0x22000000);
        }
        
        GlStateManager.scale(0.6, 0.6, 1);
        FontUtils.drawString(getLabel(), 10/0.6, 8, 0xFFD2D2D2, false);
        GlStateManager.disableDepth(); // Disable depth testing after drawing
        GlStateManager.popMatrix();
    }
    
    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        if(mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
            this.toggle();
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public int getHeight() {
        return this.height;
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
    public ClientCsgoGuiScreen getClientScreen() {
        return this.parentButton.getClientScreen();
    }
}
