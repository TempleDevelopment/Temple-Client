package xyz.templecheats.templeclient.features.gui.clickgui.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.ClientGuiScreen;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.Panel;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.items.Item;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.items.buttons.Button;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.items.buttons.ColorButton;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.features.module.modules.client.HUD;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.util.setting.Setting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static xyz.templecheats.templeclient.features.gui.font.Fonts.font18;

public class HudElementButton extends Button {
    /*
     * Variables
     */
    private final HUD.HudElement element;
    private final List<Item> items = new ArrayList<>();
    private boolean subOpen;
    private long timeHovering;
    private double dragX;
    private double dragY;
    private int progress;

    public HudElementButton(HUD.HudElement element) {
        super(element.getName());
        this.element = element;
        final List<Setting<?>> settings = TempleClient.settingsManager.getSettingsByMod(element);
        for (Setting<?> s : settings) {
            this.items.add(s.createBasicButton(this));
        }

        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int screenWidth = sr.getScaledWidth();
        int screenHeight = sr.getScaledHeight();
        if (this.element.getX() == 0 && this.element.getY() == 0) {
            this.element.setX((double) screenWidth / 2);
            this.element.setY((double) screenHeight / 2);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (!this.items.isEmpty()) {
            if (ClickGUI.INSTANCE.gears.booleanValue()) {
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/icons/gear.png"));
                GlStateManager.translate(this.x + this.width - 6.7F, this.y + 7.7F - 0.3F, 0);
                if (this.subOpen) {
                    ++this.progress;
                    GlStateManager.rotate(Panel.calculateRotation((float) this.progress), 0.0F, 0.0F, 1.0F);
                }
                Gui.drawScaledCustomSizeModalRect(-5, -5, 0, 0, 10, 10, 10, 10, 10.0F, 10.0F);
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }

            if (this.subOpen) {
                float height = 1.0f;
                for (Item item : items) {
                    item.setLocation(this.x + 1.0f, this.y + (height += 15.0f));
                    item.setHeight(15);
                    item.setWidth(this.width - 9);
                    item.drawScreen(mouseX, mouseY, partialTicks);

                    if (item instanceof ColorButton && ((ColorButton) item).getExtended()) {
                        height += 98;
                    }
                }
            }
        }

        this.drag(mouseX, mouseY);
    }

    private void drag(int mouseX, int mouseY) {
        final double x = this.element.isDragging() ? this.dragX + mouseX : this.element.getX();
        final double y = this.element.isDragging() ? this.dragY + mouseY : this.element.getY();

        if (HUD.INSTANCE.clamping.booleanValue()) {
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
        if (ClickGUI.INSTANCE.description.booleanValue() && this.isHovering(mouseX, mouseY)) {
            final String description = element.getDescription();
            final float startX = mouseX + 7;
            final float startY = mouseY + 7;
            final float width = (float) font18.getStringWidth(description);
            final float height = (float) font18.getFontHeight();
            int color = ClickGUI.INSTANCE.getStartColor().getRGB();
            RenderUtil.drawOutlineRect(startX - 1, startY - 1, startX + width, startY + height, color);
            RenderUtil.drawRect(startX - 1, startY - 1, startX + width, startY + height, 0x88000000);
            font18.drawString(description, startX, startY, -1, false);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (!this.items.isEmpty()) {
            if (mouseButton == 1 && this.isHovering(mouseX, mouseY)) {
                this.subOpen = !this.subOpen;
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
            if (this.subOpen) {
                for (Item item : items) {
                    item.mouseClicked(mouseX, mouseY, mouseButton);
                }
            }
        }

        if (mouseButton == 0 && this.isHoveringOnElement(mouseX, mouseY)) {
            this.dragX = this.element.getX() - mouseX;
            this.dragY = this.element.getY() - mouseY;
            this.element.setDragging(true);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        super.mouseReleased(mouseX, mouseY, releaseButton);
        if (!items.isEmpty() && subOpen) {
            for (Item item : items) {
                item.mouseReleased(mouseX, mouseY, releaseButton);
            }
        }

        if (releaseButton == 0) {
            this.element.setDragging(false);
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if (!items.isEmpty() && subOpen) {
            for (Item item : items) {
                item.keyTyped(typedChar, keyCode);
            }
        }
    }

    @Override
    public int getHeight() {
        if (this.subOpen) {
            int height = 14;
            for (Item item : items) {
                height += item.getHeight() + 1;

                if (item instanceof ColorButton && ((ColorButton) item).getExtended()) {
                    height += 98;
                }
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
        for (Panel panel : this.getClientScreen().getPanels()) {
            if (!panel.drag) continue;
            return false;
        }
        for (HUD.HudElement element : HUD.INSTANCE.getHudElements()) {
            if (!element.isDragging()) continue;
            return false;
        }
        return mouseX >= this.element.getX() && mouseX <= this.element.getX() + this.element.getWidth() && mouseY >= this.element.getY() && mouseY <= this.element.getY() + this.element.getHeight();
    }
}