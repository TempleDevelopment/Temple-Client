package xyz.templecheats.templeclient.impl.gui.clickgui.setting.component.varients;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.gui.Gui;
import xyz.templecheats.templeclient.impl.modules.client.ClickGUI;
import xyz.templecheats.templeclient.impl.gui.clickgui.setting.component.Component;
import xyz.templecheats.templeclient.impl.gui.clickgui.Button;
import xyz.templecheats.templeclient.impl.gui.font.FontUtils;

public class Keybind extends Component {

	private boolean hovered;
	private boolean binding;
	private xyz.templecheats.templeclient.impl.gui.clickgui.Button parent;
	private int offset;
	private int x;
	private int y;

	public Keybind(Button button, int offset) {
		this.parent = button;
		this.x = button.parent.getX() + button.parent.getWidth();
		this.y = button.parent.getY() + button.offset;
		this.offset = offset;
	}

	@Override
	public void setOff(int newOff) {
		offset = newOff;
	}

	@Override
	public void renderComponent() {

		Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + (parent.parent.getWidth() * 1), parent.parent.getY() + offset + 12, this.hovered ? 0xFF222222 : 0xFF111111);

		int borderThickness = 1;
		int borderColor = ClickGUI.RGBColor.getRGB();
		Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + borderThickness, parent.parent.getY() + offset + 12, borderColor);

		int rightBorderX = parent.parent.getX() + parent.parent.getWidth() - borderThickness;

		Gui.drawRect(rightBorderX, parent.parent.getY() + offset, parent.parent.getX() + parent.parent.getWidth(), parent.parent.getY() + offset + 12, borderColor);

		int bottomBorderY = parent.parent.getY() + offset + 12 - borderThickness;

		Gui.drawRect(parent.parent.getX(), bottomBorderY, parent.parent.getX() + parent.parent.getWidth(), parent.parent.getY() + offset + 12, borderColor);

		GlStateManager.pushMatrix();
		GlStateManager.scale(0.5f, 0.5f, 0.5f);

		int scaledX = (int) ((parent.parent.getX() + 7) / 0.5f);
		int scaledY = (int) ((parent.parent.getY() + offset + 2) / 0.5f + 2);

		FontUtils.normal.drawString(binding ? "< PRESS KEY >" : ("Key: " + Keyboard.getKeyName(this.parent.mod.getKey())), scaledX, scaledY, -1);

		GlStateManager.popMatrix();
	}



	@Override
	public void updateComponent(int mouseX, int mouseY) {
		this.hovered = isMouseOnButton(mouseX, mouseY);
		this.y = parent.parent.getY() + offset;
		this.x = parent.parent.getX();
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		if (isMouseOnButton(mouseX, mouseY) && button == 0 && this.parent.open) {
			this.binding = !this.binding;
		}
	}

	@Override
	public void keyTyped(char typedChar, int key) {
		if (this.binding && key != Keyboard.KEY_DELETE) {
			this.parent.mod.setKey(key);
			this.binding = false;
		} else if (this.binding && key == Keyboard.KEY_DELETE) {
			this.parent.mod.setKey(0);
			this.binding = false;
		}
	}

	public boolean isMouseOnButton(int x, int y) {
		if (x > this.x && x < this.x + 88 && y > this.y && y < this.y + 12) {
			return true;
		}
		return false;
	}
}
