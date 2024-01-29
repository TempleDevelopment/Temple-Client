package xyz.templecheats.templeclient.impl.gui.clickgui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import xyz.templecheats.templeclient.api.util.render.RenderUtil;
import xyz.templecheats.templeclient.impl.gui.clickgui.item.Button;
import xyz.templecheats.templeclient.impl.gui.clickgui.item.Item;
import xyz.templecheats.templeclient.impl.gui.font.FontUtils;
import xyz.templecheats.templeclient.impl.modules.client.ClickGUI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class Panel {
	private final Minecraft mc = Minecraft.getMinecraft();
	private final String label;
	private int angle;
	private int x;
	private int y;
	private int x2;
	private int y2;
	private int width;
	private int height;
	private boolean open;
	public boolean drag;
	private final List<Item> items = new ArrayList<>();
	
	public Panel(String label, int x, int y, boolean open) {
		this.label = label;
		this.x = x;
		this.y = y;
		this.angle = 180;
		this.width = 88;
		this.height = 18;
		this.open = open;
		this.setupItems();
	}
	
	public abstract void setupItems();
	
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drag(mouseX, mouseY);
		float totalItemHeight = this.open ? this.getTotalItemHeight() - 2.0f : 0.0f;
		RenderUtil.drawRect(this.x, (float) this.y - 1.5f, this.x + this.width, this.y + this.height - 6, 0x77000000);
		RenderUtil.drawGradientRect(this.x, (float) this.y - 1.5f, this.x + this.width, this.y + this.height - 6, ClickGUI.INSTANCE.getStartColor(), ClickGUI.INSTANCE.getEndColor());//0x77FB4242, 0x77FB4242);
		if(this.open) {
			RenderUtil.drawRect(this.x, (float) this.y + 12.5f, this.x + this.width, this.open ? (float) (this.y + this.height) + totalItemHeight : (float) (this.y + this.height - 1), 0x77000000);//1996488704
		}
		FontUtils.drawString(this.getLabel(), (float) this.x + 3.0f, (float) this.y + 1.5f/* - 4.0f*/, -1, false); //15592941
		
		if(!open) {
			if(this.angle > 0) {
				this.angle -= 6;
			}
		} else if(this.angle < 180) {
			this.angle += 6;
		}
		
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		//GlStateManager.color(1, 1, 1, 1);
		mc.getTextureManager().bindTexture(new ResourceLocation("textures/arrow.png"));
		GlStateManager.translate(getX() + getWidth() - 7, (getY() + 6) - 0.3F, 0.0F);
		GlStateManager.rotate(calculateRotation(angle), 0.0F, 0.0F, 1.0F);
		Gui.drawScaledCustomSizeModalRect(-5, -5, 0.0F, 0.0F, 10, 10, 10, 10, 10.0F, 10.0F);
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
		
		if(this.open) {
			float y = (float) (this.getY() + this.getHeight()) - 3.0f;
			for(Item item : getItems()) {
				item.setLocation((float) this.x + 2.0f, y);
				item.setWidth(this.getWidth() - 4);
				item.drawScreen(mouseX, mouseY, partialTicks);
				y += (float) item.getHeight() + 1.5f;
			}
		}
	}
	
	private void drag(int mouseX, int mouseY) {
		if(!this.drag) {
			return;
		}
		this.x = this.x2 + mouseX;
		this.y = this.y2 + mouseY;
	}
	
	public void drawScreenPost(int mouseX, int mouseY) {
		if(this.open) {
			for(Item item : getItems()) {
				item.drawScreenPost(mouseX, mouseY);
			}
		}
	}
	
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if(mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
			this.x2 = this.x - mouseX;
			this.y2 = this.y - mouseY;
			ClickGuiScreen.getClickGui().getPanels().forEach(panel -> {
				if(panel.drag) {
					panel.drag = false;
				}
			});
			this.drag = true;
			return;
		}
		if(mouseButton == 1 && this.isHovering(mouseX, mouseY)) {
			this.open = !this.open;
			mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			return;
		}
		if(!this.open) {
			return;
		}
		this.getItems().forEach(item -> item.mouseClicked(mouseX, mouseY, mouseButton));
	}
	
	public void addButton(Button button) {
		this.items.add(button);
	}
	
	public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
		if(releaseButton == 0) {
			this.drag = false;
		}
		if(!this.open) {
			return;
		}
		this.getItems().forEach(item -> item.mouseReleased(mouseX, mouseY, releaseButton));
	}
	
	public void keyTyped(char typedChar, int keyCode) throws IOException {
		for(Item item : getItems()) {
			item.keyTyped(typedChar, keyCode);
		}
	}
	
	public final String getLabel() {
		return this.label;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public void setOpen(boolean open) {
		this.open = open;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public boolean getOpen() {
		return this.open;
	}
	
	public final List<Item> getItems() {
		return this.items;
	}
	
	private boolean isHovering(int mouseX, int mouseY) {
		return mouseX >= this.getX() && mouseX <= this.getX() + this.getWidth() && mouseY >= this.getY() && mouseY <= this.getY() + this.getHeight() - (this.open ? 2 : 0);
	}
	
	//added this method in, just to fix shit. It is from uz1 class in future
	public static float calculateRotation(float var0) {
		if((var0 %= 360.0F) >= 180.0F) {
			var0 -= 360.0F;
		}
		
		if(var0 < -180.0F) {
			var0 += 360.0F;
		}
		
		return var0;
	}
	
	private float getTotalItemHeight() {
		float height = 0.0f;
		for(Item item : getItems()) {
			height += (float) item.getHeight() + 1.5f;
		}
		return height;
	}
	
	public void setX(int dragX) {
		this.x = dragX;
	}
	
	public void setY(int dragY) {
		this.y = dragY;
	}
}

