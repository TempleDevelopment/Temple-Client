package xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.buttons.slider;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.Item;
import xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.Panel;
import xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.buttons.Button;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.util.color.impl.RectBuilder;
import xyz.templecheats.templeclient.util.math.Vec2d;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;

import java.awt.*;

import static xyz.templecheats.templeclient.features.gui.font.Fonts.*;

public class DoubleSlider extends Item {
    private final Button parentButton;
    private final DoubleSetting setting;
    private boolean dragging;
    private double value;

    public DoubleSlider(DoubleSetting setting, Button parentButton) {
        super(setting.name);
        this.setting = setting;
        this.parentButton = parentButton;
        this.height = 18;
        this.value = setting.doubleValue();
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
        font12.drawString(Double.toString(Math.round(value * 100D) / 100D), x + width - (font12.getStringWidth(Double.toString(Math.round(value * 100D) / 100D)) / 2) - 8, y + 5, Color.WHITE, false);

        GlStateManager.disableDepth();
        GlStateManager.popMatrix();
        dragging(mouseX, range);
    }

    private void dragging(final int mouseX, double range) {
        if(this.dragging) {
            double offset = (MathHelper.clamp((mouseX - x) / (this.width), 0, 1) * range);
            this.value = setting.min + offset;

            if(!(this.setting.parent instanceof ClickGUI) || !this.setting.name.equals("Scale")) {
                this.setting.setDoubleValue(this.value);
            }
        } else {
            this.value = setting.doubleValue();
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
            this.setting.setDoubleValue(this.value);
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
