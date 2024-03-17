package xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.features.gui.clickgui.csgo.ClientCsgoGuiScreen;
import xyz.templecheats.templeclient.features.gui.clickgui.csgo.Item;
import xyz.templecheats.templeclient.features.gui.clickgui.csgo.Button;
import xyz.templecheats.templeclient.features.gui.font.FontUtils;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;

import java.util.ArrayList;
import java.util.List;

public class EnumButton<T extends Enum<T>> extends Button {
    private final Button parentButton;
    private final EnumSetting<T> setting;
    private final List<EnumValueButton<T>> items = new ArrayList<>();
    private boolean subOpen;

    private final Minecraft mc = Minecraft.getMinecraft();

    public EnumButton(EnumSetting<T> setting, Button parentButton) {
        super(setting.name);
        this.setting = setting;
        this.parentButton = parentButton;
        this.height = 22;

        for (T enumValue : setting.getValues()) {
            this.items.add(new EnumValueButton<>(enumValue));
        }
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        if(!this.items.isEmpty()) {
            // RenderUtil.drawRect(x, y, x + width + 7.4F, y + height, !isHovering(mouseX, mouseY) ? 0x11333333 : 0x88333333);
    
            GlStateManager.pushMatrix();
            GlStateManager.translate(this.x+2, this.y, 0);
            
            RenderUtil.drawRect(0, 8, width-4, 20, !this.isHovering(mouseX, mouseY) ? 0xFF2A2927 : 0x88888888);
            RenderUtil.drawGradientRect(0, 8, width-4, 20, 0x33555555, 0xAA333333);

            GlStateManager.scale(0.6, 0.6, 0);
            FontUtils.drawString(getLabel(), 2/0.6, 0, 0xFFD2D2D2, false);
            FontUtils.drawString(setting.value().toString(), 5/0.6, (float)(12/0.6), 0xFFD2D2D2, false);
            mc.getTextureManager().bindTexture(new ResourceLocation("textures/icons/down.png"));
            Gui.drawScaledCustomSizeModalRect(width+15, 20, 0.0F, 0.0F, 8, 8, 8, 8, 8, 8);
            GlStateManager.popMatrix();
            
            if (this.subOpen) {
                float height = 20.0f;
                for(Item item : items) {
                    item.setLocation(this.x, this.y + height);
                    item.setWidth(this.width);
                    item.drawScreen(mouseX, mouseY, partialTicks);
                    height += 12;
                }
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton==0 && isHovering(mouseX, mouseY)) {
            for (EnumValueButton<T> item : items) {
                String str = item.otherEvent(mouseX, mouseY, mouseButton);
                if(str != null) {
                    setting.setValue(item.value);

                    if (str.equals("CSGO") || str.equals("Default")) {
                        Minecraft.getMinecraft().displayGuiScreen(null);
                        Minecraft.getMinecraft().setIngameFocus();
                    }
                }

            }

            subOpen = !subOpen;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public boolean getState() {
        return false;
    }

    @Override
    public ClientCsgoGuiScreen getClientScreen() {
        return this.parentButton.getClientScreen();
    }

    @Override
    protected boolean isHovering(final int mouseX, final int mouseY) {
        if (subOpen) {
            return this.getX() <= mouseX && mouseX <= this.getX() + this.getWidth() && this.getY() <= mouseY && mouseY <= this.getY() + this.height*items.size();
        }

        return this.getX() <= mouseX && mouseX <= this.getX() + this.getWidth() && this.getY() <= mouseY && mouseY <= this.getY() + this.height;
    }
}
