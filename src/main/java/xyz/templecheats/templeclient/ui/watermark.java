package xyz.templecheats.templeclient.ui;

import xyz.templecheats.templeclient.Client;
import xyz.templecheats.templeclient.modules.CLIENT.Panic;
import xyz.templecheats.templeclient.modules.Module;
import xyz.templecheats.templeclient.font.FontUtils;
import xyz.templecheats.templeclient.font.MinecraftFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

import static net.minecraft.client.gui.Gui.drawRect;

public class watermark {
    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post e) {
        switch (e.getType()) {
            case TEXT:
                if (!Panic.isPanic) {
                    int y = 10;
                    final int[] counter = {1};

                    Minecraft mc = Minecraft.getMinecraft();
                    MinecraftFontRenderer fr = FontUtils.normal;
                    ScaledResolution sr = new ScaledResolution(mc);

                    drawRect(6, 4, (int) (110 * 1.5), (int) (5 * 1.5), new Color(173, 216, 230).getRGB());

                    GlStateManager.pushMatrix();
                    GlStateManager.scale(0.7F, 0.7F, 1);

                    FontUtils.normal.drawString("Temple-1.7.5" + " | " + mc.getSession().getUsername() +
                            " | FPS: " + Minecraft.getDebugFPS(), 12, 15, -1);
                    GlStateManager.popMatrix();

                    GlStateManager.pushMatrix();
                    GlStateManager.scale(0.7F, 0.7F, 1);
                    int moduleHeight = 12;
                    for (Module module : Client.modules) {
                        if (module.toggled) {
                            GlStateManager.pushMatrix();
                            GlStateManager.translate((sr.getScaledWidth() / 0.7) - 104, y, 1);
                            Gui.drawRect(100, 0, 102, moduleHeight, new Color(173, 216, 230).getRGB());
                            fr.drawStringWithShadow(module.name, 100 - fr.getStringWidth(module.name) - 4, 1, -1);
                            GlStateManager.popMatrix();

                            y += moduleHeight;
                        }
                    }
                    GlStateManager.popMatrix();
                    break;
                }
                break;
            default:
                break;
        }
    }
}

