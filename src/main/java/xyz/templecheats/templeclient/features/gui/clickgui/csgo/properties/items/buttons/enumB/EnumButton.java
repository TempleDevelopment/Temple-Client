package xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.buttons.enumB;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import xyz.templecheats.templeclient.features.gui.clickgui.csgo.CsgoGuiScreen;
import xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.Item;
import xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.buttons.Button;
import xyz.templecheats.templeclient.util.math.Vec2d;
import xyz.templecheats.templeclient.util.render.shader.impl.RectBuilder;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static xyz.templecheats.templeclient.features.gui.font.Fonts.font16;

public class EnumButton<T extends Enum<T>> extends xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.buttons.Button {
    private final xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.buttons.Button parentButton;
    private final EnumSetting<T> setting;
    private final List<EnumValueButton<T>> items = new ArrayList<>();
    private boolean subOpen;

    private final Minecraft mc = Minecraft.getMinecraft();

    public EnumButton(EnumSetting<T> setting, Button parentButton) {
        super(setting.name);
        this.setting = setting;
        this.parentButton = parentButton;
        this.height = 22;
        this.width = 188;

        for (T enumValue : setting.getValues()) {
            this.items.add(new EnumValueButton<>(enumValue));
        }
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        if (!this.items.isEmpty()) {
            GlStateManager.pushMatrix();
            new RectBuilder(new Vec2d(x + getWidth() / 1.836, y + 6), new Vec2d(x + getWidth() - 8, y + 21))
                    .color(!this.isHovering(mouseX, mouseY) ? new Color(0xFF2A2927) : new Color(0x363636))
                    .radius(2.0).draw();
            font16.drawString(getLabel(), x + 3 + 2 / 0.6, y + 10, 0xFFD2D2D2, false);
            font16.drawString(setting.value().toString(), x + getWidth() / 1.8, y + 10, 0xFFD2D2D2, false);
            mc.getTextureManager().bindTexture(new ResourceLocation("textures/icons/down.png"));
            Gui.drawScaledCustomSizeModalRect((int) (x + getWidth() - 23), (int) (y + 8), 0.0F, 0.0F, 7, 8, 7, 8, 7, 8);
            GlStateManager.popMatrix();

            if (this.subOpen) {
                float height = 20.0f;
                for (Item item : items) {
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
        if (mouseButton == 0 && isHovering(mouseX, mouseY)) {
            for (EnumValueButton<T> item : items) {
                String str = item.otherEvent(mouseX, mouseY, mouseButton);
                if (str != null) {
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
    public int getWidth() {
        return 188;
    }

    @Override
    public int getHeight() {
        int subItemsHeight = subOpen ? items.size() * 12 : 0;
        return subOpen ? 22 + subItemsHeight : 22;
    }


    @Override
    public boolean getState() {
        return false;
    }

    @Override
    public CsgoGuiScreen getClientScreen() {
        return this.parentButton.getClientScreen();
    }

    @Override
    protected boolean isHovering(final int mouseX, final int mouseY) {
        if (subOpen) {
            return this.getX() <= mouseX && mouseX <= this.getX() + this.getWidth() && this.getY() <= mouseY && mouseY <= this.getY() + this.height * items.size();
        }

        return this.getX() <= mouseX && mouseX <= this.getX() + this.getWidth() && this.getY() <= mouseY && mouseY <= this.getY() + this.height;
    }
}
