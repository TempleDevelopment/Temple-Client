package xyz.templecheats.templeclient.gui.ui;

import xyz.templecheats.templeclient.Client;
import xyz.templecheats.templeclient.features.modules.client.Panic;
import xyz.templecheats.templeclient.features.modules.Module;
import xyz.templecheats.templeclient.gui.font.FontUtils;
import xyz.templecheats.templeclient.gui.font.MinecraftFontRenderer;
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

                    Gui.drawRect(6, 4, (int) (110 * 1.5) + 20, (int) (5 * 1.5), new Color(173, 216, 230).getRGB());

                    GlStateManager.pushMatrix();
                    GlStateManager.scale(0.7F, 0.7F, 1);

                    FontUtils.normal.drawString("temple-client" + " | ", 12, 15, -1);
                    FontUtils.normal.drawString("1.7.6", 12 + FontUtils.normal.getStringWidth("temple-client" + " | "), 15, new Color(173, 216, 230).getRGB());
                    FontUtils.normal.drawString(" | " + mc.getSession().getUsername() + " | FPS: " + Minecraft.getDebugFPS(), 12 + FontUtils.normal.getStringWidth("temple-client" + " | " + "1.7.6"), 15, -1);

                    GlStateManager.popMatrix();

                    GlStateManager.pushMatrix();
                    GlStateManager.scale(0.7F, 0.7F, 1);
                    int moduleHeight = 12;
                    for (Module module : Client.modules) {
                        if (module.toggled) {
                            GlStateManager.pushMatrix();
                            GlStateManager.translate((sr.getScaledWidth() / 0.7) - 104, y, 1);
                            fr.drawStringWithShadow(module.name, 100 - fr.getStringWidth(module.name) - 4, 1, new Color(173, 216, 230).getRGB());
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

