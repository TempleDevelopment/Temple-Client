package xyz.templecheats.templeclient.impl.gui.ui;

import xyz.templecheats.templeclient.ModuleManager;
import xyz.templecheats.templeclient.impl.modules.client.ClickGUI;
import xyz.templecheats.templeclient.impl.modules.client.Panic;
import xyz.templecheats.templeclient.impl.modules.Module;
import xyz.templecheats.templeclient.impl.gui.font.FontUtils;
import xyz.templecheats.templeclient.impl.gui.font.MinecraftFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Comparator;

public class watermark {

    private static Comparator<Module> MODULE_COMPARATOR = new Comparator<Module>() {
        @Override
        public int compare(Module a, Module b) {
            return Integer.compare(Minecraft.getMinecraft().fontRenderer.getStringWidth(b.getName()), Minecraft.getMinecraft().fontRenderer.getStringWidth(a.getName()));
        }
    };

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

                    Gui.drawRect(6, 4, (int) (110 * 1.5) + 20, (int) (5 * 1.5), ClickGUI.RGBColor.getRGB());

                    GlStateManager.pushMatrix();
                    GlStateManager.scale(0.7F, 0.7F, 1);

                    FontUtils.normal.drawString("temple-client" + " | ", 12, 15, -1);
                    FontUtils.normal.drawString("1.8.2", 12 + FontUtils.normal.getStringWidth("temple-client" + " | "), 15, ClickGUI.RGBColor.getRGB());
                    FontUtils.normal.drawString(" | " + mc.getSession().getUsername() + " | FPS: " + Minecraft.getDebugFPS(), 12 + FontUtils.normal.getStringWidth("temple-client" + " | " + "1.8.2"), 15, -1);
                    GlStateManager.popMatrix();

                    GlStateManager.pushMatrix();
                    GlStateManager.scale(0.7F, 0.7F, 1);
                    int moduleHeight = 12;

                    //for (Module module : Client.modules) {
                    ArrayList<Module> mods = ModuleManager.getActiveModules();
                    mods.sort(MODULE_COMPARATOR);

                    for (Module m : mods) {
                        GlStateManager.pushMatrix();
                        GlStateManager.translate((sr.getScaledWidth() / 0.7) - 104, y, 1);
                        fr.drawStringWithShadow(m.name, 100 - fr.getStringWidth(m.name) - 4, 1, ClickGUI.RGBColor.getRGB());
                        GlStateManager.popMatrix();

                        y += moduleHeight + 2;
                    }
                    GlStateManager.popMatrix();
                    break;
                }
        }
    }
}
