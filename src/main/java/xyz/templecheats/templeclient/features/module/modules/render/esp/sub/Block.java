package xyz.templecheats.templeclient.features.module.modules.render.esp.sub;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.util.render.shader.impl.GradientShader;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;

import java.awt.*;

public class Block extends Module {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    private final BooleanSetting outline = new BooleanSetting("Outline", this, true);
    private final BooleanSetting fill = new BooleanSetting("Fill", this, true);
    private final BooleanSetting hitSideOnly = new BooleanSetting("FaceOnly", this, true);
    private final DoubleSetting lineWidth = new DoubleSetting("Line Width", this, 0.1, 5.0, 1.0);
    private final DoubleSetting opacity = new DoubleSetting("Opacity", this, 0.0, 1.0, 0.5);

    /****************************************************************
     *                      Variables
     ****************************************************************/
    public static boolean rendering;

    public Block() {
        super("Block", "Highlights the block at your crosshair", Keyboard.KEY_NONE, Category.Render, true);
        registerSettings(outline, fill, hitSideOnly, lineWidth, opacity);
    }

    @SubscribeEvent
    public void onRender3D(RenderWorldLastEvent event) {
        RayTraceResult ray = Block.mc.objectMouseOver;
        if (ray == null || ray.typeOfHit != RayTraceResult.Type.BLOCK)
            return;

        BlockPos pos = ray.getBlockPos();
        EnumFacing sideHit = ray.sideHit;
        rendering = false;
        GradientShader.setup((float) opacity.doubleValue());
        rendering = true;

        if (fill.booleanValue()) {
            if (hitSideOnly.booleanValue()) {
                RenderUtil.boxFaceShader(pos, sideHit, Color.WHITE);
            } else {
                RenderUtil.boxShader(pos);
            }
        }
        if (outline.booleanValue()) {
            GL11.glLineWidth(lineWidth.floatValue());
            if (hitSideOnly.booleanValue()) {
                RenderUtil.outlineFaceShader(pos, sideHit, Color.WHITE);
            } else {
                RenderUtil.outlineShader(pos);
            }
        }

        GradientShader.finish();
        rendering = false;
    }

    @SubscribeEvent
    public void onHighlightBlock(DrawBlockHighlightEvent event) {
        if (outline.booleanValue())
            event.setCanceled(true);
    }
}