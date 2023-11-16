package xyz.templecheats.templeclient.modules.HUD;

import net.minecraft.client.renderer.GlStateManager;
import xyz.templecheats.templeclient.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.gui.ScaledResolution;
import xyz.templecheats.templeclient.font.FontUtils;

import java.awt.Color;

public class Coords extends Module {
    public Coords() {
        super("Coords", Keyboard.KEY_NONE, Category.HUD);
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            drawXYZCoordinates();
        }
    }

    private void drawXYZCoordinates() {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc);

        GlStateManager.pushMatrix();
        GlStateManager.scale(0.8F, 0.8F, 1);

        String coordinates = String.format("%.2f, %.2f, %.2f", mc.player.posX, mc.player.posY, mc.player.posZ);

        int textWidth = (int) (FontUtils.normal.getStringWidth(coordinates) / 0.8F);

        int xPos = 4;
        int yPos = (int) (sr.getScaledHeight() / 0.8F - 14);


        Color textColor = new Color(0xFFFFFF);

        FontUtils.normal.drawString(coordinates, xPos, yPos, textColor.getRGB());

        GlStateManager.popMatrix();

    }
}
