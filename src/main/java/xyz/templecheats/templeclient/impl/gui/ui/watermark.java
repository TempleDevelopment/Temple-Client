package xyz.templecheats.templeclient.impl.gui.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.templecheats.templeclient.ModuleManager;
import xyz.templecheats.templeclient.impl.gui.font.FontUtils;
import xyz.templecheats.templeclient.impl.modules.Module;
import xyz.templecheats.templeclient.impl.modules.client.ClickGUI;
import xyz.templecheats.templeclient.impl.modules.client.Panic;

import java.util.ArrayList;
import java.util.Comparator;

public class watermark {
	
	private static final Comparator<Module> MODULE_COMPARATOR = (a, b) -> Double.compare(FontUtils.getStringWidth(b.getName()), FontUtils.getStringWidth(a.getName()));
	
	@SubscribeEvent
	public void onRender(RenderGameOverlayEvent.Post event) {
		if(event.getType() == RenderGameOverlayEvent.ElementType.TEXT && !Panic.isPanic) {
			int y = 10;
			
			Minecraft mc = Minecraft.getMinecraft();
			ScaledResolution sr = new ScaledResolution(mc);
			
			final String name = "temple-client" + " | ";
			final String version = "1.8.5";
			final String debug = " | " + mc.getSession().getUsername() + " | FPS: " + Minecraft.getDebugFPS();
			
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.9F, 0.9F, 1);
			FontUtils.drawString(name, 12, 15, -1, false);
			FontUtils.drawString(version, 12 + FontUtils.getStringWidth(name), 15, ClickGUI.INSTANCE.getStartColor(), false);
			FontUtils.drawString(debug, 12 + FontUtils.getStringWidth(name + version), 15, -1, false);
			GlStateManager.popMatrix();
			
			Gui.drawRect(6, 4, (int) (FontUtils.getStringWidth(name + version + debug)), (int) (5 * 1.5), ClickGUI.INSTANCE.getStartColor());
			
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.9F, 0.9F, 1);
			int moduleHeight = 12;
			
			//for (Module module : Client.modules) {
			ArrayList<Module> mods = ModuleManager.getActiveModules();
			mods.sort(MODULE_COMPARATOR);
			
			for(Module m : mods) {
				GlStateManager.pushMatrix();
				GlStateManager.translate((sr.getScaledWidth() / 0.9) - 104, y, 1);
				FontUtils.drawString(m.name, 100 - FontUtils.getStringWidth(m.name) - 4, 1, ClickGUI.INSTANCE.getStartColor(), true);
				GlStateManager.popMatrix();
				
				y += moduleHeight + 2;
			}
			GlStateManager.popMatrix();
		}
	}
}
