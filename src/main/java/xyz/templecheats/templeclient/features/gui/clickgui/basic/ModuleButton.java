package xyz.templecheats.templeclient.features.gui.clickgui.basic;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.properties.BindButton;
import xyz.templecheats.templeclient.features.gui.font.FontUtils;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.util.setting.Setting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static xyz.templecheats.templeclient.features.gui.clickgui.basic.Panel.calculateRotation;

public class ModuleButton extends Button {
    private final Module module;
    private final List<Item> items = new ArrayList<>();
    private boolean subOpen;
    private long timeHovering;

    private int startScrollIdx;
    private int endScrollIdx;
    private int progress;
    private int scrollBarHeight;
    private int subScrollCtrHeight;

    private int wrapNum;
    
    public ModuleButton(Module module) {
        super(module.getName());
        this.module = module;
        progress = 0;
        this.startScrollIdx = 0;
        this.endScrollIdx = 0;
        this.wrapNum = 11;
        this.scrollBarHeight = 220;
        this.subScrollCtrHeight = 220;

        final List<Setting<?>> settings = TempleClient.settingsManager.getSettingsByMod(module);
        for (Setting<?> s : settings) {
            this.items.add(s.createBasicButton(this));
        }
        this.items.add(new BindButton(module));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if(!this.items.isEmpty()) {
            if(ClickGUI.INSTANCE.gears.booleanValue()) {
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/icons/gear.png"));
                GlStateManager.translate(getX() + getWidth() - 6.7F, getY() + 7.7F - 0.3F, 0.0F);
                if(this.subOpen) {
                    ++progress;
                    GlStateManager.rotate(calculateRotation((float) this.progress), 0.0F, 0.0F, 1.0F);
                }
                Gui.drawScaledCustomSizeModalRect(-5, -5, 0.0F, 0.0F, 10, 10, 10, 10, 10.0F, 10.0F);
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }

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

