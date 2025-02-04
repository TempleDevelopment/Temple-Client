package xyz.templecheats.templeclient.mixins.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.event.events.player.ModelEvent;

@Mixin(value = RenderLivingBase.class, priority = 114514)
public abstract class MixinRenderLivingBase<T extends EntityLivingBase> {
    @Shadow
    protected ModelBase mainModel;

    @Inject(method = "renderModel", at = @At("TAIL"), cancellable = true)
    public void hookRenderModel(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, CallbackInfo ci) {
        ModelEvent event = new ModelEvent(mainModel, entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
        TempleClient.eventBus.dispatchEvent(event);

        if (event.isCanceled()) {
            ci.cancel();
        }
    }
}
