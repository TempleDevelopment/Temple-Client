package xyz.templecheats.templeclient.features.module.modules.render.particle.impl;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import xyz.templecheats.templeclient.util.render.enums.TextureModifiers;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static xyz.templecheats.templeclient.util.math.MathUtil.*;
import static xyz.templecheats.templeclient.util.render.RenderUtil.renderTexture;

public class ParticleTickHandler {
    public Vec3d pos, prevPos;
    public double motionX, motionY, motionZ;
    public double gravityAmount, inertiaAmount;
    public double size;
    public int tickLiving = (int) random(100, 300);
    public int maxTickLiving = tickLiving;

    public ParticleTickHandler(Vec3d posIn, double motionX, double motionY, double motionZ, double gravityAmount, double inertiaAmount) {
        this.pos = posIn;
        this.prevPos = posIn;
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
        this.gravityAmount = gravityAmount;
        this.inertiaAmount = inertiaAmount;
    }

    public boolean tick() {
        tickLiving = 0;
        maxTickLiving = 100;
        tickLiving -= mc.player.getDistanceSq(pos.x, pos.y, pos.z) > 4096 ? 8 : 1;

        if (tickLiving < 0) {
            return true;
        } else {
            prevPos = pos;
            pos = pos.add(motionX, motionY, motionZ);

            motionY -= gravityAmount * 0.01;

            motionX *= 0.9 + (inertiaAmount / 10.0);
            motionY *= 0.9 + (inertiaAmount / 10.0);
            motionZ *= 0.9 + (inertiaAmount / 10.0);

            if ( blockPos(pos.x, pos.y, pos.z) ) {
                motionY = -motionY;
            } else if ( collisionCheck(pos.x, pos.y, pos.z, size / 10f, MathHelper.sqrt(motionX * motionY + motionZ * motionZ) * 1) ) {
                motionX = -motionX + motionZ;
                motionZ = -motionZ + motionX;
            }

            return false;
        }
    }

    public boolean collisionCheck(double x, double y, double z, double size, double sp) {
        return blockPos(x, y, z) ||
                blockPos(x, y - size, z) ||
                blockPos(x, y + size, z) ||

                blockPos(x - sp, y, z - sp) ||
                blockPos(x + sp, y, z + sp) ||
                blockPos(x + sp, y, z - sp) ||
                blockPos(x - sp, y, z + sp) ||
                blockPos(x + sp, y, z) ||
                blockPos(x - sp, y, z) ||
                blockPos(x, y, z + sp) ||
                blockPos(x, y, z - sp) ||

                blockPos(x - sp, y - size, z - sp) ||
                blockPos(x + sp, y - size, z + sp) ||
                blockPos(x + sp, y - size, z - sp) ||
                blockPos(x - sp, y - size, z + sp) ||
                blockPos(x + sp, y - size, z) ||
                blockPos(x - sp, y - size, z) ||
                blockPos(x, y - size, z + sp) ||
                blockPos(x, y - size, z - sp) ||

                blockPos(x - sp, y + size, z - sp) ||
                blockPos(x + sp, y + size, z + sp) ||
                blockPos(x + sp, y + size, z - sp) ||
                blockPos(x - sp, y + size, z + sp) ||
                blockPos(x + sp, y + size, z) ||
                blockPos(x - sp, y + size, z) ||
                blockPos(x, y + size, z + sp) ||
                blockPos(x, y + size, z - sp);
    }

    public boolean blockPos(double x, double y, double z) {
        if (mc.world != null) {
            Set<Block> excludedBlocks = new HashSet<>(Arrays.asList(
                    Blocks.AIR, Blocks.WATER, Blocks.LAVA, Blocks.BED, Blocks.CAKE, Blocks.TALLGRASS,
                    Blocks.FLOWER_POT, Blocks.RED_FLOWER, Blocks.YELLOW_FLOWER, Blocks.SAPLING, Blocks.VINE,
                    Blocks.ACACIA_FENCE, Blocks.ACACIA_FENCE_GATE, Blocks.BIRCH_FENCE, Blocks.BIRCH_FENCE_GATE,
                    Blocks.DARK_OAK_FENCE, Blocks.DARK_OAK_FENCE_GATE, Blocks.JUNGLE_FENCE, Blocks.JUNGLE_FENCE_GATE,
                    Blocks.NETHER_BRICK_FENCE, Blocks.OAK_FENCE, Blocks.OAK_FENCE_GATE, Blocks.SPRUCE_FENCE,
                    Blocks.SPRUCE_FENCE_GATE, Blocks.ENCHANTING_TABLE, Blocks.END_PORTAL_FRAME, Blocks.DOUBLE_PLANT,
                    Blocks.STANDING_SIGN, Blocks.WALL_SIGN, Blocks.SKULL, Blocks.DAYLIGHT_DETECTOR,
                    Blocks.DAYLIGHT_DETECTOR_INVERTED, Blocks.STONE_SLAB, Blocks.WOODEN_SLAB, Blocks.CARPET,
                    Blocks.DEADBUSH, Blocks.VINE, Blocks.REDSTONE_WIRE, Blocks.REEDS, Blocks.SNOW_LAYER
            ));
            Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
            return !excludedBlocks.contains(block);
        } else {
            return false;
        }
    }

    public void draw(double scale, Color color1, Color color2, TextureModifiers textures) {
        setUp();

        double x = lerp((float) prevPos.x, (float) pos.x, mc.getRenderPartialTicks()) - mc.getRenderManager().viewerPosX;
        double y = lerp((float) prevPos.y, (float) pos.y, mc.getRenderPartialTicks()) - mc.getRenderManager().viewerPosY;
        double z = lerp((float) prevPos.z, (float) pos.z, mc.getRenderPartialTicks()) - mc.getRenderManager().viewerPosZ;

        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0, 1, 0);
        GlStateManager.rotate((mc.gameSettings.thirdPersonView == 2 ? -1F : 1F) * mc.getRenderManager().playerViewX, 1, 0, 0);

        GlStateManager.scale(-scale / 10f, -scale / 10f, scale / 10f);
        GlStateManager.translate(-0.75, -0.75, -0.01);
        renderTexture(textures.getResourceLocation(), color1, color2);
        restore();
    }

    private void setUp() {
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.shadeModel(7425);
        GlStateManager.disableCull();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
    }

    private void restore() {
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
    }
}
