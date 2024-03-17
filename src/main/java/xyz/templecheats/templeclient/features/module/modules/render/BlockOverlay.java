package xyz.templecheats.templeclient.features.module.modules.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;


public class BlockOverlay extends Module {
    private final IntSetting red = new IntSetting("Red", this, 0, 255, 255);
    private final IntSetting green = new IntSetting("Green", this, 0, 255, 0);
    private final IntSetting blue = new IntSetting("Blue", this, 0, 255, 0);

    private boolean isRightFaceVisible(BlockPos pos) {
        return mc.world.getBlockState(pos.add(1, 0, 0)).getBlock().isAir(mc.world.getBlockState(pos.add(1, 0, 0)), mc.world, pos.add(1, 0, 0));
    }
    private boolean isLeftFaceVisible(BlockPos pos) {
        return mc.world.getBlockState(pos.add(-1, 0, 0)).getBlock().isAir(mc.world.getBlockState(pos.add(-1, 0, 0)), mc.world, pos.add(-1, 0, 0));
    }

    private boolean isFrontFaceVisible(BlockPos pos) {
        return mc.world.getBlockState(pos.add(0, 0, 1)).getBlock().isAir(mc.world.getBlockState(pos.add(0, 0, 1)), mc.world, pos.add(0, 0, 1));
    }

    private boolean isBackFaceVisible(BlockPos pos) {
        return mc.world.getBlockState(pos.add(0, 0, -1)).getBlock().isAir(mc.world.getBlockState(pos.add(0, 0, -1)), mc.world, pos.add(0, 0, -1));
    }

    private boolean isBottomFaceVisible(BlockPos pos) {
        return mc.world.getBlockState(pos.add(0, -1, 0)).getBlock().isAir(mc.world.getBlockState(pos.add(0, -1, 0)), mc.world, pos.add(0, -1, 0));
    }

    private boolean isTopFaceVisible(BlockPos pos) {
        return mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().isAir(mc.world.getBlockState(pos.add(0, 1, 0)), mc.world, pos.add(0, 1, 0));
    }

    public BlockOverlay() {
        super("BlockOverlay","Highlights the block at your cross-hair", 0, Category.Render);

        registerSettings(red, green, blue);
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        RayTraceResult rayTraceResult = mc.objectMouseOver;
        if (rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos blockPos = rayTraceResult.getBlockPos();

            GlStateManager.pushMatrix();
            GlStateManager.pushAttrib();

            try {
                GlStateManager.disableTexture2D();
                GlStateManager.disableDepth();
                GlStateManager.disableLighting();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                GlStateManager.color(red.intValue() / 255.0F, green.intValue() / 255.0F, blue.intValue() / 255.0F, 0.4F);
                GlStateManager.glLineWidth(2.0F);

                AxisAlignedBB box = mc.world.getBlockState(rayTraceResult.getBlockPos()).getSelectedBoundingBox(mc.world, rayTraceResult.getBlockPos()).grow(0.0020000000949949026D).offset(-mc.getRenderManager().viewerPosX, -mc.getRenderManager().viewerPosY, -mc.getRenderManager().viewerPosZ);

                if (isRightFaceVisible(blockPos)) {
                    mc.renderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(box.maxX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ), red.intValue() / 255.0F, green.intValue() / 255.0F, blue.intValue() / 255.0F, 0.4F);
                }

                if (isLeftFaceVisible(blockPos)) {
                    mc.renderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(box.minX, box.minY, box.minZ, box.minX, box.maxY, box.maxZ), red.intValue() / 255.0F, green.intValue() / 255.0F, blue.intValue() / 255.0F, 0.4F);
                }

                if (isFrontFaceVisible(blockPos)) {
                    mc.renderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(box.minX, box.minY, box.maxZ, box.maxX, box.maxY, box.maxZ), red.intValue() / 255.0F, green.intValue() / 255.0F, blue.intValue() / 255.0F, 0.4F);
                }

                if (isBackFaceVisible(blockPos)) {
                    mc.renderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.minZ), red.intValue() / 255.0F, green.intValue() / 255.0F, blue.intValue() / 255.0F, 0.4F);
                }

                if (isBottomFaceVisible(blockPos)) {
                    mc.renderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(box.minX, box.minY, box.minZ, box.maxX, box.minY, box.maxZ), red.intValue() / 255.0F, green.intValue() / 255.0F, blue.intValue() / 255.0F, 0.4F);
                }

                if (isTopFaceVisible(blockPos)) {
                    mc.renderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(box.minX, box.maxY, box.minZ, box.maxX, box.maxY, box.maxZ), red.intValue() / 255.0F, green.intValue() / 255.0F, blue.intValue() / 255.0F, 0.4F);
                }

            } finally {
                GlStateManager.popAttrib();
                GlStateManager.popMatrix();
            }
        }
    }
}