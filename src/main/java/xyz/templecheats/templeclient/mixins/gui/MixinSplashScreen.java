package xyz.templecheats.templeclient.mixins.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraftforge.fml.client.SplashProgress;
import org.lwjgl.LWJGLException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Mixin(value = {SplashProgress.class})
public class MixinSplashScreen {
    @Inject(method = {"drawVanillaScreen"}, at = {@At(value = "HEAD")}, cancellable = true, remap = false)
    private static void drawVanillaScreen(TextureManager textureManager, CallbackInfo callbackInfo) throws LWJGLException {
        Minecraft.getMinecraft().drawSplashScreen(textureManager);
        callbackInfo.cancel();
    }

    @Inject(method = {"start"}, at = {@At(value = "HEAD")}, cancellable = true, remap = false)
    private static void start(CallbackInfo callbackInfo) {
        try {
            Method method = SplashProgress.class.getDeclaredMethod("disableSplash", new Class[0]);
            method.setAccessible(true);
            method.invoke(null, new Object[0]);
        } catch (IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException reflectiveOperationException) {
            reflectiveOperationException.printStackTrace();
        }
        callbackInfo.cancel();
    }
}
