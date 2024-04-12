package xyz.templecheats.templeclient.mixins.render;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.event.events.render.HeldItemEvent;
import xyz.templecheats.templeclient.util.Globals;

@Mixin(LayerHeldItem.class)
public class MixinLayerHeldItem implements Globals {

    @Inject(method = "renderHeldItem", at = @At("HEAD"), cancellable = true)
    private void renderHeldItem(EntityLivingBase entity, ItemStack item, ItemCameraTransforms.TransformType transformType, EnumHandSide handSide, CallbackInfo ci) {
        if (entity instanceof EntityPlayer && !entity.equals(mc.player)) {
            final HeldItemEvent event = new HeldItemEvent();
            TempleClient.eventBus.dispatchEvent(event);
            if (event.isCanceled()) {
                ci.cancel();
            }
        }
    }
}