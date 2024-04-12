package xyz.templecheats.templeclient.mixins.render;

import net.minecraft.client.renderer.*;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import org.lwjgl.util.glu.Project;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.templecheats.templeclient.features.module.modules.client.Colors;
import xyz.templecheats.templeclient.features.module.modules.render.*;
import xyz.templecheats.templeclient.features.module.modules.render.esp.sub.Shader;

import javax.vecmath.Vector3f;
import java.awt.*;

@Mixin(value = EntityRenderer.class)
public abstract class MixinEntityRenderer {
    @Shadow protected abstract void hurtCameraEffect(float partialTicks);
    @Shadow private ItemStack itemActivationItem;
    @Shadow @Final private int[] lightmapColors;
    @Unique float aspect;

    @Inject(method = "renderWorldPass", at = @At("RETURN"))
    private void renderWorldPassPost(int pass, float partialTicks, long finishTimeNano, CallbackInfo ci) {
        if (Shader.INSTANCE.isEnabled() && Shader.INSTANCE.hand.booleanValue()) {
            Shader.INSTANCE.drawHand(partialTicks, pass);
        }
    }

    @Inject(method = "renderItemActivation", at = @At("HEAD"), cancellable = true)
    public void noRenderItemActivation(CallbackInfo info) {
        if (itemActivationItem != null && itemActivationItem.getItem().equals(Items.TOTEM_OF_UNDYING)) {
            if (NoRender.preventTotem()) {
                info.cancel();
            }
        }
    }

    @Inject(method = "setupFog", at = @At("HEAD"), cancellable = true)
    private void noRenderFog(int startCoords, float partialTicks, CallbackInfo ci) {
        if (NoRender.preventFog()) {
            GlStateManager.setFogDensity(0.0f);
            ci.cancel();
        }
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

    @Inject(method = "applyBobbing", at = @At("HEAD"), cancellable = true)
    private void noCameraBob(float partialTicks, CallbackInfo ci) {
        if(NoRender.preventBobbing()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderRainSnow", at = @At("HEAD"), cancellable = true)
    private void cancelWeather(float partialTicks, CallbackInfo ci) {
        if (NoRender.preventWeather()) {
            ci.cancel();
        }
    }

    @Redirect(method = "setupCameraTransform", at = @At(value = "INVOKE", target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onSetupCameraTransform(float fovy, float aspect, float zNear, float zFar) {
        aspectControl(fovy, aspect, zNear, zFar);
    }

    @Redirect(method = "renderWorldPass", at = @At(value = "INVOKE", target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onRenderWorldPass(float fovy, float aspect, float zNear, float zFar) {
        aspectControl(fovy, aspect, zNear, zFar);
    }

    @Redirect(method = "renderCloudsCheck", at = @At(value = "INVOKE", target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onRenderCloudsCheck(float fovy, float aspect, float zNear, float zFar) {
        aspectControl(fovy, aspect, zNear, zFar);
    }

    @Unique
    private void aspectControl(float fov, float aspect, float zNear, float zFar) {
        if (Aspect.INSTANCE.isEnabled()) {
            this.aspect = Aspect.INSTANCE.width.floatValue() / Aspect.INSTANCE.height.floatValue();
        } else {
            this.aspect = aspect;
        }
        Project.gluPerspective(fov, this.aspect, zNear, zFar);
    }

    @Inject(method = "updateLightmap", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/DynamicTexture;updateDynamicTexture()V", shift = At.Shift.BEFORE))
    private void updateLightMapHook(float partialTicks, CallbackInfo ci) {
        if (Ambience.INSTANCE.isEnabled() && Ambience.INSTANCE.LightMapState.booleanValue()) {
            for (int i = 0; i < this.lightmapColors.length; ++i) {
                Color ambientColor = Colors.INSTANCE.getLightMapColor();
                Vector3f finalValues = temple_Client_Development$getVector3f(ambientColor, i);
                int red = (int) (finalValues.x * 255.0f);
                int green = (int) (finalValues.y * 255.0f);
                int blue = (int) (finalValues.z * 255.0f);
                this.lightmapColors[i] = 0xFF000000 | red << 16 | green << 8 | blue;
            }
        }
    }

    // TODO: Move these thing to some where
    @Unique
    private Vector3f temple_Client_Development$getVector3f(Color ambientColor, int i) {
        int alpha = ambientColor.getAlpha();
        float modifier = (float) alpha / 255.0f;
        int color = this.lightmapColors[i];
        int[] bgr = temple_Client_Development$toRGBAArray(color);
        Vector3f values = new Vector3f((float) bgr[2] / 255.0f, (float) bgr[1] / 255.0f, (float) bgr[0] / 255.0f);
        Vector3f newValues = new Vector3f((float) ambientColor.getRed() / 255.0f, (float) ambientColor.getGreen() / 255.0f, (float) ambientColor.getBlue() / 255.0f);
        return temple_Client_Development$mix(values, newValues, modifier);
    }

    @Unique
    private int[] temple_Client_Development$toRGBAArray(int colorBuffer) {
        return new int[]{colorBuffer >> 16 & 0xFF, colorBuffer >> 8 & 0xFF, colorBuffer & 0xFF};
    }

    @Unique
    private Vector3f temple_Client_Development$mix(Vector3f first, Vector3f second, float factor) {
        return new Vector3f(first.x * (1.0f - factor) + second.x * factor, first.y * (1.0f - factor) + second.y * factor, first.z * (1.0f - factor) + first.z * factor);
    }
}