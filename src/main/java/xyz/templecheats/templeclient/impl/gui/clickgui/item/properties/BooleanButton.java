package xyz.templecheats.templeclient.impl.gui.clickgui.item.properties;

import net.minecraft.client.renderer.GlStateManager;
import xyz.templecheats.templeclient.api.util.render.RenderUtil;
import xyz.templecheats.templeclient.impl.gui.clickgui.item.Button;
import xyz.templecheats.templeclient.impl.gui.font.FontUtils;
import xyz.templecheats.templeclient.impl.gui.setting.Setting;
import xyz.templecheats.templeclient.impl.modules.client.ClickGUI;

public class BooleanButton extends Button {
	private final Setting property;
	
	public BooleanButton(Setting property) {
		super(property.getName());
		this.property = property;
		width = 15;
	}
	
	@Override
	public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
		if(this.getState()) {
			RenderUtil.drawRect(x, y, x + width + 7.4F, y + height, ClickGUI.INSTANCE.getStartColor());
			
			if(this.isHovering(mouseX, mouseY)) {
				RenderUtil.drawRect(x, y, x + width + 7.4F, y + height, 0x22000000);
			}
		} else {
			RenderUtil.drawRect(x, y, x + width + 7.4F, y + height, !this.isHovering(mouseX, mouseY) ? 0x11555555 : 0x88555555);
		}
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(this.x + 2.3, this.y + 4, 0);
		GlStateManager.scale(0.8, 0.8, 0);
		FontUtils.drawString(getLabel(), 0, 0, 0xFFFFFFFF, false);
		GlStateManager.popMatrix();
	}
	
	@Override
	public int getHeight() {
		return 14;
	}
	
	@Override
	public void toggle() {
		property.setValBoolean(!property.getValBoolean());
	}
	
	@Override
	public boolean getState() {
		return property.getValBoolean();
	}
}
