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

@Mixin(value = BlockLiquid.class)
public class MixinBlockLiquid {
    @Inject(method = "getCollisionBoundingBox", at = @At("HEAD"), cancellable = true)
    public void getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos, CallbackInfoReturnable < AxisAlignedBB > callback) {
        if (Minecraft.getMinecraft() == null || Minecraft.getMinecraft().player == null) {
            return;
        }

        final LiquidCollisionEvent event = new LiquidCollisionEvent(pos);
        TempleClient.eventBus.dispatchEvent(event);
        if (event.getBoundingBox() != null && !event.getBoundingBox().equals(BlockLiquid.NULL_AABB)) {
            callback.setReturnValue(event.getBoundingBox());
        }
    }
}