package xyz.templecheats.templeclient.mixins.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.GameSettings;
import org.lwjgl.Sys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.templecheats.templeclient.features.gui.splash.SplashProgress;

@Mixin(Minecraft.class)
public abstract class MixinSplashProgress {

    @Shadow
    public GameSettings gameSettings;

    public long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }

    @Inject(method = "drawSplashScreen", at = @At("HEAD"), cancellable = true)
    public void drawSplashScreen(TextureManager textureManager, CallbackInfo callbackInfo) {
        SplashProgress.drawSplash(textureManager);
        SplashProgress.setProgress(1, "Starting Game...");
        callbackInfo.cancel();
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureMap;<init>(Ljava/lang/String;)V", shift = At.Shift.BEFORE))
    private void onLoadingTextureMap(CallbackInfo callbackInfo) {
        SplashProgress.setProgress(2, "Loading Texture Map...");
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/model/ModelManager;<init>(Lnet/minecraft/client/renderer/texture/TextureMap;)V", shift = At.Shift.BEFORE))
    private void onLoadingModelManager(CallbackInfo callbackInfo) {
        SplashProgress.setProgress(3, "Loading Model Manager...");
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderItem;<init>(Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/client/renderer/block/model/ModelManager;Lnet/minecraft/client/renderer/color/ItemColors;)V", shift = At.Shift.BEFORE))
    private void onLoadingItemRenderer(CallbackInfo callbackInfo) {
        SplashProgress.setProgress(4, "Loading Item Renderer...");
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/EntityRenderer;<init>(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/resources/IResourceManager;)V", shift = At.Shift.BEFORE))
    private void onLoadingEntityRenderer(CallbackInfo callbackInfo) {
        SplashProgress.setProgress(5, "Loading Entity Renderer...");
    }
}