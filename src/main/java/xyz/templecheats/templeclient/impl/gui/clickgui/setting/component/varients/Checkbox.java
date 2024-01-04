package xyz.templecheats.templeclient.impl.gui.clickgui.setting.component.varients;

import java.awt.Color;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.gui.Gui;
import xyz.templecheats.templeclient.impl.modules.client.ClickGUI;
import xyz.templecheats.templeclient.impl.gui.clickgui.setting.Setting;
import xyz.templecheats.templeclient.impl.gui.clickgui.setting.component.Component;
import xyz.templecheats.templeclient.impl.gui.clickgui.Button;
import xyz.templecheats.templeclient.impl.gui.font.FontUtils;

public class Checkbox extends Component {

	private boolean hovered;
	private Setting op;
	private Button parent;
	private int offset;
	private int x;
	private int y;
	
	public Checkbox(Setting option, Button button, int offset) {
		this.op = option;
		this.parent = button;
		this.x = button.parent.getX() + button.parent.getWidth();
		this.y = button.parent.getY() + button.offset;
		this.offset = offset;
	}

	@Override
	public void renderComponent() {

		Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + (parent.parent.getWidth() * 1), parent.parent.getY() + offset + 12, this.hovered ? 0xFF222222 : 0xFF111111);

		int borderThickness = 2;
		int borderColor = ClickGUI.RGBColor.getRGB();
		Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + borderThickness, parent.parent.getY() + offset + 12, borderColor);

		GlStateManager.pushMatrix();
		GlStateManager.scale(0.5f, 0.5f, 0.5f);

		int originalX = parent.parent.getX() + 10 + 4 - 1;
		int originalY = parent.parent.getY() + offset + 2 - 1;

		int scaledX = (int) ((originalX / 0.5f) + 5);
		int scaledY = (int) ((originalY / 0.5f) + 4);

		FontUtils.normal.drawString(this.op.getName(), scaledX, scaledY, -1);

		GlStateManager.popMatrix();

		if (this.op.getValBoolean()) {
			Gui.drawRect(parent.parent.getX() + 4 + 4, parent.parent.getY() + offset + 4, parent.parent.getX() + 8 + 4, parent.parent.getY() + offset + 8, ClickGUI.RGBColor.hashCode());
		} else {
			Gui.drawRect(parent.parent.getX() + 4 + 4, parent.parent.getY() + offset + 4, parent.parent.getX() + 8 + 4, parent.parent.getY() + offset + 8, ClickGUI.RGBColor.hashCode());
			Gui.drawRect(parent.parent.getX() + 5 + 4, parent.parent.getY() + offset + 5, parent.parent.getX() + 7 + 4, parent.parent.getY() + offset + 7, new Color(0xFF222222).hashCode());
		}
	}




	@Override
	public void setOff(int newOff) {
		offset = newOff;
	}
	
	@Override
	public void updateComponent(int mouseX, int mouseY) {
		this.hovered = isMouseOnButton(mouseX, mouseY);
		this.y = parent.parent.getY() + offset;
		this.x = parent.parent.getX();
	}
	
	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		if(isMouseOnButton(mouseX, mouseY) && button == 0 && this.parent.open) {
			this.op.setValBoolean(!op.getValBoolean());;
		}
	}
	
	public boolean isMouseOnButton(int x, int y) {
		if(x > this.x && x < this.x + 88 && y > this.y && y < this.y + 12) {
			return true;
		}
		return false;
	}
}
