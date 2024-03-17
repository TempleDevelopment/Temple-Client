package xyz.templecheats.templeclient.mixins;

import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.templecheats.templeclient.features.module.modules.render.NoRender;

@Mixin(value = EntityRenderer.class)
public abstract class MixinEntityRenderer {
    @Shadow protected abstract void hurtCameraEffect(float partialTicks);

    @Inject(method = "setupFog", at = @At("HEAD"), cancellable = true)
    private void noRenderFog(int startCoords, float partialTicks, CallbackInfo ci) {
        if (NoRender.preventFog()) ci.cancel();
    }

    @Redirect(method = "setupFog", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;isPotionActive(Lnet/minecraft/potion/Potion;)Z"))
    private boolean noRenderBlindness(EntityLivingBase instance, Potion potion) {
        if (NoRender.preventBlindness()) return false;
        return instance.isPotionActive(potion);
    }

    @ModifyVariable(method = "setupCameraTransform", index = 4, at = @At("STORE"))
    private float noRenderNausea(float original) {
        if (NoRender.preventNausea()) return 0.0f;
        return original;
    }

    @Redirect(method = "setupCameraTransform", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/EntityRenderer;hurtCameraEffect(F)V"))
    private void noHurtCam(EntityRenderer instance, float partialTicks) {
        if (NoRender.preventHurtCam()) return;
        else this.hurtCameraEffect(partialTicks);
    }
}
