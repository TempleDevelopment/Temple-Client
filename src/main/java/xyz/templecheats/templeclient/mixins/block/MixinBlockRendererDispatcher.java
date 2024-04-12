package xyz.templecheats.templeclient.mixins.block;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.templecheats.templeclient.features.module.modules.render.xray.sub.Ores;
import xyz.templecheats.templeclient.manager.ModuleManager;

@Mixin(BlockRendererDispatcher.class)
public abstract class MixinBlockRendererDispatcher {
    @Shadow
    @Final
    private BlockModelRenderer blockModelRenderer;

    @Shadow
    public abstract IBakedModel getModelForState(IBlockState state);

    @Inject(method = "renderBlock", at = @At("HEAD"), cancellable = true)
    public void renderBlock(IBlockState state, BlockPos pos, IBlockAccess blockAccess, BufferBuilder bufferBuilderIn, CallbackInfoReturnable<Boolean> info) {
        if (ModuleManager.getModuleByName("XRay").isEnabled()) {
            Block block = state.getBlock();
            Ores oresModule = (Ores) ModuleManager.getModuleByName("Ores");
            if (oresModule.isEnabled() && isOre(block) && shouldRenderOre(block, oresModule)) {
                info.setReturnValue(this.blockModelRenderer.renderModel(blockAccess, getModelForState(state), state, pos, bufferBuilderIn, false));
            } else {
                info.setReturnValue(false);
            }
        }
    }

    private boolean isOre(Block block) {
        return block == Blocks.DIAMOND_ORE || block == Blocks.IRON_ORE || block == Blocks.GOLD_ORE || block == Blocks.LAPIS_ORE
                || block == Blocks.REDSTONE_ORE || block == Blocks.EMERALD_ORE || block == Blocks.COAL_ORE
                || block == Blocks.QUARTZ_ORE || block == Blocks.REDSTONE_ORE || block == Blocks.LIT_REDSTONE_ORE;
    }

    private boolean shouldRenderOre(Block block, Ores oresModule) {
        if (block == Blocks.DIAMOND_ORE) {
            return oresModule.shouldRenderDiamondOres();
        } else if (block == Blocks.IRON_ORE) {
            return oresModule.shouldRenderIronOres();
        } else if (block == Blocks.GOLD_ORE) {
            return oresModule.shouldRenderGoldOres();
        } else if (block == Blocks.LAPIS_ORE) {
            return oresModule.shouldRenderLapisOres();
        } else if (block == Blocks.REDSTONE_ORE || block == Blocks.LIT_REDSTONE_ORE) {
            return oresModule.shouldRenderRedstoneOres();
        } else if (block == Blocks.EMERALD_ORE) {
            return oresModule.shouldRenderEmeraldOres();
        } else if (block == Blocks.COAL_ORE) {
            return oresModule.shouldRenderCoalOres();
        } else if (block == Blocks.QUARTZ_ORE) {
            return oresModule.shouldRenderQuartzOres();
        } else {
            return false;
        }
    }
}