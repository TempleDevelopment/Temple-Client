package xyz.templecheats.templeclient.impl.gui.clickgui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;
import xyz.templecheats.templeclient.ModuleManager;
import xyz.templecheats.templeclient.api.util.render.RenderUtil;
import xyz.templecheats.templeclient.impl.gui.clickgui.item.Item;
import xyz.templecheats.templeclient.impl.gui.clickgui.item.ModuleButton;
import xyz.templecheats.templeclient.impl.modules.Module;
import xyz.templecheats.templeclient.impl.modules.client.ClickGUI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public final class ClickGuiScreen extends GuiScreen {
	private static ClickGuiScreen clickGui;
	private final ArrayList<Panel> panels = new ArrayList<>();
	
	public ClickGuiScreen() {
		if(this.getPanels().isEmpty()) {
			this.load();
		}
	}
	
	public static ClickGuiScreen getClickGui() {
		return clickGui == null ? (clickGui = new ClickGuiScreen()) : clickGui;
	}
	
	private void load() {
		int x = -42;
		for(final Module.Category category : Module.Category.values()) {
			this.panels.add(new Panel(category.name(), x += 90, 25, true) {
				
				@Override
				public void setupItems() {
					ModuleManager.getModules().forEach(module -> {
						if(module.getCategory() == category) {
							this.addButton(new ModuleButton(module));
						}
					});
				}
			});
		}
		this.panels.forEach(panel -> panel.getItems().sort(Comparator.comparing(Item::getLabel)));
	}
	
	@Override
	public void drawScreen(int unscaledMouseX, int unscaledMouseY, float partialTicks) {
		
		
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		RenderUtil.drawGradientRect(0.0F, 0.0F, mc.displayWidth, mc.displayHeight, 536870912, -1879048192);
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
		
		GlStateManager.pushMatrix();
		GlStateManager.scale(ClickGUI.INSTANCE.scale.getValDouble(), ClickGUI.INSTANCE.scale.getValDouble(), 0);
		final int mouseX = (int) (unscaledMouseX / ClickGUI.INSTANCE.scale.getValDouble());
		final int mouseY = (int) (unscaledMouseY / ClickGUI.INSTANCE.scale.getValDouble());
		final int scroll = Mouse.getDWheel();
		this.panels.forEach(panel -> {
			if(scroll < 0) {
				panel.setY(panel.getY() - ClickGUI.INSTANCE.scrollSpeed.getValInt());
			} else if(scroll > 0) {
				panel.setY(panel.getY() + ClickGUI.INSTANCE.scrollSpeed.getValInt());
			}
			panel.drawScreen(mouseX, mouseY, partialTicks);
		});
		this.panels.forEach(panel -> panel.drawScreenPost(mouseX, mouseY));
		GlStateManager.popMatrix();
	}

//    @Override
//    public void drawScreen(int unscaledMouseX, int unscaledMouseY, float partialTicks) {
//        this.drawDefaultBackground();
//        this.panels.forEach(panel -> panel.drawScreen(mouseX, mouseY, partialTicks));
//    }
	
	@Override
	public void mouseClicked(int unscaledMouseX, int unscaledMouseY, int clickedButton) {
		final int mouseX = (int) (unscaledMouseX / ClickGUI.INSTANCE.scale.getValDouble());
		final int mouseY = (int) (unscaledMouseY / ClickGUI.INSTANCE.scale.getValDouble());
		this.panels.forEach(panel -> panel.mouseClicked(mouseX, mouseY, clickedButton));
	}
	
	@Override
	public void mouseReleased(int unscaledMouseX, int unscaledMouseY, int releaseButton) {
		final int mouseX = (int) (unscaledMouseX / ClickGUI.INSTANCE.scale.getValDouble());
		final int mouseY = (int) (unscaledMouseY / ClickGUI.INSTANCE.scale.getValDouble());
		this.panels.forEach(panel -> panel.mouseReleased(mouseX, mouseY, releaseButton));
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) {
		try {
			for(Panel panel : panels) {
				panel.keyTyped(typedChar, keyCode);
			}
			super.keyTyped(typedChar, keyCode);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	public ArrayList<Panel> getPanels() {
		return this.panels;
	}
}

