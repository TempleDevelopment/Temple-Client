package xyz.templecheats.templeclient.features.module.modules.render.esp.sub;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import org.lwjgl.input.Keyboard;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.event.events.render.Render3DEvent;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.mixins.accessor.IPlayerControllerMP;
import xyz.templecheats.templeclient.mixins.accessor.IRenderGlobal;
import xyz.templecheats.templeclient.util.render.shader.impl.GradientShader;
import xyz.templecheats.templeclient.util.render.enums.ProgressBoxModifiers;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;

public class Break extends Module {

    private final EnumSetting<ProgressBoxModifiers> renderMode = new EnumSetting<>("Render Mode", this, ProgressBoxModifiers.Grow);
    private final IntSetting displayRange = new IntSetting("Display Range", this, 5, 250, 20);
    private final DoubleSetting opacity = new DoubleSetting("Opacity", this, 0, 255, 200);

    private float normalizedOpacity = opacity.floatValue() / opacity.floatValue(); //we do this bcs GradientShader takes values from 1 to 0 and the setting above is 0 to 255
    private BlockPos lastMinePos;
    private BlockPos posPlayerLookingAt;

    public Break() {
        super("Break" , "Show you block break process" , Keyboard.KEY_NONE , Category.Render, true);
        registerSettings(displayRange, opacity, renderMode);
    }

    @Listener
    public void onRender3D(Render3DEvent event) {
        if (mc.player == null || mc.world == null) return;
        RayTraceResult objectMouseOver = Minecraft.getMinecraft().objectMouseOver;
        posPlayerLookingAt = objectMouseOver.getBlockPos();
        if (mc.playerController.getIsHittingBlock() && !(mc.world.getBlockState(posPlayerLookingAt).getBlock() == Blocks.AIR)) {
            if (objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
                if (mc.world.getBlockState(posPlayerLookingAt).getBlock() == Blocks.BEDROCK) {
                    return;
                }
            }
            float progress = ((IPlayerControllerMP) mc.playerController).getCurBlockDamageMP();

            BlockPos pos = ((IPlayerControllerMP) mc.playerController).getCurrentBlock();
            AxisAlignedBB bb = mc.world.getBlockState(pos).getBoundingBox(mc.world, pos).offset(pos);
            normalizedOpacity = opacity.floatValue() / opacity.floatValue();
            switch (renderMode.value()) {
                case Grow: {
                    ProgressBoxModifiers.Grow.renderBreaking(bb, progress, opacity.floatValue());
                    break;
                }
                case Shrink: {
                    ProgressBoxModifiers.Shrink.renderBreaking(bb, progress, opacity.floatValue());
                    break;
                }
                case Cross: {
                    ProgressBoxModifiers.Cross.renderBreaking(bb, progress, opacity.floatValue());
                    break;
                }
                case Fade: {
                    ProgressBoxModifiers.Fade.renderBreaking(bb, progress, opacity.floatValue());
                    break;
                }
                case UnFill: {
                    ProgressBoxModifiers.UnFill.renderBreaking(bb, progress, opacity.floatValue());
                    break;
                }
                case Fill: {
                    ProgressBoxModifiers.Fill.renderBreaking(bb, progress, opacity.floatValue());
                    break;
                }
                case Static: {
                    ProgressBoxModifiers.Static.renderBreaking(bb, progress, opacity.floatValue());
                    break;
                }
            }
        lastMinePos = pos;
        } else {
            if (normalizedOpacity > 0.0F && mc.world.getBlockState(lastMinePos).getBlock() == Blocks.AIR) {
                normalizedOpacity = (normalizedOpacity - 0.047F * event.partialTicks);
                GradientShader.setup(normalizedOpacity);
                RenderUtil.boxShader(lastMinePos);
                RenderUtil.outlineShader(lastMinePos);
                GradientShader.finish();
            }
        }



        ((IRenderGlobal) mc.renderGlobal).getDamagedBlocks().forEach(((integer, destroyBlockProgress) -> {
            if (destroyBlockProgress != null) {
                BlockPos pos = destroyBlockProgress.getPosition();
                if (mc.playerController.getIsHittingBlock() && !(mc.world.getBlockState(posPlayerLookingAt).getBlock() == Blocks.AIR)) {
                    if (((IPlayerControllerMP) mc.playerController).getCurrentBlock().equals(pos)) return;
                    if (mc.player.getDistance(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > displayRange.intValue()) return;
                    normalizedOpacity = opacity.floatValue() / opacity.floatValue();
                    lastMinePos = pos;
                } else {
                    if (normalizedOpacity > 0.0F) {
                        normalizedOpacity = (normalizedOpacity - 0.047F * event.partialTicks);
                        GradientShader.setup(normalizedOpacity);
                        RenderUtil.boxShader(lastMinePos);
                        RenderUtil.outlineShader(lastMinePos);
                        GradientShader.finish();
                    }
                }
                float progress = Math.min(1F, (float) destroyBlockProgress.getPartialBlockDamage() / 8F);

                AxisAlignedBB bb = mc.world.getBlockState(pos).getBoundingBox(mc.world, pos).offset(pos);

                switch (renderMode.value()) {
                    case Grow: {
                        ProgressBoxModifiers.Grow.renderBreaking(bb, progress, opacity.floatValue());
                        break;
                    }
                    case Shrink: {
                        ProgressBoxModifiers.Shrink.renderBreaking(bb, progress, opacity.floatValue());
                        break;
                    }
                    case Cross: {
                        ProgressBoxModifiers.Cross.renderBreaking(bb, progress, opacity.floatValue());
                        break;
                    }
                    default: {
                        ProgressBoxModifiers.Static.renderBreaking(bb, progress, opacity.floatValue());
                        break;
                    }
                }
            }
        }));
    }
}

