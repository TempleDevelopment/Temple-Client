package xyz.templecheats.templeclient.features.gui.clickgui.csgo.properties;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.features.gui.clickgui.csgo.Button;
import xyz.templecheats.templeclient.features.gui.clickgui.csgo.ClientCsgoGuiScreen;
import xyz.templecheats.templeclient.features.gui.font.FontUtils;
import xyz.templecheats.templeclient.features.module.modules.client.ClickGUI;

public class NormalButton extends Button {
    public NormalButton(String label) {
        super(label);
        this.height = 12;
    }

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {        
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.x+2, this.y, 5);
        GlStateManager.enableDepth(); // Enable depth testing
        GlStateManager.depthFunc(GL11.GL_LEQUAL); // Configure depth function
        RenderUtil.drawRect(0, 0, width-4, 12, 0xFF2A2927);
        GlStateManager.scale(0.6, 0.6, 1);
        FontUtils.drawString(getLabel(), 5/0.6, (float)(4/0.6), !isHovering(mouseX, mouseY) ? 0xFFFFFFFF : ClickGUI.INSTANCE.getStartColor(), false);
        GlStateManager.disableDepth(); // Disable depth testing after drawing
        GlStateManager.popMatrix();
    }

    // @Override
    // public String mouseClicked(int mouseX, int mouseY, int mouseButton) {
    //     if(mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
    //         return getLabel();
    //     }

    //     return "Arial";
    // }

    @Override
    public String otherEvent(int mouseX, int mouseY, int mouseButton) {
        if(mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
            return getLabel();
        }
        return null;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    protected boolean isHovering(int mouseX, int mouseY) {
        return this.getX() <= mouseX && (float) mouseX <= this.getX() + (float) this.getWidth() && this.getY() <= (float) mouseY && (float) mouseY <= this.getY() + (float) this.height;
    }

    @Override
    public ClientCsgoGuiScreen getClientScreen() {
        return ClientCsgoGuiScreen.getInstance();
    }
}
