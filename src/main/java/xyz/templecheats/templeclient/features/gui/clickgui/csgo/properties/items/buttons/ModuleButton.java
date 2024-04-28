package xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.features.gui.clickgui.csgo.CsgoGuiScreen;
import xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.IContainer;
import xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.Item;
import xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.buttons.enumB.EnumButton;
import xyz.templecheats.templeclient.features.gui.font.CFont;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.util.render.shader.impl.RectBuilder;
import xyz.templecheats.templeclient.util.math.Vec2d;
import xyz.templecheats.templeclient.util.setting.Setting;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import static xyz.templecheats.templeclient.features.gui.font.Fonts.*;

public class ModuleButton extends Button implements IContainer {
    private final Module module;
    private List<Item> items = new ArrayList<>();
    int color0, color1, color2, color3;
    int offsetY;
    public ModuleButton(Module module) {
        super(module.getName());
        this.module = module;

        if(!module.parent) {
            this.items.add(new BindButton(module));
        }
        final List<Setting<?>> settings = TempleClient.settingsManager.getSettingsByMod(module);
        for (Setting<?> s : settings) {
            this.items.add(s.createCsgoButton(this));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (!this.items.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.enableDepth();
            GlStateManager.depthFunc(GL11.GL_LEQUAL);
            Color color = new Color(0x101010);

            String description = module.getDescription();
            int maxLineWidth = getWidth() - 36;
            List<String> lines = new ArrayList<>();
            offsetY = (font14.getStringWidth(description) > maxLineWidth) ? 12 : 8;

            new RectBuilder(new Vec2d(x, y), new Vec2d(x + getWidth(), y + height))
                    .outlineColor(
                            this.getState() ? new Color(color3) : new Color(40, 40, 40),
                            this.getState() ? new Color(color2) : new Color(25, 25, 25),
                            this.getState() ? new Color(color0) : new Color(25, 25, 25),
                            this.getState() ? new Color(color1) : new Color(40, 40, 40)
                    ).width(0.9).color(color).radius(2.0).draw();
            if (getItems().size() > 1) {
                new RectBuilder(new Vec2d(x + 3, y + font20.getFontHeight() + 15 + ((font14.getStringWidth(description) > maxLineWidth) ? 7 : 0)), new Vec2d(x + getWidth() - 3, y + height - 3))
                        .color(new Color(25, 25, 25)).radius(3.0).draw();
            }
            font20.drawString(this.getLabel(), (float) (x + 5 / 0.8), (float) (y + font20.getFontHeight() - 6.5), module.isEnabled() ? Color.WHITE : new Color(99, 104, 107), false);

            while (font14.getStringWidth(description) > maxLineWidth) {
                int endIndex = findLastSpaceIndex(description, maxLineWidth, font14);
                if (endIndex == -1) {
                    endIndex = font14.trimStringToWidth(description, maxLineWidth).length();
                }
                String line = description.substring(0, endIndex);
                lines.add(line);
                description = description.substring(endIndex).trim();
            }
            if (!description.isEmpty()) {
                lines.add(description);
            }

            int yOffset = 0;
            for (String line : lines) {
                font14.drawString(line, x + 5 / 0.8f, y + font14.getFontHeight() + 9 + yOffset, new Color(99, 104, 107).getRGB(), false);
                yOffset += (int) (font14.getFontHeight() + 1);
            }

            GlStateManager.disableDepth();
            GlStateManager.popMatrix();

            int height = 4;
            int index = 0;

            for (int i = 0; i < getItems().size(); i++) {
                boolean shouldNewLine = false;

                if (index == 1 && items.get(i - 1).getHeight() != items.get(i).getHeight()) {
                    index = 0;
                    height += items.get(i - 1).getHeight() + 4;
                }

                float itemOffsetX = this.x + (float) this.getWidth() / 2 * index + 3;
                float itemOffsetY = this.y + height + (offsetY - 1);

                if (items.get(i) instanceof EnumButton) {
                    EnumButton enumButton = (EnumButton) items.get(i);
                    if (enumButton.getWidth() >= (this.getWidth() / 2) - 12) {
                        shouldNewLine = true;
                    }
                }

                items.get(i).setLocation(itemOffsetX, itemOffsetY);
                items.get(i).setWidth((this.getWidth() / 2) - 12);
                items.get(i).drawScreen(mouseX, mouseY, partialTicks);

                index++;

                if (index == 2 || shouldNewLine) {
                    index = 0;
                    height += items.get(i).getHeight() + 4;
                }

                if (i + 1 < items.size()) {
                    color0 = ClickGUI.INSTANCE.getClientColor(i);
                    color1 = ClickGUI.INSTANCE.getClientColor(i + 1);
                    color0 = ClickGUI.INSTANCE.getClientColor(i + 5);
                    color1 = ClickGUI.INSTANCE.getClientColor(i + 20);
                }
            }

            if (index == 1) {
                height += items.get(getItems().size() - 1).getHeight() + 4;
            }

            this.setHeight(height + offsetY + 1);
        }
    }

    private int findLastSpaceIndex(String text, int maxWidth, CFont fontRenderer) {
        int index = -1;
        int currentWidth = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            float charWidth = fontRenderer.getCharWidthFloat(c);

            if (currentWidth + charWidth > maxWidth) {
                return index;
            }

            if (c == ' ') {
                index = i;
            }

            currentWidth += (int) charWidth;
        }

        return index;
    }

    /*@Override
    public int getHeight() {
        int height = 4;

        for (int i = 0, index = 0; i < getItems().size(); ) {
            if (index == 1) {
                if (items.get(i - 1).getHeight() != items.get(i).getHeight()) {
                    index = 0;
                    height += items.get(i - 1).getHeight() + 4;
                }
            }
            index += 1;

            if (index == 2) {
                index = 0;
                height += items.get(i - 1).getHeight() + 4;
            }
            i++;

            if (i == items.size() && index == 1) {
                index = 0;
                height += items.get(i - 1).getHeight() + 4;
            }
        }
        return height + offsetY + 1;
    }*/

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if(!this.items.isEmpty()) {
            if(mouseButton == 1 && this.isHovering(mouseX, mouseY)) {
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
            for(Item item : getItems()) {
                System.out.println(item.getLabel()+", "+item.getHeight());
                item.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        super.mouseReleased(mouseX, mouseY, releaseButton);
        if(!items.isEmpty()) {
            for(Item item : getItems()) {
                item.mouseReleased(mouseX, mouseY, releaseButton);
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if(!items.isEmpty()) {
            for(Item item : getItems()) {
                item.keyTyped(typedChar, keyCode);
            }
        }
    }

    @Override
    public int getWidth() {
        return 196;
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
    public CsgoGuiScreen getClientScreen() {
        return CsgoGuiScreen.getInstance();
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