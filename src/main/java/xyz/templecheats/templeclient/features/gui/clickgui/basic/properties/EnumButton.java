package xyz.templecheats.templeclient.features.gui.clickgui.basic.properties;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.Button;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.ClientGuiScreen;
import xyz.templecheats.templeclient.features.gui.font.FontUtils;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;

public class EnumButton<T extends Enum<T>> extends Button {
    private final Button parentButton;
    private final EnumSetting<T> setting;
    private final T[] values;
    
    public EnumButton(EnumSetting<T> setting, Button parentButton) {
        super(setting.name);
        this.setting = setting;
        this.parentButton = parentButton;
        this.values = setting.getValues();
        width = 15;
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        RenderUtil.drawRect(x, y, x + width + 7.4F, y + height, !isHovering(mouseX, mouseY) ? 0x11333333 : 0x88333333);
        
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.x + 2.3, this.y + 4, 0);
        GlStateManager.scale(0.8, 0.8, 0);
        FontUtils.drawString(getLabel() + TextFormatting.GRAY + " " + setting.value().toString(), 0, 0, 0xFFFFFFFF, false);
        GlStateManager.popMatrix();
    }
    
    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if(isHovering(mouseX, mouseY)) {
            int count = this.setting.index();
            if(mouseButton == 0) {
                count++;
            } else if(mouseButton == 1) {
                count--;
            } else {
                return;
            }

            if (count > values.length - 1) count = 0;
            else if (count < 0) count = values.length - 1;
            setting.setValue(values[count]);

            if(this.isHovering(mouseX, mouseY) && mouseButton == 0) {
                if(this.getLabel().equals("Themes")) {
                    Minecraft.getMinecraft().displayGuiScreen(null);
                    Minecraft.getMinecraft().setIngameFocus();
                }
            }
        }
    }
    
    @Override
    public int getHeight() {
        return 14;
    }
    
    @Override
    public boolean getState() {
        return false;
    }
    
    @Override
    public ClientGuiScreen getClientScreen() {
        return this.parentButton.getClientScreen();
    }
}
