package xyz.templecheats.templeclient.mixins.render;

import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.templecheats.templeclient.event.events.render.RenderChunkEvent;

@Mixin(RenderChunk.class)
public class MixinRenderChunk {
    @Inject(method = "setPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos$MutableBlockPos;setPos(III)Lnet/minecraft/util/math/BlockPos$MutableBlockPos;"))
    private void setPositionHook(int x, int y, int z, CallbackInfo callbackInfo) {
        MinecraftForge.EVENT_BUS.post(new RenderChunkEvent((RenderChunk) (Object) this, new BlockPos(x, y, z)));
    }
}
