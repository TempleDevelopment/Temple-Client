package xyz.templecheats.templeclient.impl.gui.clickgui.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import xyz.templecheats.templeclient.api.util.render.RenderUtil;
import xyz.templecheats.templeclient.impl.gui.clickgui.ClientGuiScreen;
import xyz.templecheats.templeclient.impl.gui.clickgui.Panel;
import xyz.templecheats.templeclient.impl.gui.font.FontUtils;
import xyz.templecheats.templeclient.impl.modules.client.ClickGUI;

public abstract class Button extends Item {
    private boolean state;
    
    public Button(String label) {
        super(label);
        this.height = 15;
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        //  The original exeter
        //RenderUtil.drawGradientRect(this.x, this.y, this.x + (float) this.width, this.y + (float) this.height, this.getState() ? (!this.isHovering(mouseX, mouseY) ? 2012955202 : 1442529858) : (!this.isHovering(mouseX, mouseY) ? 0x33555555 : -2007673515), this.getState() ? (!this.isHovering(mouseX, mouseY) ? -1426374078 : -1711586750) : (!this.isHovering(mouseX, mouseY) ? 0x55555555 : -1722460843));
        
        // Future
        //RenderUtil.drawGradientRect(this.x, this.y, this.x + (float) this.width, this.y + (float) this.height, this.getState() ? (!this.isHovering(mouseX, mouseY) ? 0x77FB4242 : 0x55FB4242) : (!this.isHovering(mouseX, mouseY) ? 0x33555555 : 0x77AAAAAB), this.getState() ? (!this.isHovering(mouseX, mouseY) ? 0x77FB4242 : 0x55FB4242) : (!this.isHovering(mouseX, mouseY) ? 0x55555555 : 0x66AAAAAB));
        
        if(this.getState()) {
            //RenderUtil.drawGradientRect(x, y, x + width, y + height, !isHovering(mouseX, mouseY) ? 0x77fb4242 : 0x55fb4242, !isHovering(mouseX, mouseY) ? 0xAAfb4242 : 0x99fb4242);
            RenderUtil.drawGradientRect(x, y, x + width, y + height, ClickGUI.INSTANCE.getStartColor(), ClickGUI.INSTANCE.getEndColor());
            
            if(this.isHovering(mouseX, mouseY)) {
                RenderUtil.drawRect(x, y, x + width, y + height, 0x22000000);
            }
        } else {
            RenderUtil.drawGradientRect(x, y, x + width, y + height, !isHovering(mouseX, mouseY) ? 0x33555555 : 0x88555555, !isHovering(mouseX, mouseY) ? 0x55555555 : 0x99555555);
        }
        
        FontUtils.drawString(this.getLabel(), this.x + 2.0f, this.y + 4.0f, this.getState() ? -1 : -5592406, false);
    }
    
    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
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
    
    @Override
    public int getHeight() {
        return 14;
    }
    
    public abstract ClientGuiScreen getClientScreen();
    
    protected boolean isHovering(int mouseX, int mouseY) {
        for(Panel panel : this.getClientScreen().getPanels()) {
            if(!panel.drag) continue;
            return false;
        }
        return (float) mouseX >= this.getX() && (float) mouseX <= this.getX() + (float) this.getWidth() && (float) mouseY >= this.getY() && (float) mouseY <= this.getY() + (float) this.height;
    }
}

