package xyz.templecheats.templeclient.impl.modules.misc;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import xyz.templecheats.templeclient.impl.modules.Module;

public class Hitmarker extends Module {
	private ResourceLocation hitmarkerTexture = new ResourceLocation("textures/hitmarker.png");
	private int hitmarkerDuration = 0;
	
	public Hitmarker() {
		super("Hitmarker", Keyboard.KEY_NONE, Module.Category.MISC);
	}

	@SubscribeEvent
	public void onAttackEntity(AttackEntityEvent event) {
		if(event.getEntityPlayer().equals(mc.player)) {
			this.hitmarkerDuration = 5;
		}
	}

	@SubscribeEvent
	public void renderGameOverlay(RenderGameOverlayEvent.Post event) {
		if(event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
			return;
		}

		if(this.hitmarkerDuration-- > 0) {
			ScaledResolution scaledResolution = new ScaledResolution(mc);
			int centerX = scaledResolution.getScaledWidth() / 2;
			int centerY = scaledResolution.getScaledHeight() / 2;
			int size = 16;

			GL11.glEnable(3042);
			GL11.glBlendFunc(770, 771);
			GL11.glPushMatrix();
			mc.getTextureManager().bindTexture(hitmarkerTexture);
			this.drawModalRectWithCustomSizedTexture(centerX - 7.5, centerY - 7.5, 0, 0, size, size, size, size);
			GL11.glPopMatrix();
			GL11.glDisable(3042);
			GL11.glDepthFunc(515);
			mc.getTextureManager().bindTexture(Gui.ICONS);
		}
	}

	@Override
	public void onDisable() {
		this.hitmarkerDuration = 0;
	}
	private void drawModalRectWithCustomSizedTexture(double x, double y, float u, float v, int width, int height, float textureWidth, float textureHeight) {
		float f = 1.0F / textureWidth;
		float f1 = 1.0F / textureHeight;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(x, (y + height), 0.0D).tex((u * f), ((v + (float) height) * f1)).endVertex();
		bufferbuilder.pos((x + width), (y + height), 0.0D).tex(((u + (float) width) * f), ((v + (float) height) * f1)).endVertex();
		bufferbuilder.pos((x + width), y, 0.0D).tex(((u + (float) width) * f), (v * f1)).endVertex();
		bufferbuilder.pos(x, y, 0.0D).tex((u * f), (v * f1)).endVertex();
		tessellator.draw();
	}
}