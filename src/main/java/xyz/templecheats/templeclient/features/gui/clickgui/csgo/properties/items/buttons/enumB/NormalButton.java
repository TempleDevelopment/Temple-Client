package xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.buttons.enumB;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import xyz.templecheats.templeclient.features.gui.clickgui.csgo.CsgoGuiScreen;
import xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties.items.buttons.Button;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;
import xyz.templecheats.templeclient.util.math.Vec2d;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.util.render.shader.impl.RectBuilder;

import java.awt.*;

import static xyz.templecheats.templeclient.features.gui.font.Fonts.font12;

public class NormalButton extends Button {
    public NormalButton(String label) {
        super(label);
        this.height = 12;
        this.width = 188;
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, 5);
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(GL11.GL_LEQUAL);
        RenderUtil.drawRect(x + getWidth() / 1.836f, y, x + getWidth() - 8, y + 12, 0xFF2A2927);
        new RectBuilder(new Vec2d(x + getWidth() / 1.836, y), new Vec2d(x + getWidth() - 10, y + 12))
                .outlineColor(new Color(0x262626))
                .width(0.5)
                .color(new Color(0xFF2A2927))
                .radius(2.0)
                .draw();
        font12.drawString(getLabel(), x + (getWidth() / 1.8), y + (4 / 0.6) - 1.5, !isHovering(mouseX, mouseY) ? 0xFFFFFFFF : ClickGUI.INSTANCE.getStartColor().getRGB(), false);
        GlStateManager.disableDepth();
        GlStateManager.popMatrix();
    }

    @Override
    public String otherEvent(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
            return getLabel();
        }
        return null;
    }

    @Override
    public int getWidth() {
        return 188;
    }

    protected boolean isHovering(int mouseX, int mouseY) {
        return this.getX() <= mouseX && (float) mouseX <= this.getX() + (float) this.getWidth() && this.getY() <= (float) mouseY && (float) mouseY <= this.getY() + (float) this.height;
    }

    @Override
    public CsgoGuiScreen getClientScreen() {
        return CsgoGuiScreen.getInstance();
    }
}
