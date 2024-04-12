package xyz.templecheats.templeclient.features.gui.clickgui.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.ClientGuiScreen;
import xyz.templecheats.templeclient.features.gui.clickgui.basic.panels.Panel;
import xyz.templecheats.templeclient.features.module.modules.client.HUD;

public class HudEditorScreen extends ClientGuiScreen {
    private static HudEditorScreen instance;

    @Override
    public void load() {
        this.getPanels().clear();

        this.getPanels().add(new Panel("Hud Editor", 200, 100, true) {
            @Override
            public void setupItems() {
                for (HUD.HudElement element: HUD.INSTANCE.getHudElements()) {
                    this.addButton(new HudElementButton(element));
                }
            }
        });

        super.load();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int screenWidth = sr.getScaledWidth();
        int screenHeight = sr.getScaledHeight();

        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        drawLine(centerX, 0, centerX, screenHeight, 1.0F);

        drawLine(0, centerY, screenWidth, centerY, 1.0F);
    }

    private void drawLine(int x1, int y1, int x2, int y2, float thickness) {
        GlStateManager.glLineWidth(thickness);
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(1, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(x1, y1, 0.0D).endVertex();
        bufferbuilder.pos(x2, y2, 0.0D).endVertex();
        tessellator.draw();

        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
    }

    public static HudEditorScreen getInstance() {
        return instance == null ? (instance = new HudEditorScreen()) : instance;
    }

    protected double getScale() {
        return HUD.INSTANCE.hudScale.doubleValue();
    }
}