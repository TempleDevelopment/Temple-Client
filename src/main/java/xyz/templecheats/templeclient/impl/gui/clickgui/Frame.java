package xyz.templecheats.templeclient.impl.gui.clickgui;

import java.util.ArrayList;

import net.minecraft.client.renderer.GlStateManager;
import xyz.templecheats.templeclient.ModuleManager;
import xyz.templecheats.templeclient.impl.modules.client.ClickGUI;
import xyz.templecheats.templeclient.impl.gui.clickgui.setting.component.Component;
import xyz.templecheats.templeclient.impl.modules.Module;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.*;
import xyz.templecheats.templeclient.impl.gui.font.FontUtils;

public class Frame {

	public ArrayList<Component> components;
	public Module.Category category;
	private boolean open;
	private int width;
	private int y;
	private int x;
	private int barHeight;
	private boolean isDragging;
	public int dragX;
	public int dragY;

	public static int color;

	private float currentHeight;
	private float targetHeight;
	public Frame(Module.Category cat) {
		this.components = new ArrayList<Component>();
		this.category = cat;
		this.width = 88;
		this.x = 0;
		this.y = 60;
		this.dragX = 0;
		this.barHeight = 12;
		this.open = false;
		this.isDragging = false;
		int tY = this.barHeight;

		for(Module mod : ModuleManager.getModulesInCategory(category)) {
			Button modButton = new Button(mod, this, tY);
			this.components.add(modButton);
			tY += 12;
		}
	}

	public ArrayList<Component> getComponents() {
		return components;
	}

	public void setX(int newX) {
		this.x = newX;
	}

	public void setY(int newY) {
		this.y = newY;
	}

	public void setDrag(boolean drag) {
		this.isDragging = drag;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public void renderFrame(FontRenderer fontRenderer) {

		GlStateManager.pushMatrix();

		Gui.drawRect(this.x, this.y, this.x + this.width, this.y + 12, ClickGUI.RGBColor.getRGB());

		int adjustedX = (int) ((this.x + 5) / 0.7f);
		int adjustedY = (int) ((this.y + 3) / 0.7f);

		GlStateManager.scale(0.7f, 0.7f, 1.0f);

		FontUtils.normal.drawString(this.category.name(), adjustedX, adjustedY, -1);

		GlStateManager.popMatrix();

		if (this.open && !this.components.isEmpty()) {
			for (Component component : components) {
				component.renderComponent();
			}
		}
	}




	public void refresh() {
		int off = this.barHeight;
		for(Component comp : components) {
			comp.setOff(off);
			off += comp.getHeight();
		}
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getWidth() {
		return width;
	}

	public void updatePosition(int mouseX, int mouseY) {
		if(this.isDragging) {
			this.setX(mouseX - dragX);
			this.setY(mouseY - dragY);
		}
	}

	public boolean isWithinHeader(int x, int y) {
		if(x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.barHeight) {
			return true;
		}
		return false;
	}
	
}
