package xyz.templecheats.templeclient.gui.clickgui;

import java.awt.Color;
import java.util.ArrayList;

import xyz.templecheats.templeclient.Client;
import xyz.templecheats.templeclient.features.modules.client.ClickGUI;
import xyz.templecheats.templeclient.gui.clickgui.setting.component.Component;
import xyz.templecheats.templeclient.features.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.*;

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

		for(Module mod : Client.getModulesInCategory(category)) {
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

		Gui.drawRect(this.x - 1, this.y - 2, this.x + this.width + 1, this.y - 1, ClickGUI.RGBColor.getRGB());
		Gui.drawRect(this.x - 1, this.y - 2, this.x, this.y + 12, ClickGUI.RGBColor.getRGB());
		Gui.drawRect(this.x + this.width, this.y - 2, this.x + this.width + 1, this.y + 12, ClickGUI.RGBColor.getRGB());
		Gui.drawRect(this.x - 1, this.y + 12, this.x + this.width + 1, this.y + 13, ClickGUI.RGBColor.getRGB());

			Gui.drawRect(this.x, this.y-1, this.x + this.width, this.y, new Color(ClickGUI.RGBColor.getRGB(), true).hashCode());
			Gui.drawRect(this.x, this.y, this.x + this.width, this.y + 12, new Color(0xD81C1B1B, true).hashCode());
			Minecraft.getMinecraft(); fontRenderer.drawStringWithShadow(this.category.name(), this.x + 5, this.y + 2, -1);
			if(this.open) {
				if(!this.components.isEmpty()) {
					for(Component component : components) {
						component.renderComponent();
					}
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
