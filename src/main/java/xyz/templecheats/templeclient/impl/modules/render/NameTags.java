package xyz.templecheats.templeclient.impl.modules.render;

import com.google.common.collect.Lists;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.impl.gui.clickgui.setting.Setting;
import xyz.templecheats.templeclient.impl.gui.font.FontUtils;
import xyz.templecheats.templeclient.impl.modules.Module;
import xyz.templecheats.templeclient.mixins.IMixinRenderManager;

import java.awt.*;
import java.util.List;
import java.util.*;

public class NameTags extends Module {
    private final Setting self = new Setting("Self", this, true);
    private final Setting customFont = new Setting("Custom Font", this, false);
    private final Setting items = new Setting("Items", this, true);
    private final Setting border = new Setting("Border", this, true);
    private final Setting borderRed = new Setting("Border Red", this, 255, 0, 255, true);
    private final Setting borderGreen = new Setting("Border Green", this, 255, 0, 255, true);
    private final Setting borderBlue = new Setting("Border Blue", this, 255, 0, 255, true);

    private final Set<EntityPlayer> players = new TreeSet<>(Comparator.comparing(player -> mc.player.getDistance((EntityPlayer) player)).reversed());

    public NameTags() {
        super("Nametags", Keyboard.KEY_NONE, Category.RENDER);
        TempleClient.settingsManager.rSetting(self);
        TempleClient.settingsManager.rSetting(customFont);
        TempleClient.settingsManager.rSetting(items);
        TempleClient.settingsManager.rSetting(border);
        TempleClient.settingsManager.rSetting(borderRed);
        TempleClient.settingsManager.rSetting(borderGreen);
        TempleClient.settingsManager.rSetting(borderBlue);
    }

    @Override
    public void onUpdate() {
        this.players.clear();
        this.players.addAll(mc.world.playerEntities);
    }

    @SubscribeEvent
    public void onRender(RenderLivingEvent.Specials.Pre<?> event) {
        final EntityLivingBase e = event.getEntity();

        if(e instanceof EntityPlayer && ((this.self.getValBoolean() && mc.gameSettings.thirdPersonView != 0) || !e.equals(mc.player)) && !e.isDead && e.getHealth() > 0 && !e.isInvisible()) {
            event.setCanceled(true);
        }
    }

    @Override
    public void onRenderWorld(float partialTicks) {
        for(EntityPlayer player : this.players) {
            if(((!this.self.getValBoolean() || mc.gameSettings.thirdPersonView == 0) && player.equals(mc.player)) || player.isDead || player.getHealth() <= 0 || player.isInvisible()) {
                continue;
            }

            final double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks - ((IMixinRenderManager) mc.getRenderManager()).getRenderPosX();
            final double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks - ((IMixinRenderManager) mc.getRenderManager()).getRenderPosY();
            final double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks - ((IMixinRenderManager) mc.getRenderManager()).getRenderPosZ();
            this.renderNametag(player, new Vec3d(x, y + player.height / 1.5, z));
        }
    }

    private void renderNametag(EntityLivingBase entity, Vec3d pos) {
        final String name = entity.getDisplayName().getFormattedText();
        final String health = " " + (int) Math.ceil(entity.getHealth() + entity.getAbsorptionAmount());
        String ping = "";
        try {
            ping = " " + Objects.requireNonNull(mc.getConnection()).getPlayerInfo(entity.getUniqueID()).getResponseTime() + "ms";
        } catch(Exception ignored) {
        }

        GL11.glPushMatrix();
        GL11.glTranslated(pos.x, pos.y + 1, pos.z);
        GL11.glRotatef(-mc.getRenderManager().playerViewY, 0, 1, 0);
        GL11.glRotatef((mc.gameSettings.thirdPersonView == 2 ? -1F : 1F) * mc.getRenderManager().playerViewX, 1, 0, 0);

        final float distance = mc.player.getDistance(entity);
        final float scale = (0.027F + distance / 600) * Math.min(3, Math.max(1, distance / 50));
        GL11.glScalef(-scale, -scale, scale);

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        final int totalWidth = this.getStringWidth(name + health + ping) / 2;
        final int fontHeight = this.customFont.getValBoolean() ? FontUtils.normal.getHeight() + 1 : 9;

        if(this.border.getValBoolean()) {
            this.drawRoundedBorderedRect(-totalWidth - 2, -2, totalWidth + 1, fontHeight, 0x80000000, 0xFF000000);
        } else {
            this.drawRect(-totalWidth - 1, -1, totalWidth, fontHeight - 1, 0, 0, 0, 128);
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);

        if(this.customFont.getValBoolean()) {
            FontUtils.normal.drawString(name, -totalWidth, 0, 0xFFFFFF);
            FontUtils.normal.drawString(health, -totalWidth + this.getStringWidth(name), 0, this.getHealthColor(entity));
            FontUtils.normal.drawString(ping, -totalWidth + this.getStringWidth(name + health), 0, this.getPingColor(entity));
        } else {
            mc.fontRenderer.drawString(name, -totalWidth, 0, 0xFFFFFF);
            mc.fontRenderer.drawString(health, -totalWidth + this.getStringWidth(name), 0, this.getHealthColor(entity));
            mc.fontRenderer.drawString(ping, -totalWidth + this.getStringWidth(name + health), 0, this.getPingColor(entity));
        }

        if(entity instanceof EntityPlayer && this.items.getValBoolean()) {
            final EntityPlayer player = (EntityPlayer) entity;
            final List<ItemStack> armor = Lists.reverse(player.inventory.armorInventory);

            boolean hasDurability = false;
            int totalItemsWidth = 0;
            for(ItemStack stack : armor) {
                if(!stack.isEmpty()) {
                    totalItemsWidth += 16;
                    if(stack.getItem().showDurabilityBar(stack)) {
                        hasDurability = true;
                    }
                }
            }
            if(!player.getHeldItemMainhand().isEmpty()) {
                totalItemsWidth += 16;
                if(player.getHeldItemMainhand().getItem().showDurabilityBar(player.getHeldItemMainhand())) {
                    hasDurability = true;
                }
            }
            if(!player.getHeldItemOffhand().isEmpty()) {
                totalItemsWidth += 16;
                if(player.getHeldItemOffhand().getItem().showDurabilityBar(player.getHeldItemOffhand())) {
                    hasDurability = true;
                }
            }

            int xOffset = -totalItemsWidth / 2;
            final int yOffset = hasDurability ? -28 : -15;

            if(!player.getHeldItemMainhand().isEmpty()) {
                this.renderItem(player.getHeldItemMainhand(), xOffset, yOffset);
                xOffset += 16;
            }

            for(ItemStack stack : armor) {
                if(!stack.isEmpty()) {
                    this.renderItem(stack, xOffset, yOffset);
                    xOffset += 16;
                }
            }

            if(!player.getHeldItemOffhand().isEmpty()) {
                this.renderItem(player.getHeldItemOffhand(), xOffset, yOffset);
            }
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopMatrix();
    }

    private void renderItem(ItemStack stack, int x, int y) {
        final int durability = (int) ((stack.getMaxDamage() - stack.getItemDamage()) / (float) stack.getMaxDamage() * 100);
        if(durability >= 0 && stack.getItem().showDurabilityBar(stack)) {
            final String duraString = String.valueOf(durability);
            if(this.customFont.getValBoolean()) {
                FontUtils.normal.drawStringWithShadow(duraString, x, y + (-y - 10), stack.getItem().getRGBDurabilityForDisplay(stack));
            } else {
                mc.fontRenderer.drawStringWithShadow(duraString, x + 3, y + (-y - 10), stack.getItem().getRGBDurabilityForDisplay(stack));
            }
        }

        GL11.glPushMatrix();
        GL11.glDepthMask(true);
        GlStateManager.pushMatrix();
        GlStateManager.clear(256);
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
        GL11.glPopMatrix();
        mc.getRenderItem().zLevel = -100.0F;
        GlStateManager.scale(1, 1, 0.01F);
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, (y / 2) - 12);
        mc.getRenderItem().renderItemOverlays(mc.fontRenderer, stack, x, (y / 2) - 12); //durability
        mc.getRenderItem().zLevel = 0.0F;
        GlStateManager.scale(1, 1, 1);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
    }

    private void drawRect(float left, float top, float right, float bottom, int r, int g, int b, int a) {
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(left, bottom, 0).color(r, g, b, a).endVertex();
        bufferbuilder.pos(right, bottom, 0).color(r, g, b, a).endVertex();
        bufferbuilder.pos(right, top, 0).color(r, g, b, a).endVertex();
        bufferbuilder.pos(left, top, 0).color(r, g, b, a).endVertex();
        tessellator.draw();
    }

    private void drawHorizontalLine(int startX, int endX, int y, int r, int g, int b, int a) {
        this.drawRect(startX, y, endX + 1, y + 1, r, g, b, a);
    }

    private void drawVerticalLine(int x, final int startY, int endY, int r, int g, int b, int a) {
        this.drawRect(x, startY + 1, x + 1, endY, r, g, b, a);
    }

    private void drawRoundedBorderedRect(int left, int top, int right, int bottom, int insideColor, int outsideColor) {
        final int insideR = (insideColor >> 16 & 255);
        final int insideG = (insideColor >> 8 & 255);
        final int insideB = (insideColor & 255);
        final int insideA = (insideColor >> 24 & 255);
        this.drawRect(left + 1, top + 1, right - 1, bottom - 1, insideR, insideG, insideB, insideA);

        final int outsideR = this.borderRed.getValInt();
        final int outsideG = this.borderGreen.getValInt();
        final int outsideB = this.borderBlue.getValInt();
        final int outsideA = (outsideColor >> 24 & 255);
        this.drawHorizontalLine(left, right - 1, top, outsideR, outsideG, outsideB, outsideA);
        this.drawHorizontalLine(left, right - 1, bottom - 1, outsideR, outsideG, outsideB, outsideA);
        this.drawVerticalLine(left, top, bottom - 1, outsideR, outsideG, outsideB, outsideA);
        this.drawVerticalLine(right - 1, top, bottom - 1, outsideR, outsideG, outsideB, outsideA);
    }

    private int getHealthColor(EntityLivingBase entity) {
        final int health = (int) ((entity.getMaxHealth() - (entity.getHealth() + entity.getAbsorptionAmount())) / entity.getMaxHealth() * 36);

        //magic numbers yippee
        switch(MathHelper.clamp(health, 0, 36) / 6) {
            case 0:
                return Color.GREEN.getRGB();
            case 1:
                return 43520;
            case 2:
                return Color.YELLOW.getRGB();
            case 3:
                return 16755200;
            case 4:
                return Color.RED.getRGB();
            case 5:
                return 11141120;
        }

        return 0xFFFFFF;
    }

    private int getPingColor(EntityLivingBase entity) {
        long ping;
        try {
            ping = Objects.requireNonNull(mc.getConnection()).getPlayerInfo(entity.getUniqueID()).getResponseTime();
        } catch(Exception ignored) {
            return 0xFFFFFF;
        }

        if(ping >= 200) {
            return 11141120;
        } else if(ping >= 150) {
            return Color.RED.getRGB();
        } else if(ping >= 100) {
            return 16755200;
        } else if(ping >= 75) {
            return Color.YELLOW.getRGB();
        } else if(ping >= 50) {
            return 43520;
        } else if(ping > 0) {
            return Color.GREEN.getRGB();
        } else {
            return 0xFFFFFF;
        }
    }

    private int getStringWidth(String string) {
        if(this.customFont.getValBoolean()) {
            return (int) Math.ceil(FontUtils.normal.getStringWidth(string));
        } else {
            return mc.fontRenderer.getStringWidth(string);
        }
    }
}
