package xyz.templecheats.templeclient.util.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import xyz.templecheats.templeclient.features.module.modules.client.Colors;
import xyz.templecheats.templeclient.mixins.accessor.IMixinRenderManager;
import xyz.templecheats.templeclient.util.Globals;
import xyz.templecheats.templeclient.util.color.ColorUtil;
import xyz.templecheats.templeclient.util.math.Vec2d;
import xyz.templecheats.templeclient.util.render.shader.impl.RoundedTexture;

import java.awt.*;

import static java.lang.Math.atan;
import static org.lwjgl.opengl.GL11.*;
import static xyz.templecheats.templeclient.util.color.ColorUtil.lerpColor;
import static xyz.templecheats.templeclient.util.color.ColorUtil.setAlpha;
import static xyz.templecheats.templeclient.util.render.StencilUtil.*;

public class RenderUtil implements Globals {
    public static final IMixinRenderManager renderManager = (IMixinRenderManager) mc.getRenderManager();
    private static final ResourceLocation blank = new ResourceLocation("textures/blank.png");
    public static final Color transparent = new Color(0, 0, 0, 0);

    /****************************************************************
     *                  Rendering Methods
     ****************************************************************/

    public static void trace(Minecraft mc, Entity e, float partialTicks, int mode, float red, float green, float blue) {
        if (mc.getRenderManager().renderViewEntity != null) {
            glDisable(GL_DEPTH_TEST);
            glDisable(GL_LIGHTING);
            glLineWidth(2F);

            glPushMatrix();
            glDepthMask(false);
            glColor4d(0, 0, 1, 1);

            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glDisable(GL_TEXTURE_2D);
            glBegin(GL_LINES);

            glColor4d(red, green, blue, 1);

            RenderManager renderManager = mc.getRenderManager();

            Vec3d v = new Vec3d(0.0D, 0.0D, 1.0D).rotatePitch(-((float) Math.toRadians((double) mc.player.rotationPitch))).rotateYaw(-((float) Math.toRadians((double) mc.player.rotationYaw)));

            glVertex3d(v.x, mc.player.getEyeHeight() + v.y, v.z);

            double x = e.lastTickPosX + (e.posX - e.lastTickPosX) * partialTicks;
            double y = e.lastTickPosY + (e.posY - e.lastTickPosY) * partialTicks;
            double z = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * partialTicks;

            glVertex3d(x - renderManager.viewerPosX, y - renderManager.viewerPosY + 0.25, z - renderManager.viewerPosZ);

            glEnd();
            glDepthMask(true);
            glEnable(GL_DEPTH_TEST);
            glEnable(GL_TEXTURE_2D);
            glColor4d(1.0, 1.0, 1.0, 1.0);
            glPopMatrix();
        }
    }

    public static void drawHat(float radius, float height, int alpha1, int alpha2) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        GlStateManager.color(-1f, -1f, -1f, -1f);
        buffer.begin(GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(0.0, height, 0.0).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
        for (int i = 0; i <= 360; i++) {
            double percent = (double) i / 360;
            double progress = ((percent > 0.5) ? 1.0 - percent : percent) * 2.0;
            Color color = lerpColor(setAlpha(Colors.INSTANCE.getGradient()[1], alpha1), setAlpha(Colors.INSTANCE.getGradient()[0], alpha2), (float) progress);

            double dir = Math.toRadians(i - 180.0);
            double x = -Math.sin(dir) * radius;
            double z = Math.cos(dir) * radius;
            buffer.pos(x, 0.0, z).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        }
        buffer.pos(0.0, height, 0.0).color(0, 0, 0, 0).endVertex();
        tessellator.draw();
    }

    public static void drawPlayer(EntityPlayer player, float playerScale, float x, float y) {
        GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.shadeModel(7424);
        GlStateManager.enableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.rotate(0.0f, 0.0f, 5.0f, 0.0f);
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 50.0f);
        GlStateManager.scale(-50.0f * playerScale, 50.0f * playerScale, 50.0f * playerScale);
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(135.0f, 0.0f, 1.0f, 0.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate((float) ((-atan(y / 40.0f)) * 20.0f), 1.0f, 0.0f, 0.0f);
        GlStateManager.translate(0.0f, 0.0f, 0.0f);
        RenderManager renderManager = mc.getRenderManager();
        try {
            renderManager.setPlayerViewY(180.0f);
            renderManager.renderEntity(player, 0.0, 0.0, 0.0, 0.0f, 1.0f, false);
        } catch (Exception ignored) {
        }
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.depthFunc(515);
        GlStateManager.resetColor();
        GlStateManager.disableDepth();
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    public static void drawHead(Entity entity, Vec2d pos1, Vec2d pos2, float radius) {
        GlStateManager.pushMatrix();
        initStencilToWrite();
        new RoundedTexture().drawRoundTextured((float) pos1.x, (float) pos1.y, (float) pos2.minus(pos1.x).x, (float) pos2.minus(pos1.y).y, radius, 1.0f);
        readStencilBuffer(1);

        if (entity instanceof AbstractClientPlayer) {
            AbstractClientPlayer player = (AbstractClientPlayer) entity;
            ResourceLocation skin = player.getLocationSkin();
            Color headColor = Color.WHITE;

            glColor3d(headColor.getRed() / 255.0, headColor.getGreen() / 255.0, headColor.getBlue() / 255.0);
            mc.getTextureManager().bindTexture(skin);

            Vec2d uv1 = new Vec2d(8.0F, 8.0F); // head left top
            Vec2d uv2 = new Vec2d(16.0F, 16.0F); // head right bottom

            float textureSize = 64.0F;

            // normalized uv cords
            Vec2d nuv1 = uv1.div(textureSize);
            Vec2d nuv2 = uv2.div(textureSize);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);

            buffer.pos(pos1.x, pos2.y, 0.0).tex(nuv1.x, nuv2.y).endVertex();
            buffer.pos(pos2.x, pos2.y, 0.0).tex(nuv2.x, nuv2.y).endVertex();
            buffer.pos(pos2.x, pos1.y, 0.0).tex(nuv2.x, nuv1.y).endVertex();
            buffer.pos(pos1.x, pos1.y, 0.0).tex(nuv1.x, nuv1.y).endVertex();

            tessellator.draw();
        }
        uninitStencilBuffer();
        GlStateManager.popMatrix();
    }

    public static void FillOnlyLinePlayerESP(Entity entity, AxisAlignedBB box, float r, float g, float b) {
        glPushMatrix();
        glBlendFunc(770, 771);
        glEnable(GL_BLEND);
        glLineWidth(2.0F);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);

        RenderGlobal.drawSelectionBoundingBox(box, r, g, b, 1);

        glEnable(GL_TEXTURE_2D);
        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);
        glDisable(GL_BLEND);
        glPopMatrix();
    }

    public static void blockESP(BlockPos blockPos) {
        glPushMatrix();

        double x =
                blockPos.getX() -
                        Minecraft.getMinecraft().getRenderManager().viewerPosX;
        double y =
                blockPos.getY() -
                        Minecraft.getMinecraft().getRenderManager().viewerPosY;
        double z =
                blockPos.getZ() -
                        Minecraft.getMinecraft().getRenderManager().viewerPosZ;

        glBlendFunc(770, 771);
        glEnable(GL_BLEND);

        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);

        glDepthMask(false);

        RenderGlobal.renderFilledBox(new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0), 1, 1, 0, 0.5F);

        glEnable(GL_TEXTURE_2D);
        glEnable(GL_DEPTH_TEST);

        glDepthMask(true);

        glDisable(GL_BLEND);
        glPopMatrix();
    }

    /****************************************************************
     *                  ClickGUI Methods
     ****************************************************************/

    public static void drawRect(float left, float top, float right, float bottom, int color) {
        if (left < right) {
            float i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
            float j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float) (color >> 24 & 255) / 255.0F;
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(f, f1, f2, f3);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(left, bottom, 0.0).endVertex();
        bufferbuilder.pos(right, bottom, 0.0).endVertex();
        bufferbuilder.pos(right, top, 0.0).endVertex();
        bufferbuilder.pos(left, top, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawGradientRect(float left, float top, float right, float bottom, int startColor,
                                        int endColor) {
        float f = (float) (startColor >> 24 & 255) / 255.0F;
        float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        float f2 = (float) (startColor >> 8 & 255) / 255.F;
        float f3 = (float) (startColor & 255) / 255.0F;
        float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        float f7 = (float) (endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(right, top, 0.0).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos(left, top, 0.0).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos(left, bottom, 0.0).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos(right, bottom, 0.0).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawHorizontalGradientRect(float left, float top, float right, float bottom,
                                                  int startColor, int endColor) {
        float f = (float) (startColor >> 24 & 255) / 255.0F;
        float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        float f3 = (float) (startColor & 255) / 255.0F;
        float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        float f7 = (float) (endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(left, top, 0.0).color(f1, f2, f3, f).endVertex(); // Top-left
        bufferbuilder.pos(left, bottom, 0.0).color(f1, f2, f3, f).endVertex(); // Bottom-left
        bufferbuilder.pos(right, bottom, 0.0).color(f5, f6, f7, f4).endVertex(); // Bottom-right
        bufferbuilder.pos(right, top, 0.0).color(f5, f6, f7, f4).endVertex(); // Top-right
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawOutBorderedRect(int left, int top, int right, int bottom, int borderWidth, int color) {
        RenderUtil.drawRect(left - borderWidth, top - borderWidth, right + borderWidth, top, color);
        RenderUtil.drawRect(left - borderWidth, bottom, right + borderWidth, bottom + borderWidth, color);
        RenderUtil.drawRect(left - borderWidth, top - borderWidth, left, bottom + borderWidth, color);
        RenderUtil.drawRect(right, top - borderWidth, right + borderWidth, bottom + borderWidth, color);
    }

    public static void drawOutlineRect(float left, float top, float right, float bottom, int color) {
        float f = (float)(color >> 24 & 255) / 255.0F;
        float f1 = (float)(color >> 16 & 255) / 255.0F;
        float f2 = (float)(color >> 8 & 255) / 255.0F;
        float f3 = (float)(color & 255) / 255.0F;
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(f1, f2, f3, f);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.glLineWidth(3.0F);
        bufferbuilder.begin(2, DefaultVertexFormats.POSITION);
        bufferbuilder.pos((double)left, (double)bottom, 0.0D).endVertex();
        bufferbuilder.pos((double)right, (double)bottom, 0.0D).endVertex();
        bufferbuilder.pos((double)right, (double)top, 0.0D).endVertex();
        bufferbuilder.pos((double)left, (double)top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    /****************************************************************
     *                  Shader Methods
     ****************************************************************/

    public static void outlineShader(final BlockPos pos) {
        if (pos != null) {
            final AxisAlignedBB bb = new AxisAlignedBB(pos);
            outlineShader(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
        }
    }

    public static void outlineShader(final double minX, final double minY, final double minZ, final double maxX,
                                     final double maxY, final double maxZ) {
        final AxisAlignedBB bb = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ).offset(RenderUtil.renderOffset());
        bindBlank();
        glPushMatrix();
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glLineWidth(1.5f);
        glColor(Colors.INSTANCE.getColor());
        glBegin(GL_LINE_STRIP);
        glVertex3d(bb.minX, bb.minY, bb.minZ);
        glVertex3d(bb.minX, bb.minY, bb.maxZ);
        glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        glVertex3d(bb.maxX, bb.minY, bb.minZ);
        glVertex3d(bb.minX, bb.minY, bb.minZ);
        glVertex3d(bb.minX, bb.maxY, bb.minZ);
        glVertex3d(bb.minX, bb.maxY, bb.maxZ);
        glVertex3d(bb.minX, bb.minY, bb.maxZ);
        glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        glVertex3d(bb.minX, bb.maxY, bb.maxZ);
        glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        glVertex3d(bb.maxX, bb.minY, bb.minZ);
        glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        glVertex3d(bb.minX, bb.maxY, bb.minZ);
        glEnd();
        glColor(Color.WHITE);
        glLineWidth(1.0f);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);
        glPopMatrix();
    }

    public static void outlineFaceShader(final BlockPos pos, EnumFacing face, Color color) {
        if (pos != null) {
            final AxisAlignedBB bb = new AxisAlignedBB(pos).offset(RenderUtil.renderOffset());
            bindBlank();
            glPushMatrix();
            glEnable(GL_BLEND);
            glDisable(GL_TEXTURE_2D);
            glDisable(GL_DEPTH_TEST);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glColor(color);

            glBegin(GL_LINES);
            switch (face) {
                case DOWN:
                case UP:
                    double y = (face == EnumFacing.DOWN) ? bb.minY : bb.maxY;
                    glVertex3d(bb.minX, y, bb.minZ);
                    glVertex3d(bb.maxX, y, bb.minZ);
                    glVertex3d(bb.maxX, y, bb.minZ);
                    glVertex3d(bb.maxX, y, bb.maxZ);
                    glVertex3d(bb.maxX, y, bb.maxZ);
                    glVertex3d(bb.minX, y, bb.maxZ);
                    glVertex3d(bb.minX, y, bb.maxZ);
                    glVertex3d(bb.minX, y, bb.minZ);
                    break;
                case NORTH:
                case SOUTH:
                    double z = (face == EnumFacing.NORTH) ? bb.minZ : bb.maxZ;
                    glVertex3d(bb.minX, bb.minY, z);
                    glVertex3d(bb.maxX, bb.minY, z);
                    glVertex3d(bb.maxX, bb.minY, z);
                    glVertex3d(bb.maxX, bb.maxY, z);
                    glVertex3d(bb.maxX, bb.maxY, z);
                    glVertex3d(bb.minX, bb.maxY, z);
                    glVertex3d(bb.minX, bb.maxY, z);
                    glVertex3d(bb.minX, bb.minY, z);
                    break;
                case WEST:
                case EAST:
                    double x = (face == EnumFacing.WEST) ? bb.minX : bb.maxX;
                    glVertex3d(x, bb.minY, bb.minZ);
                    glVertex3d(x, bb.maxY, bb.minZ);
                    glVertex3d(x, bb.maxY, bb.minZ);
                    glVertex3d(x, bb.maxY, bb.maxZ);
                    glVertex3d(x, bb.maxY, bb.maxZ);
                    glVertex3d(x, bb.minY, bb.maxZ);
                    glVertex3d(x, bb.minY, bb.maxZ);
                    glVertex3d(x, bb.minY, bb.minZ);
                    break;
            }
            glEnd();

            glColor(Color.WHITE);
            glEnable(GL_TEXTURE_2D);
            glEnable(GL_DEPTH_TEST);
            glDisable(GL_BLEND);
            glPopMatrix();
        }
    }

    public static void boxShader(final BlockPos pos) {
        if (pos != null) {
            final AxisAlignedBB bb = new AxisAlignedBB(pos);
            boxShader(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
        }
    }

    public static void boxShader(final double minX, final double minY, final double minZ, final double maxX,
                                 final double maxY, final double maxZ) {
        final AxisAlignedBB bb = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ).offset(RenderUtil.renderOffset());
        bindBlank();
        glPushMatrix();
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glColor(Colors.INSTANCE.getColor());
        glBegin(GL_TRIANGLE_STRIP);
        glVertex3d(bb.minX, bb.minY, bb.minZ);
        glVertex3d(bb.minX, bb.minY, bb.minZ);
        glVertex3d(bb.minX, bb.minY, bb.minZ);
        glVertex3d(bb.minX, bb.minY, bb.maxZ);
        glVertex3d(bb.minX, bb.maxY, bb.minZ);
        glVertex3d(bb.minX, bb.maxY, bb.maxZ);
        glVertex3d(bb.minX, bb.maxY, bb.maxZ);
        glVertex3d(bb.minX, bb.minY, bb.maxZ);
        glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        glVertex3d(bb.maxX, bb.minY, bb.minZ);
        glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        glVertex3d(bb.maxX, bb.minY, bb.minZ);
        glVertex3d(bb.minX, bb.maxY, bb.minZ);
        glVertex3d(bb.minX, bb.minY, bb.minZ);
        glVertex3d(bb.minX, bb.minY, bb.minZ);
        glVertex3d(bb.maxX, bb.minY, bb.minZ);
        glVertex3d(bb.minX, bb.minY, bb.maxZ);
        glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        glVertex3d(bb.maxX, bb.minY, bb.maxZ);
        glVertex3d(bb.minX, bb.maxY, bb.minZ);
        glVertex3d(bb.minX, bb.maxY, bb.minZ);
        glVertex3d(bb.minX, bb.maxY, bb.maxZ);
        glVertex3d(bb.maxX, bb.maxY, bb.minZ);
        glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
        glVertex3d(bb.maxX, bb.maxY, bb.maxZ);

        glEnd();
        glColor(Color.WHITE);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);
        glPopMatrix();
    }

    public static void boxShader(final double minX, final double minY, final double minZ, final double maxX,
                                 final double maxY, final double maxZ, final Color color) {
        final AxisAlignedBB bb = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ).offset(RenderUtil.renderOffset());

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableCull();
        GlStateManager.shadeModel(GL_SMOOTH);

        final BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        //bottom
        bufferBuilder.pos(bb.minX, bb.minY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(bb.maxX, bb.minY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(bb.minX, bb.minY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();

        //top
        bufferBuilder.pos(bb.minX, bb.maxY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        bufferBuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        bufferBuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        bufferBuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();

        //front
        bufferBuilder.pos(bb.minX, bb.minY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(bb.minX, bb.maxY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        bufferBuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        bufferBuilder.pos(bb.maxX, bb.minY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();

        //back
        bufferBuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        bufferBuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        bufferBuilder.pos(bb.minX, bb.minY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();

        //left
        bufferBuilder.pos(bb.minX, bb.minY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(bb.minX, bb.minY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(bb.minX, bb.maxY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        bufferBuilder.pos(bb.minX, bb.maxY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();

        //right
        bufferBuilder.pos(bb.maxX, bb.minY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        bufferBuilder.pos(bb.maxX, bb.maxY, bb.minZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        bufferBuilder.pos(bb.maxX, bb.maxY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        bufferBuilder.pos(bb.maxX, bb.minY, bb.maxZ).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();

        Tessellator.getInstance().draw();

        GlStateManager.shadeModel(GL_FLAT);
        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void renderGradientLine(final double minX, final double minY, final double minZ,
                                          final double maxX, final double maxY, final double maxZ, final Color color) {
        final AxisAlignedBB bb = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ).offset(RenderUtil.renderOffset());

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        GlStateManager.disableCull();
        GlStateManager.shadeModel(GL_SMOOTH);
        GlStateManager.glLineWidth(1.0F);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        bufferBuilder.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        addVertexWithColor(bufferBuilder, bb.minX, bb.minY, bb.minZ, color);
        addVertexWithColor(bufferBuilder, bb.maxX, bb.minY, bb.minZ, color);
        addVertexWithColor(bufferBuilder, bb.maxX, bb.minY, bb.maxZ, color);
        addVertexWithColor(bufferBuilder, bb.minX, bb.minY, bb.maxZ, color);
        addVertexWithColor(bufferBuilder, bb.minX, bb.minY, bb.minZ, color);
        tessellator.draw();

        bufferBuilder.begin(GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        addGradientLine(bufferBuilder, bb.minX, bb.minY, bb.minZ, bb.minX, bb.maxY, bb.minZ, color);
        addGradientLine(bufferBuilder, bb.maxX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.minZ, color);
        addGradientLine(bufferBuilder, bb.maxX, bb.minY, bb.maxZ, bb.maxX, bb.maxY, bb.maxZ, color);
        addGradientLine(bufferBuilder, bb.minX, bb.minY, bb.maxZ, bb.minX, bb.maxY, bb.maxZ, color);
        tessellator.draw();

        GlStateManager.shadeModel(GL_FLAT);
        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawFadeGradientCircleOutline(float radius, float size, int alpha) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        GlStateManager.color(-1f, -1f, -1f, -1f);
        buffer.begin(GL_QUAD_STRIP, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i <= 360; i++) {
            double percent = (double) i / 360;
            double progress = ((percent > 0.5) ? 1.0 - percent : percent) * 2.0;
            Color color = ColorUtil.setAlpha(lerpColor(Colors.INSTANCE.getGradient()[1], Colors.INSTANCE.getGradient()[0], (float) progress), alpha);

            double dir = Math.toRadians(i - 180.0);

            double x = -Math.sin(dir) * radius;
            double z = Math.cos(dir) * radius;

            double x0 = -Math.sin(dir) * (radius + size);
            double z0 = Math.cos(dir) * (radius + size);

            buffer.pos(x, 0.0, z).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
            buffer.pos(x0, 0.0, z0).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
        }
        tessellator.draw();
    }

    public static void drawGradientCircleOutline(float radius, float outlineWidth, int alpha) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        GlStateManager.glLineWidth(outlineWidth);
        GlStateManager.color(-1f, -1f, -1f, -1f);
        buffer.begin(GL_LINE_LOOP, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i <= 360; i++) {
            double percent = (double) i / 360;
            double progress = ((percent > 0.5) ? 1.0 - percent : percent) * 2.0;
            Color color = ColorUtil.setAlpha(lerpColor(Colors.INSTANCE.getGradient()[1], Colors.INSTANCE.getGradient()[0], (float) progress), alpha);

            double dir = Math.toRadians(i - 180.0);
            double x = -Math.sin(dir) * radius;
            double z = Math.cos(dir) * radius;
            buffer.pos(x, 0.0, z).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        }
        tessellator.draw();
    }

    private static void addVertexWithColor(BufferBuilder bufferBuilder, double x, double y, double z, Color color) {
        bufferBuilder.pos(x, y, z).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
    }

    private static void addGradientLine(BufferBuilder bufferBuilder, double startX, double startY, double startZ,
                                        double endX, double endY, double endZ, Color color) {
        Color transparent = new Color(color.getRed(), color.getGreen(), color.getBlue(), 0); // Assuming you want to fade to transparent
        addVertexWithColor(bufferBuilder, startX, startY, startZ, color);
        addVertexWithColor(bufferBuilder, endX, endY, endZ, transparent);
    }

    public static void boxFaceShader(final BlockPos pos, EnumFacing face, Color color) {
        if (pos != null) {
            final AxisAlignedBB bb = new AxisAlignedBB(pos).offset(RenderUtil.renderOffset());
            bindBlank();
            glPushMatrix();
            glEnable(GL_BLEND);
            glDisable(GL_TEXTURE_2D);
            glDisable(GL_DEPTH_TEST);
            glDisable(GL_CULL_FACE);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glColor(Colors.INSTANCE.getColor());

            glBegin(GL_QUADS);
            switch (face) {
                case DOWN:
                case UP:
                    double y = (face == EnumFacing.DOWN) ? bb.minY : bb.maxY;
                    glVertex3d(bb.minX, y, bb.minZ);
                    glVertex3d(bb.minX, y, bb.maxZ);
                    glVertex3d(bb.maxX, y, bb.maxZ);
                    glVertex3d(bb.maxX, y, bb.minZ);
                    break;
                case NORTH:
                case SOUTH:
                    double z = (face == EnumFacing.NORTH) ? bb.minZ : bb.maxZ;
                    glVertex3d(bb.minX, bb.minY, z);
                    glVertex3d(bb.minX, bb.maxY, z);
                    glVertex3d(bb.maxX, bb.maxY, z);
                    glVertex3d(bb.maxX, bb.minY, z);
                    break;
                case WEST:
                case EAST:
                    double x = (face == EnumFacing.WEST) ? bb.minX : bb.maxX;
                    glVertex3d(x, bb.minY, bb.minZ);
                    glVertex3d(x, bb.minY, bb.maxZ);
                    glVertex3d(x, bb.maxY, bb.maxZ);
                    glVertex3d(x, bb.maxY, bb.minZ);
                    break;
            }
            glEnd();

            glColor(Color.WHITE);
            glEnable(GL_TEXTURE_2D);
            glEnable(GL_DEPTH_TEST);
            glEnable(GL_CULL_FACE);
            glDisable(GL_BLEND); //boom
            glPopMatrix();
        }
    }

    /****************************************************************
     *                  Utility Methods
     ****************************************************************/

    public static Vec3d renderOffset() {
        return new Vec3d(-mc.getRenderManager().viewerPosX, -mc.getRenderManager().viewerPosY, -mc.getRenderManager().viewerPosZ);
    }

    public static void renderTexture(ResourceLocation resourceLocation, Color color1, Color color2) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        mc.getTextureManager().bindTexture(resourceLocation);
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

        buffer.pos(0.0F, 1.5F, 0.0F).tex(0F, 1F).color(color1.getRed(), color1.getGreen(), color1.getBlue(), color1.getAlpha()).endVertex();
        buffer.pos(1.5F, 1.5F, 0.0F).tex(1F, 1F).color(color2.getRed(), color2.getGreen(), color2.getBlue(), color2.getAlpha()).endVertex();
        buffer.pos(1.5F, 0.0F, 0.0F).tex(1F, 0F).color(color1.getRed(), color1.getGreen(), color1.getBlue(), color1.getAlpha()).endVertex();
        buffer.pos(0.0F, 0.0F, 0.0F).tex(0F, 0F).color(color2.getRed(), color2.getGreen(), color2.getBlue(), color2.getAlpha()).endVertex();

        tessellator.draw();
    }

    public static void bind(final ResourceLocation resourceLocation) {
        mc.getTextureManager().bindTexture(resourceLocation);
    }

    public static void bindBlank() {
        bind(blank);
    }

    public static void glColor(final Color color) {
        glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
    }

    public static Framebuffer createFrameBuffer(final Framebuffer framebuffer) {
        if (framebuffer == null || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(mc.displayWidth, mc.displayHeight, true);
        }
        return framebuffer;
    }

    public static double interpolateLastTickPos(double pos, double lastPos) {
        return lastPos + (pos - lastPos) * mc.getRenderPartialTicks();
    }

    public static Vec3d interpolateEntity(Entity entity) {
        double x;
        double y;
        double z;
        x = interpolateLastTickPos(entity.posX, entity.lastTickPosX) - mc.getRenderManager().viewerPosX;
        y = interpolateLastTickPos(entity.posY, entity.lastTickPosY) - mc.getRenderManager().viewerPosY;
        z = interpolateLastTickPos(entity.posZ, entity.lastTickPosZ) - mc.getRenderManager().viewerPosZ;
        return new Vec3d(x, y, z);
    }
}
