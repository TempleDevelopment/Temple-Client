package xyz.templecheats.templeclient.impl.gui.clickgui;

import java.util.ArrayList;

import net.minecraft.client.renderer.GlStateManager;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.impl.modules.client.ClickGUI;
import xyz.templecheats.templeclient.impl.gui.clickgui.setting.Setting;
import xyz.templecheats.templeclient.impl.gui.clickgui.setting.component.Component;
import xyz.templecheats.templeclient.impl.gui.clickgui.setting.component.varients.Keybind;
import xyz.templecheats.templeclient.impl.gui.clickgui.setting.component.varients.ModeButton;
import xyz.templecheats.templeclient.impl.gui.clickgui.setting.component.varients.Slider;
import xyz.templecheats.templeclient.impl.modules.Module;
import xyz.templecheats.templeclient.impl.gui.clickgui.setting.component.varients.Checkbox;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import xyz.templecheats.templeclient.impl.gui.font.FontUtils;

public class Button extends Component {

	public Module mod;
	public Frame parent;
	public int offset;
	private boolean isHovered;
	private ArrayList<Component> subcomponents;
	public boolean open;
	public int height;
	public FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
	public Button(Module mod, Frame parent, int offset) {
		this.mod = mod;
		this.parent = parent;
		this.offset = offset;
		this.height = 12;
		this.subcomponents = new ArrayList<Component>();
		this.open = false;
		int opY = offset + 12;
		if(TempleClient.settingsManager.getSettingsByMod(mod) != null) {
			for(Setting s : TempleClient.settingsManager.getSettingsByMod(mod)){
				if(s.isCombo()){
					this.subcomponents.add(new ModeButton(s, this, mod, opY));
					opY += 12;
				}
				if(s.isSlider()){
					this.subcomponents.add(new Slider(s, this, opY));
					opY += 12;
				}
				if(s.isCheck()){
					this.subcomponents.add(new Checkbox(s, this, opY));
					opY += 12;
				}
			}
		}
		this.subcomponents.add(new Keybind(this, opY));
	}

	@Override
	public void setOff(int newOff) {
		offset = newOff;
		int opY = offset + 12;
		for(Component comp : this.subcomponents) {
			comp.setOff(opY);
			opY += 12;
		}
	}

	@Override
	public void renderComponent() {
		Gui.drawRect(parent.getX(), this.parent.getY() + this.offset, parent.getX() + parent.getWidth(), this.parent.getY() + 12 + this.offset, this.isHovered ? 0xFF222222 : 0xFF111111);

		int borderThickness = 1;
		int borderColor = ClickGUI.RGBColor.getRGB();

		int adjustedX = (int) ((parent.getX() + 5) / 0.7f);
		int adjustedY = (int) ((parent.getY() + offset + 3) / 0.7f);

		if (this.open && !this.subcomponents.isEmpty()) {
			int rightBorderX = parent.getX() + parent.getWidth() - borderThickness;

			int totalHeight = 12;
			for (Component comp : this.subcomponents) {
				totalHeight += comp.getHeight();
			}

			Gui.drawRect(rightBorderX, this.parent.getY() + this.offset, parent.getX() + parent.getWidth(), this.parent.getY() + this.offset + totalHeight, borderColor);

			Gui.drawRect(parent.getX(), this.parent.getY() + this.offset, parent.getX() + parent.getWidth(), this.parent.getY() + this.offset + borderThickness, borderColor);
		}

		Gui.drawRect(parent.getX(), this.parent.getY() + this.offset, parent.getX() + borderThickness, this.parent.getY() + 12 + this.offset, borderColor);

		GlStateManager.pushMatrix();

		GlStateManager.scale(0.7f, 0.7f, 1.0f);

		FontUtils.normal.drawString(this.mod.getName(), adjustedX, adjustedY, this.mod.isEnabled() ? ClickGUI.RGBColor.getRGB() : 0xFFFFFF);

		GlStateManager.popMatrix();

		if (this.subcomponents.size() >= 2) {
			Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(this.open ? "-" : "+", (parent.getX() + parent.getWidth() - 10), (parent.getY() + offset + 2), ClickGUI.RGBColor.getRGB());
		}
		if (this.open) {
			if (!this.subcomponents.isEmpty()) {
				for (Component comp : this.subcomponents) {
					comp.renderComponent();
				}
			}
		}
	}




	@Override
	public int getHeight() {
		if(this.open) {
			return (12 * (this.subcomponents.size() + 1));
		}
		return 12;
	}

	@Override
	public void updateComponent(int mouseX, int mouseY) {
		this.isHovered = isMouseOnButton(mouseX, mouseY);
		if(!this.subcomponents.isEmpty()) {
			for(Component comp : this.subcomponents) {
				comp.updateComponent(mouseX, mouseY);
			}
		}
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		if(isMouseOnButton(mouseX, mouseY) && button == 0) {
			this.mod.toggle();
		}
		if(isMouseOnButton(mouseX, mouseY) && button == 1) {
			this.open = !this.open;
			this.parent.refresh();
		}
		for(Component comp : this.subcomponents) {
			comp.mouseClicked(mouseX, mouseY, button);
		}
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		for(Component comp : this.subcomponents) {
			comp.mouseReleased(mouseX, mouseY, mouseButton);
		}
	}

	@Override
	public void keyTyped(char typedChar, int key) {
		for(Component comp : this.subcomponents) {
			comp.keyTyped(typedChar, key);
		}
	}

	public boolean isMouseOnButton(int x, int y) {
		if(x > parent.getX() && x < parent.getX() + parent.getWidth() && y > this.parent.getY() + this.offset && y < this.parent.getY() + 12 + this.offset) {
			return true;
		}
		return false;
	}
}
