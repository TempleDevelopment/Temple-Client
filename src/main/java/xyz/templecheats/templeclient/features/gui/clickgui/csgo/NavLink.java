package xyz.templecheats.templeclient.features.gui.clickgui.csgo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import xyz.templecheats.templeclient.features.gui.font.FontUtils;
import xyz.templecheats.templeclient.util.render.RenderUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class NavLink {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final String label;
    private int angle;
    private int x;
    private int y;
    private int x2;
    private int y2;
    private int width;
    private int height;
    public boolean open;
    public boolean drag;
    public int color = 0xFF747474;

    private final List<Item> items = new ArrayList<>();

    public NavLink(String label, int x, int y, boolean open) {
        this.label = label;
        this.x = x;
        this.y = y;
        this.angle = 180;
        this.width = 48;
        this.height = 48;
        this.open = open;
        this.setupItems();
        this.color = 0xFF131313;
    }
    
    public abstract void setupItems();

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // float totalItemHeight = this.open ? this.getTotalItemHeight() - 2.0f : 0.0f;
        RenderUtil.drawRect(this.x, (float) this.y, this.x + this.width, this.y + this.height, this.color);
        // RenderUtil.drawGradientRect(this.x, (float) this.y - 1.5f, this.x + this.width, this.y + this.height - 6, 0x77FB4242, 0x77FB4242);//0x77FB4242, 0x77FB4242);
        
        // Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/"+this.label+".png"));
        // Gui.drawModalRectWithCustomSizedTexture(this.x, this.y, 0, 0, 25, 25, 25, 25);

        // FontUtils.drawString(this.getLabel(), (float) this.x + 3.0f, (float) this.y + 1.5f/* - 4.0f*/, -1, false); //15592941
        if(open) {
            this.color = 0xFF131313;

            RenderUtil.drawRect(this.x, this.y+1, this.x+this.width, this.y, 0xFF282826);
            RenderUtil.drawRect(this.x, this.y+this.height, this.x+this.width, this.y+this.height-1, 0xFF282826);

            RenderUtil.drawRect(this.x + this.width, (float) this.y, this.x + this.width+1, this.y + this.height, this.color);
        } else {
            this.color = 0xFF0C0C0C;
        }

        GlStateManager.pushMatrix();
        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(this.open ? new ResourceLocation("textures/icons/"+this.label+"_selected.png") : new ResourceLocation("textures/icons/"+this.label+".png"));
        Gui.drawScaledCustomSizeModalRect(this.x + 13, this.y + 13, 0.0F, 0.0F, 22, 22, 22, 22, 22.0F, 22.0F);
        GlStateManager.popMatrix();

        if(this.open) {
            // float y = ClientCsgoGuiScreen.getInstance().y + 40;

            // sort the item from long to short
            for (int i = 0; i < items.size()-1; i++) {
                for (int j = i+1; j < items.size(); j++) {
                    if (items.get(i).getHeight() < items.get(j).getHeight()) {
                        Item temp = items.get(i);
                        items.set(i, items.get(j));
                        items.set(j, temp);
                    }
                }
            }

            int[] heights = {0, 0, 0};
            int left = 16;
            int bottomIndex = 0;

            // arrange the item
            for(Item item : getItems()) {
                if (item.getHeight() == 10 + 5 + 2) {
                    int minH = 354;
                    item.setLocation(this.x + 48 + (57 * (bottomIndex % 7)) + 16, minH + ClientCsgoGuiScreen.getInstance().y + 22 * (bottomIndex/7));
                    item.drawScreen(mouseX, mouseY, partialTicks);

                    bottomIndex++;
                } else {
                    int minH = heights[0];
                    int minIndex = 0;
                    for (int i=1; i<3; i++) {
                        if (minH > heights[i]) {
                            minIndex = i;
                            minH = heights[i];
                        }
                    }
    
                    left = 16 + 28 * minIndex;
                    if (minIndex == 0) {
                        left = 16;
                    }
    
                    item.setLocation(this.x + 48 + (117 * minIndex) + left, minH + ClientCsgoGuiScreen.getInstance().y + 22);
                    item.drawScreen(mouseX, mouseY, partialTicks);
                    heights[minIndex] += item.getHeight() + 10;
                }
            }

            int max = heights[0];
            // Iterate through the array starting from the second element
            for (int i = 1; i < heights.length; i++) {
                // Check if the current element is greater than the current maximum
                if (heights[i] > max) {
                    max = heights[i]; // Update the maximum value
                }
            }
            
            max += 20;
            
            if (max < ClientCsgoGuiScreen.getInstance().minHeight) {
                max = ClientCsgoGuiScreen.getInstance().minHeight;
            }
            ClientCsgoGuiScreen.getInstance().height = max;
        }
    }
    
    public void drawScreenPost(int mouseX, int mouseY) {
        if(this.open) {
            for(Item item : getItems()) {
                item.drawScreenPost(mouseX, mouseY);
            }
        }
    }
    
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
            // close all panels
            ClientCsgoGuiScreen.getInstance().getNavs().forEach(panel -> panel.open = false);

            ClientCsgoGuiScreen.getInstance().y = ClientCsgoGuiScreen.getInstance().absY;
            this.open = true;
            // mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return;
        }

        // if(mouseButton == 1 && this.isHovering(mouseX, mouseY)) {
        //     this.open = !this.open;
        //     // mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        //     return;
        // }
        if(!this.open) {
            return;
        }
        this.getItems().forEach(item -> item.mouseClicked(mouseX, mouseY, mouseButton));
    }
    
    public void addButton(Button button) {
        this.items.add(button);
    }
    
    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        if(!this.open) {
            return;
        }
        this.getItems().forEach(item -> item.mouseReleased(mouseX, mouseY, releaseButton));
    }
    
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        for(Item item : getItems()) {
            item.keyTyped(typedChar, keyCode);
        }
    }
    
    public final String getLabel() {
        return this.label;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void setOpen(boolean open) {
        this.open = open;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public boolean getOpen() {
        return this.open;
    }
    
    public final List<Item> getItems() {
        return this.items;
    }
    
    private boolean isHovering(int mouseX, int mouseY) {
        return mouseX >= this.getX() && mouseX <= this.getX() + this.getWidth() && mouseY >= this.getY() && mouseY <= this.getY() + this.getHeight() - (this.open ? 2 : 0);
    }
    
    //added this method in, just to fix shit. It is from uz1 class in future
    public static float calculateRotation(float var0) {
        if((var0 %= 360.0F) >= 180.0F) {
            var0 -= 360.0F;
        }
        
        if(var0 < -180.0F) {
            var0 += 360.0F;
        }
        
        return var0;
    }
    
    private float getTotalItemHeight() {
        float height = 0.0f;
        for(Item item : getItems()) {
            height += (float) item.getHeight() + 1.5f;
        }
        return height;
    }
}

