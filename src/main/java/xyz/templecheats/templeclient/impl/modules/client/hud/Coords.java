package xyz.templecheats.templeclient.impl.modules.client.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.impl.modules.Module;
import xyz.templecheats.templeclient.impl.gui.font.FontUtils;

public class Coords extends Module {
    public static final Coords INSTANCE = new Coords();

    public Coords() {
        super("Coords", Keyboard.KEY_NONE, Category.CLIENT);
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            ScaledResolution sr = new ScaledResolution(mc);
            renderCoordinates(sr);
        }
    }

    private void renderCoordinates(ScaledResolution sr) {
        Minecraft mc = Minecraft.getMinecraft();

        float[] playerPos = {
                Math.round(mc.player.posX * 100D) / 100F,
                Math.round(mc.player.posY * 100D) / 100F,
                Math.round(mc.player.posZ * 100D) / 100F
        };

        String coordsString = TextFormatting.GRAY + "XYZ: " + TextFormatting.RESET + playerPos[0] + ", " + playerPos[1] + ", " + playerPos[2];

        GlStateManager.pushMatrix();
        GlStateManager.scale(0.7f, 0.7f, 1.0f);
        FontUtils.normal.drawStringWithShadow(coordsString, (int) (4 / 0.7f), (int) ((sr.getScaledHeight() - mc.fontRenderer.FONT_HEIGHT - 4) / 0.7f), 0xFFFFFF);
        GlStateManager.popMatrix();

        if (mc.player.dimension == 0) {
            float[] netherPos = {
                    Math.round(mc.player.posX * 12.5D) / 100F,
                    playerPos[1],
                    Math.round(mc.player.posZ * 12.5D) / 100F
            };

            String netherCoordsString = TextFormatting.GRAY + "Nether: " + TextFormatting.RESET + netherPos[0] + ", " + netherPos[1] + ", " + netherPos[2];

            GlStateManager.pushMatrix();
            GlStateManager.scale(0.7f, 0.7f, 1.0f);
            FontUtils.normal.drawStringWithShadow(netherCoordsString, (int) (4 / 0.7f), (int) ((sr.getScaledHeight() - 2 * mc.fontRenderer.FONT_HEIGHT - 4) / 0.7f), 0xFFFFFF);
            GlStateManager.popMatrix();
        } else if (mc.player.dimension == -1) {
            float[] overworldPos = {
                    playerPos[0] * 8,
                    playerPos[1],
                    playerPos[2] * 8
            };

            String overworldCoordsString = TextFormatting.GRAY + "Overworld: " + TextFormatting.RESET + overworldPos[0] + ", " + overworldPos[1] + ", " + overworldPos[2];

            GlStateManager.pushMatrix();
            GlStateManager.scale(0.7f, 0.7f, 1.0f);
            FontUtils.normal.drawStringWithShadow(overworldCoordsString, (int) (4 / 0.7f), (int) ((sr.getScaledHeight() - 2 * mc.fontRenderer.FONT_HEIGHT - 4) / 0.7f), 0xFFFFFF);
            GlStateManager.popMatrix();
        }
    }
}

