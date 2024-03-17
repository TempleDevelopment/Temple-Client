package xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.features.gui.clickgui.csgo.Button;
import xyz.templecheats.templeclient.features.gui.clickgui.csgo.ClientCsgoGuiScreen;
import xyz.templecheats.templeclient.features.gui.clickgui.csgo.Item;
import xyz.templecheats.templeclient.features.gui.font.FontUtils;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.util.setting.Setting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

public class ModuleButton extends Button {
    private final Module module;
    private final List<Item> items = new ArrayList<>();

    private long timeHovering;
    
    public ModuleButton(Module module) {
        super(module.getName());
        this.module = module;

        this.items.add(new BindButton(module));
        
        final List<Setting<?>> settings = TempleClient.settingsManager.getSettingsByMod(module);
        for (Setting<?> s : settings) {
            this.items.add(s.createCsgoButton(this));
        }
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if(!this.items.isEmpty()) {            
            GlStateManager.pushMatrix();
            GlStateManager.translate(this.x, this.y, 0);
            GlStateManager.enableDepth(); // Enable depth testing
            GlStateManager.depthFunc(GL11.GL_LEQUAL); // Configure depth function
            if (items.size() > 1) {
                // start draw around
                RenderUtil.drawRect(0, 0, 0+5, 0+1, 0xFF282826);
                RenderUtil.drawRect(5+(int)FontUtils.getStringWidth(this.getLabel())*0.8f+0, 0, 0+this.width, 0+1, 0xFF282826);
                RenderUtil.drawRect(0, 0, 0+1, 0+this.height, 0xFF282826);
                RenderUtil.drawRect(0+this.width, 0, 0+this.width+1, 0+this.height, 0xFF282826);
                RenderUtil.drawRect(0, 0+this.height, 0+this.width, 0+this.height+1, 0xFF282826);
                // end draw around
            }
            
            GlStateManager.scale(0.8, 0.8, 1);
            FontUtils.drawString(this.getLabel(), 5/0.8, (float)(-2/0.8), 0xFFFFFFFF, false);
            GlStateManager.disableDepth(); // Disable depth testing after drawing
            GlStateManager.popMatrix();
            
            int minH=5;
            for(int i=0, index=0; i<items.size(); ) {
                if (index == 1) {
                    if (items.get(i-1).getHeight() != items.get(i).getHeight()) {
                        index = 0;
                        minH += items.get(i-1).getHeight() + 2;
                    }
                }

                items.get(i).setLocation(this.x + this.width/2 * index, this.y + minH);
                items.get(i).setWidth(this.width / 2);
                items.get(i).drawScreen(mouseX, mouseY, partialTicks);
                index = index + 1;

                if (index == 2) {
                    index = 0;
                    minH += items.get(i-1).getHeight() + 2;
                }

                i++;

                if (i == items.size() && index == 1) {
                    minH += items.get(i-1).getHeight() + 2;
                }
            }

            this.setHeight(minH);
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
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
            
            for(Item item : items) {
                System.out.println(item.getLabel()+", "+item.getHeight());
                item.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }
    
    @Override
    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        super.mouseReleased(mouseX, mouseY, releaseButton);
        if(!items.isEmpty()) {
            for(Item item : items) {
                item.mouseReleased(mouseX, mouseY, releaseButton);
            }
        }
    }
    
    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if(!items.isEmpty()) {
            for(Item item : items) {
                item.keyTyped(typedChar, keyCode);
            }
        }
    }
    
    @Override
    public int getHeight() {
        return this.height;
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
    public ClientCsgoGuiScreen getClientScreen() {
        return ClientCsgoGuiScreen.getInstance();
    }
}