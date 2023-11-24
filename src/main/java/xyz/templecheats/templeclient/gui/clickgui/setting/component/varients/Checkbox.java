package xyz.templecheats.templeclient.gui.clickgui.setting.component.varients;

import java.awt.Color;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import xyz.templecheats.templeclient.gui.clickgui.setting.Setting;
import xyz.templecheats.templeclient.gui.clickgui.setting.component.Component;
import xyz.templecheats.templeclient.gui.clickgui.Button;

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
		// Cyan border on sides
		Gui.drawRect(parent.parent.getX() - 1, parent.parent.getY() + offset - 1, parent.parent.getX() + parent.parent.getWidth() + 1, parent.parent.getY() + offset + 13, new Color(0xFFADD8E6).getRGB());

		Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + (parent.parent.getWidth() * 1), parent.parent.getY() + offset + 12, this.hovered ? 0xFF222222 : 0xFF111111);
		GL11.glPushMatrix();
		GL11.glScalef(0.7f, 0.7f, 0.7f);
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(this.op.getName(), (int) ((parent.parent.getX() + 10 + 4 - 1) / 0.7f) + 5, (int) ((parent.parent.getY() + offset + 2 - 1) / 0.7f) + 4, -1);
		GL11.glPopMatrix();
		if (this.op.getValBoolean()) {
			Gui.drawRect(parent.parent.getX() + 4 + 4, parent.parent.getY() + offset + 4, parent.parent.getX() + 8 + 4, parent.parent.getY() + offset + 8, new Color(0xADD8E6).hashCode());
		} else {
			Gui.drawRect(parent.parent.getX() + 4 + 4, parent.parent.getY() + offset + 4, parent.parent.getX() + 8 + 4, parent.parent.getY() + offset + 8, new Color(0xADD8E6).hashCode());
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
