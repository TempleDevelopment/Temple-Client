package xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import xyz.templecheats.templeclient.features.gui.clickgui.csgo.CsgoGuiScreen;
import xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.Item;
import xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.buttons.Button;
import xyz.templecheats.templeclient.features.gui.font.CFont;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.features.module.modules.client.FontSettings;
import xyz.templecheats.templeclient.util.math.Vec2d;
import xyz.templecheats.templeclient.util.render.shader.RainbowUtil;
import xyz.templecheats.templeclient.util.render.shader.impl.RectBuilder;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class Panel {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final List<Item> items = new ArrayList<>();
    private final String label;
    private int x, y;
    private final int width, height;
    public boolean open, drag;
    private final RainbowUtil rainbowUtil = new RainbowUtil();
    public int[] color;

    public Panel(String label, int x, int y, boolean open) {
        this.label = label;
        this.x = x;
        this.y = y;
        this.width = 100;
        this.height = 25;
        this.open = open;
        this.setupItems();
        this.color = new int[]{Color.WHITE.getRGB(), Color.WHITE.getRGB()};
    }

    public abstract void setupItems();

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (open) {
            color[0] = ClickGUI.INSTANCE.getStartColor().getRGB();
            color[1] = ClickGUI.INSTANCE.getEndColor().getRGB();
        } else {
            this.color[0] = 0xFF0C0C0C;
            this.color[1] = 0xFF0C0C0C;
        }
        int step = (int) (mc.player.ticksExisted + mc.getRenderPartialTicks());

        int color0 = rainbowUtil.rainbowProgress(6, step, color[0], color[1]);
        int color1 = rainbowUtil.rainbowProgress(6, step / 10, color[0]);

        new RectBuilder(new Vec2d(x + 10, y), new Vec2d(x + width - 5, y + height))
                .outlineColorH(new Color(0x1F1F1F), new Color(35, 35, 35))
                .width(0.5)
                .colorH(new Color(color0), new Color(color1))
                .radius(2.5)
                .draw();

        GlStateManager.pushMatrix();
        if (this.open) {
            GlStateManager.color(1, 1, 1, 1);
        } else {
            GlStateManager.color(0.3f, 0.3f, 0.3f, 1f);
        }
        mc.getTextureManager().bindTexture(new ResourceLocation("textures/icons/" + this.label + "_selected.png"));
        Gui.drawScaledCustomSizeModalRect(this.x + 15, this.y + 6, 0.0F, 0.0F, 12, 12, 12, 12, 12.0F, 12.0F);
        GlStateManager.popMatrix();

        final CFont font22 = FontSettings.getFont(22);
        font22.drawString(this.label, (float) (x + 33), (float) (y + 7), this.open ? -1 : new Color(76, 76, 76).getRGB(), true);

        if (this.open) {
            // sort the item A B C
            for (int i = 0; i < items.size() - 1; i++) {
                for (int j = i + 1; j < items.size(); j++) {
                    String labelI = items.get(i).getLabel();
                    String labelJ = items.get(j).getLabel();

                    char firstCharI = labelI.charAt(0);
                    char firstCharJ = labelJ.charAt(0);

                    if (firstCharI > firstCharJ) {
                        Item temp = items.get(i);
                        items.set(i, items.get(j));
                        items.set(j, temp);
                    }
                }
            }

            int[] heights = {0, 0, 0};
            int left;
            int bottomIndex = 0;
            // arrange the item
            for (Item item : getItems()) {
                if (item.getHeight() == 10 + 5 + 2) {
                    int minH = 354;
                    item.setLocation(this.x + 105 + (57 * (bottomIndex % 7)) + 16, minH + CsgoGuiScreen.getInstance().y + 22 * ((float) bottomIndex / 7));
                    item.drawScreen(mouseX, mouseY, partialTicks);

                    bottomIndex++;
                } else {
                    int minH = heights[0];
                    int minIndex = 0;
                    for (int i = 1; i < 2; i++) {
                        if (minH > heights[i]) {
                            minIndex = i;
                            minH = heights[i];
                        }
                    }

                    left = 16 + 28 * minIndex;
                    if (minIndex == 0) {
                        left = 16;
                    }

                    item.setLocation(this.x + 105 + (176 * minIndex) + left, minH + CsgoGuiScreen.getInstance().y + 22);
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

            if (max < CsgoGuiScreen.getInstance().minHeight) {
                max = CsgoGuiScreen.getInstance().minHeight;
            }
            CsgoGuiScreen.getInstance().height = max;
        }
    }

    public void drawScreenPost(int mouseX, int mouseY) {
        if (this.open) {
            for (Item item : getItems()) {
                item.drawScreenPost(mouseX, mouseY);
            }
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
            // close all panels
            CsgoGuiScreen.getInstance().getNavs().forEach(panel -> panel.open = false);

            CsgoGuiScreen.getInstance().y = CsgoGuiScreen.getInstance().absY;
            this.open = true;
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return;
        }
        if (!this.open) {
            return;
        }
        this.getItems().forEach(item -> item.mouseClicked(mouseX, mouseY, mouseButton));
    }

    public void addButton(Button button) {
        this.items.add(button);
    }

    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        if (!this.open) {
            return;
        }
        this.getItems().forEach(item -> item.mouseReleased(mouseX, mouseY, releaseButton));
    }

    public void keyTyped(char typedChar, int keyCode) throws IOException {
        for (Item item : getItems()) {
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
}

