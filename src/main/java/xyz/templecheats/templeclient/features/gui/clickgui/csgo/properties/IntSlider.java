package xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import xyz.templecheats.templeclient.features.gui.clickgui.csgo.Button;
import xyz.templecheats.templeclient.features.gui.clickgui.csgo.Item;
import xyz.templecheats.templeclient.features.gui.clickgui.csgo.NavLink;
import xyz.templecheats.templeclient.features.gui.font.FontUtils;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;

public class IntSlider extends Item {
    private final Button parentButton;
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
        double range = setting.max - setting.min;
        double percentBar = (value - setting.min) / range;

        GlStateManager.pushMatrix();
        GlStateManager.translate(this.x+3, this.y, 0);
        GlStateManager.enableDepth(); // Enable depth testing
        GlStateManager.depthFunc(GL11.GL_LEQUAL); // Configure depth function

        RenderUtil.drawRect(0, 8, (float) ((percentBar * (this.width-4))), 13, ClickGUI.INSTANCE.getStartColor());
        RenderUtil.drawRect(0, 8, (float) (this.width-4), 13, 0x88555555);

        if(this.isHovering(mouseX, mouseY)) {
            RenderUtil.drawRect(0, 8, (float) ((percentBar * (this.width-4))), 13, 0x22000000);
        }

        RenderUtil.drawGradientRect(0, 8, (float) (this.width-4), 13, 0x33555555, 0xAA333333);

        GlStateManager.scale(0.6, 0.6, 1);
        FontUtils.drawString(getLabel(), 0, 0, 0xFFD2D2D2, false);

        for (int i = -1, cidx=0; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                FontUtils.csgoBoldNormal.drawString(
                        Integer.toString(value),
                        (int)(percentBar * (this.width-4))/0.9 + i,
                        19+j,
                        0xFF131313+cidx
                );

                cidx++;
            }
        }

        FontUtils.csgoBoldNormal.drawString(
                Integer.toString(value),
                (int)(percentBar * (this.width-4))/0.9,
                19,
                0xFFFFFFFF
        );
        GlStateManager.disableDepth(); // Disable depth testing after drawing
        GlStateManager.popMatrix();

        if(this.dragging) {
            int offset = (int) (MathHelper.clamp((mouseX - x) / (this.width-4), 0, 1) * range);
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

    @Override
    public int getHeight() {
        return this.height;
    }

    private boolean isHovering(final int mouseX, final int mouseY) {
        for(final NavLink nav : this.parentButton.getClientScreen().getNavs()) {
            if(nav.drag) {
                return false;
            }
        }
        return mouseX >= this.getX() && mouseX <= this.getX() + this.getWidth() && mouseY >= this.getY() && mouseY <= this.getY() + this.height;
    }
}
