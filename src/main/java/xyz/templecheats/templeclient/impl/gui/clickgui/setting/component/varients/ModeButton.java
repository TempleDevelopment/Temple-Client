package xyz.templecheats.templeclient.impl.gui.clickgui.setting.component.varients;

import net.minecraft.client.renderer.GlStateManager;
import xyz.templecheats.templeclient.impl.modules.client.ClickGUI;
import xyz.templecheats.templeclient.impl.gui.clickgui.setting.Setting;
import xyz.templecheats.templeclient.impl.gui.clickgui.setting.component.Component;
import xyz.templecheats.templeclient.impl.gui.clickgui.Button;
import xyz.templecheats.templeclient.impl.modules.Module;

import net.minecraft.client.gui.Gui;
import xyz.templecheats.templeclient.impl.gui.font.FontUtils;

public class ModeButton extends Component {

	private boolean hovered;
	private xyz.templecheats.templeclient.impl.gui.clickgui.Button parent;
	private Setting set;
	private int offset;
	private int x;
	private int y;
	private Module mod;
	private int modeIndex;
	
	public ModeButton(Setting set, Button button, Module mod, int offset) {
		this.set = set;
		this.parent = button;
		this.mod = mod;
		this.x = button.parent.getX() + button.parent.getWidth();
		this.y = button.parent.getY() + button.offset;
		this.offset = offset;
		this.modeIndex = 0;

		set.setValString(set.getOptions().get(0));
	}
	
	@Override
	public void setOff(int newOff) {
		offset = newOff;
	}

	@Override
	public void renderComponent() {

		Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + (parent.parent.getWidth() * 1), parent.parent.getY() + offset + 12, this.hovered ? 0xFF222222 : 0xFF111111);

		int borderThickness = 2;
		int borderColor = ClickGUI.RGBColor.getRGB();
		Gui.drawRect(parent.parent.getX(), parent.parent.getY() + offset, parent.parent.getX() + borderThickness, parent.parent.getY() + offset + 12, borderColor);

		GlStateManager.pushMatrix();
		GlStateManager.scale(0.5f, 0.5f, 0.5f);

		int scaledX = (int) ((parent.parent.getX() + 7) / 0.5f);
		int scaledY = (int) (((parent.parent.getY() + offset + 2) + 1) / 0.5f);

		FontUtils.normal.drawString(set.getTitle() + ": " + set.getOptions().get(modeIndex), scaledX, scaledY, -1);

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
		if(isMouseOnButton(mouseX, mouseY) && button == 0 && this.parent.open) {
			int maxIndex = set.getOptions().size();

			if(modeIndex + 1 >= maxIndex)
				modeIndex = 0;
			else
				modeIndex++;

			set.setValString(set.getOptions().get(modeIndex));
		}
	}
	
	public boolean isMouseOnButton(int x, int y) {
		if(x > this.x && x < this.x + 88 && y > this.y && y < this.y + 12) {
			return true;
		}
		return false;
	}
}
