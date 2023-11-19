package xyz.templecheats.templeclient.setting.component.components;

import java.util.ArrayList;

import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.modules.Module;
import xyz.templecheats.templeclient.setting.Setting;
import xyz.templecheats.templeclient.setting.component.Component;
import xyz.templecheats.templeclient.setting.component.Frame;
import xyz.templecheats.templeclient.setting.component.components.varients.Checkbox;
import xyz.templecheats.templeclient.setting.component.components.varients.Keybind;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import xyz.templecheats.templeclient.setting.component.components.varients.ModeButton;
import xyz.templecheats.templeclient.setting.component.components.varients.Slider;

public class Button extends xyz.templecheats.templeclient.setting.component.Component {

	public Module mod;
	public Frame parent;
	public int offset;
	private boolean isHovered;
	private ArrayList<xyz.templecheats.templeclient.setting.component.Component> subcomponents;
	public boolean open;
	public int height;
	public FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
	public Button(Module mod, Frame parent, int offset) {
		this.mod = mod;
		this.parent = parent;
		this.offset = offset;
		this.height = 12;
		this.subcomponents = new ArrayList<xyz.templecheats.templeclient.setting.component.Component>();
		this.open = false;
		int opY = offset + 12;
		if(TempleClient.instance.settingsManager.getSettingsByMod(mod) != null) {
			for(Setting s : TempleClient.instance.settingsManager.getSettingsByMod(mod)){
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
		for(xyz.templecheats.templeclient.setting.component.Component comp : this.subcomponents) {
			comp.setOff(opY);
			opY += 12;
		}
	}

	@Override
	public void renderComponent() {
		Gui.drawRect(parent.getX(), this.parent.getY() + this.offset, parent.getX() + parent.getWidth(), this.parent.getY() + 12 + this.offset, this.isHovered ? 0xFF222222 : 0xFF111111);
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(this.mod.getName(), (parent.getX() + 5), (parent.getY() + offset + 2), this.mod.isEnabled() ? 0xFFADD8E6 : 0xFFFFFF);
		if(this.subcomponents.size() >= 2) {
			Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(this.open ? "-" : "+", (parent.getX()+parent.getWidth()-10), (parent.getY() + offset + 2), 0xADD8E6);
		}
		if(this.open) {
			if(!this.subcomponents.isEmpty()) {
				for(xyz.templecheats.templeclient.setting.component.Component comp : this.subcomponents) {
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
			for(xyz.templecheats.templeclient.setting.component.Component comp : this.subcomponents) {
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
		for(xyz.templecheats.templeclient.setting.component.Component comp : this.subcomponents) {
			comp.mouseClicked(mouseX, mouseY, button);
		}
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		for(xyz.templecheats.templeclient.setting.component.Component comp : this.subcomponents) {
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
