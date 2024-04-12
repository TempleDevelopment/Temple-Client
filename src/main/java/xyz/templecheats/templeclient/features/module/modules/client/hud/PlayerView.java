package xyz.templecheats.templeclient.features.module.modules.client.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import xyz.templecheats.templeclient.features.module.modules.client.HUD;

public class PlayerView extends HUD.HudElement {
    public PlayerView() {
        super("PlayerView", "Shows your player model in the HUD");
    }

    @Override
    public void renderElement(ScaledResolution sr) {
        this.setWidth(40);
        this.setHeight(70);

        GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.getX() + this.getWidth() - 20, this.getY() + this.getHeight() - 10, 0);
        GlStateManager.rotate(180, 1, 0, 0);
        GlStateManager.rotate(180, 0, 1, 0);
        GlStateManager.scale(-25, 25, 25);
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
        renderManager.setPlayerViewY(180);
        renderManager.setRenderShadow(false);
        renderManager.renderEntity(mc.player, 0, 0, 0, 0, 1, false);
        renderManager.setRenderShadow(true);
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        GlStateManager.enableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.popAttrib();
    }
}