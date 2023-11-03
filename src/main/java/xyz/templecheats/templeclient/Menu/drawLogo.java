package xyz.templecheats.templeclient.Menu;

import xyz.templecheats.templeclient.font.FontUtils;
import net.minecraft.client.renderer.GlStateManager;

public class drawLogo {
    public static void drawString(final double scale, final String text, final float x, final float y, final int color) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, scale);
        FontUtils.normal.drawStringWithShadow(text, x, y, color);
        GlStateManager.popMatrix();
    }
}
