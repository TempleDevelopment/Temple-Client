package xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.buttons.slider;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.Item;
import xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.Panel;
import xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.buttons.Button;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.util.color.impl.RectBuilder;
import xyz.templecheats.templeclient.util.math.Vec2d;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;

import java.awt.*;

import static xyz.templecheats.templeclient.features.gui.font.Fonts.*;

public class IntSlider extends Item {
    private final xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.buttons.Button parentButton;
    private final IntSetting setting;
    private boolean dragging;
    private int value;

    public IntSlider(IntSetting setting, Button parentButton) {
        super(setting.name);
        this.parentButton = parentButton;
        this.setting = setting;
        this.height = 18;
        this.value = setting.intValue();
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        double range = Math.abs(setting.max - setting.min);
        double percentBar = MathHelper.clamp((value - setting.min) / range, 0.0, 1.0);

        int color0 = ClickGUI.INSTANCE.getClientColor((int) range);
        int color1 = ClickGUI.INSTANCE.getClientColor((int) range + 1);

        GlStateManager.pushMatrix();
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(GL11.GL_LEQUAL);

        if(this.isHovering(mouseX, mouseY)) {
            new RectBuilder(new Vec2d(x + 3, y + 12), new Vec2d(x + 3 + width, y + 14)).color(new Color(0x22000000)).radius(1.0).draw();
        }
        new RectBuilder(new Vec2d(x + 3, y + 12), new Vec2d(x + 3 + width, y + 14)).colorH(new Color(0x33555555), new Color(0xAA333333)).radius(1.0).draw();
        new RectBuilder(new Vec2d(x + 3, y + 12), new Vec2d(x + 3 + percentBar * (this.width),  y + 14)).colorH(new Color(color0), new Color(color1)).radius(1.0).draw();

        double circleX = x + 3 + percentBar * (this.width);
        new RectBuilder(new Vec2d(circleX - 2, y + 10.8), new Vec2d(circleX + 2, y + 14.8)).color(new Color(color1)).radius(4.0).draw();

        font14.drawString(getLabel(), x + 3, y + 4, 0xFFD2D2D2, false);
        font12.drawString(Integer.toString(value), x + width - (font12.getStringWidth(Integer.toString(value)) / 2) - 8, y + 5, Color.WHITE, false);

        GlStateManager.disableDepth();
        GlStateManager.popMatrix();

        if(this.dragging) {
            int offset = (int) (MathHelper.clamp((mouseX - x) / (this.width), 0, 1) * range);
            value = setting.min + offset;
            setting.setIntValue(setting.min + offset);

            if(!(this.setting.parent instanceof ClickGUI) || !this.setting.name.equals("Scale")) {
                setting.setIntValue(value);
            }
        } else {
            this.value = setting.intValue();
        }
    }

    @Override
    public void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        if(this.isHovering(mouseX, mouseY) && mouseButton == 0) {
            this.dragging = true;
            return;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if(state == 0 && this.dragging) {
            this.setting.setIntValue(this.value);
            this.dragging = false;
            return;
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    private boolean isHovering(final int mouseX, final int mouseY) {
        for(final Panel nav : this.parentButton.getClientScreen().getNavs()) {
            if(nav.drag) {
                return false;
            }
        }
        return mouseX >= this.getX() && mouseX <= this.getX() + this.getWidth() && mouseY >= this.getY() && mouseY <= this.getY() + this.height;
    }
}
