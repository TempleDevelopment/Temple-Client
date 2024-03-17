package xyz.templecheats.templeclient.features.gui.clickgui.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.MathHelper;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.Button;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.ClientGuiScreen;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.Item;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.Panel;
import xyz.templecheats.templeclient.features.gui.font.FontUtils;
import xyz.templecheats.templeclient.features.module.modules.client.HUD;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.util.setting.Setting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HudElementButton extends Button {
    private final HUD.HudElement element;
    private final List<Item> items = new ArrayList<>();
    private boolean subOpen;
    private long timeHovering;

    private double dragX;
    private double dragY;

    public HudElementButton(HUD.HudElement element) {
        super(element.getName());
        this.element = element;

        final List<Setting<?>> settings = TempleClient.settingsManager.getSettingsByMod(element);
        for (Setting<?> s : settings) {
            this.items.add(s.createBasicButton(this));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if(!this.items.isEmpty()) {
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

        this.drag(mouseX, mouseY);
    }

    private void drag(int mouseX, int mouseY) {
        final double x = this.element.isDragging() ? this.dragX + mouseX : this.element.getX();
        final double y = this.element.isDragging() ? this.dragY + mouseY : this.element.getY();

        if(HUD.INSTANCE.clamping.booleanValue()) {
            final ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
            final double screenWidth = sr.getScaledWidth() / HUD.INSTANCE.hudScale.doubleValue();
            final double screenHeight = sr.getScaledHeight() / HUD.INSTANCE.hudScale.doubleValue();

            this.element.setX(MathHelper.clamp(x, 0, screenWidth - this.element.getWidth()));
            this.element.setY(MathHelper.clamp(y, 0, screenHeight - this.element.getHeight()));
        } else {
            this.element.setX(x);
            this.element.setY(y);
        }
    }

    @Override
    public void drawScreenPost(int mouseX, int mouseY) {
        if(this.isHovering(mouseX, mouseY)) {
            if(this.timeHovering == 0) {
                this.timeHovering = System.currentTimeMillis();
            }

            if(System.currentTimeMillis() - this.timeHovering > 500) {
                final String description = element.getDescription();
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

        if(mouseButton == 0 && this.isHoveringOnElement(mouseX, mouseY)) {
            this.dragX = this.element.getX() - mouseX;
            this.dragY = this.element.getY() - mouseY;
            this.element.setDragging(true);
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

        if(releaseButton == 0) {
            this.element.setDragging(false);
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
        element.setEnabled(!element.isEnabled());
    }

    @Override
    public boolean getState() {
        return element.isEnabled();
    }

    @Override
    public ClientGuiScreen getClientScreen() {
        return HudEditorScreen.getInstance();
    }

    private boolean isHoveringOnElement(int mouseX, int mouseY) {
        for(Panel panel : this.getClientScreen().getPanels()) {
            if(!panel.drag) continue;
            return false;
        }
        for(HUD.HudElement element : HUD.INSTANCE.getHudElements()) {
            if(!element.isDragging()) continue;
            return false;
        }
        return mouseX >= this.element.getX() && mouseX <= this.element.getX() + this.element.getWidth() && mouseY >= this.element.getY() && mouseY <= this.element.getY() + this.element.getHeight();
    }
}
