package xyz.templecheats.templeclient.mixins.render;

import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import org.lwjgl.util.glu.Project;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.templecheats.templeclient.features.module.modules.client.Colors;
import xyz.templecheats.templeclient.features.module.modules.render.Ambience;
import xyz.templecheats.templeclient.features.module.modules.render.Aspect;
import xyz.templecheats.templeclient.features.module.modules.render.ViewClip;
import xyz.templecheats.templeclient.features.module.modules.render.esp.sub.Hand;
import xyz.templecheats.templeclient.features.module.modules.render.norender.sub.Player;
import xyz.templecheats.templeclient.features.module.modules.render.norender.sub.World;

import javax.vecmath.Vector3f;
import java.awt.*;

import static xyz.templecheats.templeclient.util.color.ColorUtil.vector3F;

@Mixin(value = EntityRenderer.class)
public abstract class MixinEntityRenderer {
    @Shadow
    protected abstract void hurtCameraEffect(float partialTicks);

    @Shadow
    private ItemStack itemActivationItem;
    @Shadow
    @Final
    private int[] lightmapColors;
    @Unique
    float aspect;

    @Inject(method = "renderWorldPass", at = @At("RETURN"))
    private void renderWorldPassPost(int pass, float partialTicks, long finishTimeNano, CallbackInfo ci) {
        if (Hand.INSTANCE.isEnabled()) {
            Hand.INSTANCE.drawHand(partialTicks, pass);
        }
    }

    @Inject(method = "renderItemActivation", at = @At("HEAD"), cancellable = true)
    public void noRenderItemActivation(CallbackInfo info) {
        if (itemActivationItem != null && itemActivationItem.getItem().equals(Items.TOTEM_OF_UNDYING)) {
            if (Player.preventTotem()) {
                info.cancel();
            }
        }
    }

    @Inject(method = "setupFog", at = @At("HEAD"), cancellable = true)
    private void noRenderFog(int startCoords, float partialTicks, CallbackInfo ci) {
        if (World.preventFog()) {
            GlStateManager.setFogDensity(0.0f);
            ci.cancel();
        }
    }

    @Redirect(method = "setupFog", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;isPotionActive(Lnet/minecraft/potion/Potion;)Z"))
    private boolean noRenderBlindness(EntityLivingBase instance, Potion potion) {
        if (Player.preventBlindness()) return false;
        return instance.isPotionActive(potion);
    }

    @ModifyVariable(method = "setupCameraTransform", index = 4, at = @At("STORE"))
    private float noRenderNausea(float original) {
        if (Player.preventNausea()) return 0.0f;
        return original;
    }

    @ModifyVariable(method = {"orientCamera"}, ordinal = 3, at = @At(value = "STORE", ordinal = 0), require = 1)
    public double changeCameraDistanceHook(double range) {
        if (ViewClip.INSTANCE.isEnabled()) {
            return ViewClip.INSTANCE.distance.doubleValue();
        } else {
            return range;
        }
    }

    @ModifyVariable(method = {"orientCamera"}, ordinal = 7, at = @At(value = "STORE", ordinal = 0), require = 1)
    public double orientCameraHook(double range) {
        if (ViewClip.INSTANCE.isEnabled()) {
            return ViewClip.INSTANCE.distance.doubleValue();
        } else {
            return range;
        }
    }

    @Redirect(method = "setupCameraTransform", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/EntityRenderer;hurtCameraEffect(F)V"))
    private void noHurtCam(EntityRenderer instance, float partialTicks) {
        if (Player.preventHurtCam()) return;
        else this.hurtCameraEffect(partialTicks);
    }

    @Inject(method = "applyBobbing", at = @At("HEAD"), cancellable = true)
    private void noCameraBob(float partialTicks, CallbackInfo ci) {
        if (Player.preventBobbing()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderRainSnow", at = @At("HEAD"), cancellable = true)
    private void cancelWeather(float partialTicks, CallbackInfo ci) {
        if (World.preventWeather()) {
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
                Vector3f finalValues = vector3F(ambientColor, lightmapColors, i);
                int red = (int) (finalValues.x * 255.0f);
                int green = (int) (finalValues.y * 255.0f);
                int blue = (int) (finalValues.z * 255.0f);
                this.lightmapColors[i] = 0xFF000000 | red << 16 | green << 8 | blue;
            }
        }
    }
}