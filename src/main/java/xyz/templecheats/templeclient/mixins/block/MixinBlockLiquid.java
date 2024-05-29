package xyz.templecheats.templeclient.mixins.block;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.event.events.world.LiquidCollisionEvent;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.world.LiquidInteract;
import xyz.templecheats.templeclient.manager.ModuleManager;

@Mixin(value = BlockLiquid.class)
public class MixinBlockLiquid {
    @Inject(method = "getCollisionBoundingBox", at = @At("HEAD"), cancellable = true)
    public void getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos, CallbackInfoReturnable<AxisAlignedBB> callback) {
        if (Minecraft.getMinecraft() == null || Minecraft.getMinecraft().player == null) {
            return;
        }

        final LiquidCollisionEvent event = new LiquidCollisionEvent(pos);
        TempleClient.eventBus.dispatchEvent(event);
        if (event.getBoundingBox() != null && !event.getBoundingBox().equals(BlockLiquid.NULL_AABB)) {
            callback.setReturnValue(event.getBoundingBox());
        }
    }

    @Inject(method = "canCollideCheck", at = @At("HEAD"), cancellable = true)
    public void canCollideCheck(final IBlockState blockState, final boolean b, final CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        Module liquidInteractModule = ModuleManager.getModule(LiquidInteract.class);
        if (liquidInteractModule != null) {
            callbackInfoReturnable.setReturnValue(liquidInteractModule.isEnabled() || (b && blockState.getValue(BlockLiquid.LEVEL) == 0));
        }
    }
}