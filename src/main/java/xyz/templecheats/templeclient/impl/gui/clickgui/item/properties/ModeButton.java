package xyz.templecheats.templeclient.impl.gui.clickgui.item.properties;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import xyz.templecheats.templeclient.api.util.render.RenderUtil;
import xyz.templecheats.templeclient.impl.gui.clickgui.item.Button;
import xyz.templecheats.templeclient.impl.gui.font.FontUtils;
import xyz.templecheats.templeclient.impl.gui.setting.Setting;

public class ModeButton extends Button {
	private final Setting property;
	
	public ModeButton(Setting property) {
		super(property.getName());
		this.property = property;
		width = 15;
	}
	
	@Override
	public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
		RenderUtil.drawRect(x, y, x + width + 7.4F, y + height, !isHovering(mouseX, mouseY) ? 0x11333333 : 0x88333333);
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(this.x + 2.3, this.y + 4, 0);
		GlStateManager.scale(0.8, 0.8, 0);
		FontUtils.drawString(getLabel() + TextFormatting.GRAY + " " + property.getValString(), 0, 0, 0xFFFFFFFF, false);
		GlStateManager.popMatrix();
	}
	
	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if(isHovering(mouseX, mouseY)) {
			int count = property.getOptions().indexOf(property.getValString());
			if(mouseButton == 0) {
				count++;
			} else if(mouseButton == 1) {
				count--;
			} else {
				return;
			}
			if(count > property.getOptions().size() - 1) count = 0;
			if(count < 0) count = property.getOptions().size() - 1;
			property.setValString(property.getOptions().get(count));
		}
	}
	
	@Override
	public int getHeight() {
		return 14;
	}
	
	@Override
	public boolean getState() {
		return false;
	}
}
