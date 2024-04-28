package xyz.templecheats.templeclient.features.module.modules.render;

import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.TempleClient;
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
import xyz.templecheats.templeclient.event.events.render.Render3DPrePreEvent;
import xyz.templecheats.templeclient.features.gui.font.CFont;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.client.Colors;
import xyz.templecheats.templeclient.features.module.modules.client.FontSettings;
import xyz.templecheats.templeclient.mixins.accessor.IMixinRenderManager;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;

import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class NameTags extends Module {
    /*
     * Settings
     */
    private final BooleanSetting border = new BooleanSetting("Border", this, true);
    private final BooleanSetting customFont = new BooleanSetting("Custom Font", this, false);
    private final BooleanSetting items = new BooleanSetting("Items", this, true);
    private final Set<EntityPlayer> players = new TreeSet<>(Comparator.comparing(player -> mc.player.getDistance((EntityPlayer) player)).reversed());
    private final CFont font = FontSettings.getFont(18);
    public NameTags() {
        super("Nametags", "Renders nametags above entities", Keyboard.KEY_NONE, Category.Render);
        registerSettings(border, customFont, items);
    }

    @Override
    public void onUpdate() {
        this.players.clear();
        this.players.addAll(mc.world.playerEntities);
    }

    @SubscribeEvent
    public void onRender(RenderLivingEvent.Specials.Pre < ? > event) {
        final EntityLivingBase e = event.getEntity();

        if (e instanceof EntityPlayer && (mc.gameSettings.thirdPersonView != 0 || !e.equals(mc.player)) && !e.isDead && e.getHealth() > 0 && !e.isInvisible()) {
            event.setCanceled(true);
        }
    }

    @Override
    public void onRenderWorld(float partialTicks) {
        for (EntityPlayer player: this.players) {
            if (player.equals(mc.player) || player.isDead || player.getHealth() <= 0 || player.isInvisible()) {
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
        } catch (Exception ignored) {}

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
        final int fontHeight = (int) Math.ceil(font.getFontHeight());

        if (this.border.booleanValue()) {
            this.drawRoundedBorderedRect(-totalWidth - 2, -2, totalWidth + 1, fontHeight, 0x80000000, entity);
        } else {
            this.drawRect(-totalWidth - 1, -1, totalWidth, fontHeight - 1, 0, 0, 0, 128);
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);

        if (customFont.booleanValue()) {
            font.drawString(name, -totalWidth, 0, 0xFFFFFF, true);
            font.drawString(health, -totalWidth + this.getStringWidth(name), 0, this.getHealthColor(entity), true);
            font.drawString(ping, -totalWidth + this.getStringWidth(name + health), 0, this.getPingColor(entity), true);
        } else {
            mc.fontRenderer.drawString(name, -totalWidth, 0, 0xFFFFFF, true);
            mc.fontRenderer.drawString(health, -totalWidth + this.getStringWidth(name), 0, this.getHealthColor(entity), true);
            mc.fontRenderer.drawString(ping, -totalWidth + this.getStringWidth(name + health), 0, this.getPingColor(entity), true);
        }
        if (entity instanceof EntityPlayer && this.items.booleanValue()) {
            final EntityPlayer player = (EntityPlayer) entity;
            final List < ItemStack > armor = Lists.reverse(player.inventory.armorInventory);

            boolean hasDurability = false;
            int totalItemsWidth = 0;
            for (ItemStack stack: armor) {
                if (!stack.isEmpty()) {
                    totalItemsWidth += 16;
                    if (stack.getItem().showDurabilityBar(stack)) {
                        hasDurability = true;
                    }
                }
            }
            if (!player.getHeldItemMainhand().isEmpty()) {
                totalItemsWidth += 16;
                if (player.getHeldItemMainhand().getItem().showDurabilityBar(player.getHeldItemMainhand())) {
                    hasDurability = true;
                }
            }
            if (!player.getHeldItemOffhand().isEmpty()) {
                totalItemsWidth += 16;
                if (player.getHeldItemOffhand().getItem().showDurabilityBar(player.getHeldItemOffhand())) {
                    hasDurability = true;
                }
            }

            int xOffset = -totalItemsWidth / 2;
            final int yOffset = hasDurability ? -28 : -15;

            if (!player.getHeldItemMainhand().isEmpty()) {
                this.renderItem(player.getHeldItemMainhand(), xOffset, yOffset);
                xOffset += 16;
            }

            for (ItemStack stack: armor) {
                if (!stack.isEmpty()) {
                    this.renderItem(stack, xOffset, yOffset);
                    xOffset += 16;
                }
            }

            if (!player.getHeldItemOffhand().isEmpty()) {
                this.renderItem(player.getHeldItemOffhand(), xOffset, yOffset);
            }
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glPopMatrix();
    }

    private void renderItem(ItemStack stack, int x, int y) {
        final int durability = (int)((stack.getMaxDamage() - stack.getItemDamage()) / (float) stack.getMaxDamage() * 100);
        if (durability >= 0 && stack.getItem().showDurabilityBar(stack)) {
            final String duraString = String.valueOf(durability);
            if (!customFont.booleanValue()) {
                mc.fontRenderer.drawString(duraString, x, y + (-y - 10), stack.getItem().getRGBDurabilityForDisplay(stack), true);
            } else
                font.drawString(duraString, x, y + (-y - 10), stack.getItem().getRGBDurabilityForDisplay(stack), true);
        }
        GlStateManager.pushMatrix();
        GL11.glDepthMask(true);
        GlStateManager.clear(256);
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
        mc.getRenderItem().zLevel = -100.0F;
        GlStateManager.scale(1, 1, 0.01F);
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, (y / 2) - 12);
        mc.getRenderItem().renderItemOverlays(mc.fontRenderer, stack, x, (y / 2) - 12);
        mc.getRenderItem().zLevel = 0.0F;
        GlStateManager.scale(1, 1, 1);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.popMatrix();
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

    private void drawRoundedBorderedRect(int left, int top, int right, int bottom, int insideColor, EntityLivingBase entity) {
        boolean isFriend = TempleClient.friendManager.isFriend(entity.getName());
        final int insideR = (insideColor >> 16 & 255);
        final int insideG = (insideColor >> 8 & 255);
        final int insideB = (insideColor & 255);
        final int insideA = (insideColor >> 24 & 255);
        this.drawRect(left + 1, top + 1, right - 1, bottom - 1, insideR, insideG, insideB, insideA);

        float outsideR, outsideG, outsideB;
        if (isFriend) {
            Color friendColor = Colors.INSTANCE.friendColor.getColor();
            outsideR = friendColor.getRed() / 255.0f;
            outsideG = friendColor.getGreen() / 255.0f;
            outsideB = friendColor.getBlue() / 255.0f;
        } else {
            Color outsideColor = Colors.INSTANCE.staticColor.getColor();
            outsideR = outsideColor.getRed() / 255.0f;
            outsideG = outsideColor.getGreen() / 255.0f;
            outsideB = outsideColor.getBlue() / 255.0f;
        }
        final int outsideA = insideA;
        this.drawHorizontalLine(left, right - 1, top, (int)(outsideR * 255), (int)(outsideG * 255), (int)(outsideB * 255), outsideA);
        this.drawHorizontalLine(left, right - 1, bottom - 1, (int)(outsideR * 255), (int)(outsideG * 255), (int)(outsideB * 255), outsideA);
        this.drawVerticalLine(left, top, bottom - 1, (int)(outsideR * 255), (int)(outsideG * 255), (int)(outsideB * 255), outsideA);
        this.drawVerticalLine(right - 1, top, bottom - 1, (int)(outsideR * 255), (int)(outsideG * 255), (int)(outsideB * 255), outsideA);
    }
    private int getHealthColor(EntityLivingBase entity) {
        final int health = (int)((entity.getMaxHealth() - (entity.getHealth() + entity.getAbsorptionAmount())) / entity.getMaxHealth() * 36);

        switch (MathHelper.clamp(health, 0, 36) / 6) {
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
        } catch (Exception ignored) {
            return 0xFFFFFF;
        }

        if (ping >= 200) {
            return 11141120;
        } else if (ping >= 150) {
            return Color.RED.getRGB();
        } else if (ping >= 100) {
            return 16755200;
        } else if (ping >= 75) {
            return Color.YELLOW.getRGB();
        } else if (ping >= 50) {
            return 43520;
        } else if (ping > 0) {
            return Color.GREEN.getRGB();
        } else {
            return 0xFFFFFF;
        }
    }

    private int getStringWidth(String string) {
        return (int) Math.ceil(font.getStringWidth(string));
    }
}