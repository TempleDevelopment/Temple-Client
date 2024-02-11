package xyz.templecheats.templeclient.impl.gui.clickgui.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.api.setting.Setting;
import xyz.templecheats.templeclient.api.util.render.RenderUtil;
import xyz.templecheats.templeclient.impl.gui.clickgui.ClickGuiScreen;
import xyz.templecheats.templeclient.impl.gui.clickgui.ClientGuiScreen;
import xyz.templecheats.templeclient.impl.gui.clickgui.item.properties.BindButton;
import xyz.templecheats.templeclient.impl.gui.clickgui.item.properties.BooleanButton;
import xyz.templecheats.templeclient.impl.gui.clickgui.item.properties.ModeButton;
import xyz.templecheats.templeclient.impl.gui.clickgui.item.properties.NumberSlider;
import xyz.templecheats.templeclient.impl.gui.font.FontUtils;
import xyz.templecheats.templeclient.impl.modules.Module;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ModuleButton extends Button {
    private final Module module;
    private final List<Item> items = new ArrayList<>();
    private boolean subOpen;
    private long timeHovering;
    
    public ModuleButton(Module module) {
        super(module.getName());
        this.module = module;
        
        final ArrayList<Setting> settings = TempleClient.settingsManager.getSettingsByMod(module);
        if(settings != null) {
            for(Setting s : settings) {
                if(s.isCombo()) {
                    this.items.add(new ModeButton(s, this));
                } else if(s.isSlider()) {
                    this.items.add(new NumberSlider(s, this));
                } else if(s.isCheck()) {
                    this.items.add(new BooleanButton(s, this));
                }
            }
        }
        this.items.add(new BindButton(module));
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if(!this.items.isEmpty()) {
            //exeter thing
            //FontUtils.drawString("...", this.x - 1.0f + (float)this.width - 8.0f, this.y - 2.0f, -1, false);
            
            //future thing
			/*GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/gear.png"));
			GlStateManager.translate(getX() + getWidth() - 6.7F, getY() + 7.7F - 0.3F, 0.0F);
			GlStateManager.rotate(calculateRotation((float) this.progress), 0.0F, 0.0F, 1.0F);
			Gui.drawScaledCustomSizeModalRect(-5, -5, 0.0F, 0.0F, 10, 10, 10, 10, 10.0F, 10.0F);
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();*/
            
            //templeclient thing
            FontUtils.drawString(!this.subOpen ? "+" : "-", this.x - 1.0f + (float) this.width - 8.0f, this.y + 4.0f, -1, false);
            
            if(this.subOpen) {
                float height = 1.0f;
                for(Item item : items) {
                    item.setLocation(this.x + 1.0f, this.y + (height += 15.0f));
                    item.setHeight(15);
                    item.setWidth(this.width - 9);
                    item.drawScreen(mouseX, mouseY, partialTicks);
                }
            }
        }
    }
    
    @Override
    public void drawScreenPost(int mouseX, int mouseY) {
        if(this.isHovering(mouseX, mouseY)) {
            if(this.timeHovering == 0) {
                this.timeHovering = System.currentTimeMillis();
            }
            
            if(System.currentTimeMillis() - this.timeHovering > 500) {
                final String description = module.getDescription();
                final float startX = mouseX + 7;
                final float startY = mouseY + 7;
                final float width = (float) FontUtils.getStringWidth(description);
                final float height = (float) FontUtils.getFontHeight();
                
                RenderUtil.drawRect(startX - 1, startY - 1, startX + width, startY + height, 0x88000000);
                FontUtils.drawString(description, startX, startY, -1, false);
            }
        } else {
            this.timeHovering = 0;
        }
    }
    
    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if(!this.items.isEmpty()) {
            if(mouseButton == 1 && this.isHovering(mouseX, mouseY)) {
                this.subOpen = !this.subOpen;
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
            if(this.subOpen) {
                for(Item item : items) {
                    item.mouseClicked(mouseX, mouseY, mouseButton);
                }
            }
        }
    }
    
    @Override
    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        super.mouseReleased(mouseX, mouseY, releaseButton);
        if(!items.isEmpty() && subOpen) {
            for(Item item : items) {
                item.mouseReleased(mouseX, mouseY, releaseButton);
            }
        }
    }
    
    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if(!items.isEmpty() && subOpen) {
            for(Item item : items) {
                item.keyTyped(typedChar, keyCode);
            }
        }
    }
    
    @Override
    public int getHeight() {
        if(this.subOpen) {
            int height = 14;
            for(Item item : items) {
                height += item.getHeight() + 1;
            }
            return height + 2;
        }
        return 14;
    }
    
    @Override
    public void toggle() {
        module.toggle();
    }
    
    @Override
    public boolean getState() {
        return module.isEnabled();
    }
    
    @Override
    public ClientGuiScreen getClientScreen() {
        return ClickGuiScreen.getInstance();
    }
}

