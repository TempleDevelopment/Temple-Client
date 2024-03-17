package xyz.templecheats.templeclient.features.gui.clickgui.csgo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import xyz.templecheats.templeclient.features.gui.font.FontUtils;
import xyz.templecheats.templeclient.util.render.RenderUtil;

public abstract class Button extends Item {
    private boolean state;
    
    public Button(String label) {
        super(label);
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
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
        return this.height;
    }
    
    public abstract ClientCsgoGuiScreen getClientScreen();
    
    protected boolean isHovering(int mouseX, int mouseY) {
        return (float) this.getX() <= mouseX && (float) mouseX <= this.getX() + (float) this.getWidth() && this.getY() <= (float) mouseY && (float) mouseY <= this.getY() + (float) this.height;
    }
}