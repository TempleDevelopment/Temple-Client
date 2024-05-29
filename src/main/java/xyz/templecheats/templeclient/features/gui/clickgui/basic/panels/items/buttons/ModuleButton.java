package xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.items.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.ClickGuiScreen;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.ClientGuiScreen;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.IContainer;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.items.Item;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.util.setting.Setting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.Panel.calculateRotation;
import static xyz.templecheats.templeclient.features.gui.font.Fonts.font18;

public class ModuleButton extends Button implements IContainer {
    private final Module module;
    private List<Item> items = new ArrayList<>();
    private boolean open;
    private long timeHovering;
    private int progress;

    public ModuleButton(Module module) {
        super(module.getName());
        this.module = module;
        this.progress = 0;

        final List<Setting<?>> settings = TempleClient.settingsManager.getSettingsByMod(module);
        if (!module.parent) {
            this.items.add(new BindButton(module));
        }
        if (!module.submodules.isEmpty()) {
            for (Module submodule : module.submodules) {
                items.add(new ModuleButton(submodule));
            }
        }
        for (Setting<?> s : settings) {
            this.items.add(s.createBasicButton(this));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (!this.items.isEmpty()) {
            if (module.submodule) {
                font18.drawString(!this.open ? "+" : "-", this.x - 1.0f + (float) getWidth() - 8.0f, this.y + 4.0f, -1, false);
            }
            if (ClickGUI.INSTANCE.gears.booleanValue() && !module.submodule) {
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/icons/gear.png"));
                GlStateManager.translate(getX() + getWidth() - 6.7F, getY() + 7.7F - 0.3F, 0.0F);
                if (this.open) {
                    ++progress;
                    GlStateManager.rotate(calculateRotation((float) this.progress), 0.0F, 0.0F, 1.0F);
                }
                Gui.drawScaledCustomSizeModalRect(-5, -5, 0.0F, 0.0F, 10, 10, 10, 10, 10.0F, 10.0F);
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }

            if (this.open) {
                float height = 1.0f;
                for (Item item : getItems()) {
                    item.setLocation(this.x + 1.0f, this.y + (height += 15.0f));
                    item.setHeight(15);
                    item.setWidth(this.getWidth() - 9);
                    item.drawScreen(mouseX, mouseY, partialTicks);
                    float offset = item.getHeight() - 14;
                    if (item instanceof ColorButton && ((ColorButton) item).getExtended()) {
                        height += 98;
                    }
                    if (item instanceof IContainer && module.parent) {
                        height += offset;
                    }
                }
            }
        }
    }

    @Override
    public void drawScreenPost(int mouseX, int mouseY) {
        if (this.isHovering(mouseX, mouseY)) {
            final String description = module.getDescription();
            final float startX = mouseX + 7;
            final float startY = mouseY + 7;
            final float width = (float) font18.getStringWidth(description);
            final float height = (float) font18.getFontHeight();
            int color = ClickGUI.INSTANCE.getEndColor().getRGB();
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
                open = !open;
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
            if (this.open) {
                for (Item item : getItems()) {
                    item.mouseClicked(mouseX, mouseY, mouseButton);
                }
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        super.mouseReleased(mouseX, mouseY, releaseButton);
        if (!items.isEmpty() && open) {
            for (Item item : getItems()) {
                item.mouseReleased(mouseX, mouseY, releaseButton);
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if (!items.isEmpty() && open) {
            for (Item item : getItems()) {
                item.keyTyped(typedChar, keyCode);
            }
        }
    }

    @Override
    public int getHeight() {
        if (this.open) {
            int height = 14;
            for (Item item : getItems()) {
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
    public int getWidth() {
        return module.submodule ? 82 : 84;
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

    @Override
    public List<Item> getItems() {
        return items;
    }

    @Override
    public void setItems(List<Item> items) {
        this.items = items;
    }
}

