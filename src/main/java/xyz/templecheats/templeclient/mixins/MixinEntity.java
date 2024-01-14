package xyz.templecheats.templeclient.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.templecheats.templeclient.ModuleManager;
import xyz.templecheats.templeclient.impl.modules.render.Freecam;

@Mixin(value = Entity.class)
public class MixinEntity {
    @Shadow
    public void move(MoverType type, double x, double y, double z) {

    }

    @Inject(method = "isEntityInsideOpaqueBlock", at = @At("HEAD"), cancellable = true)
    public void isEntityInsideOpaqueBlock(CallbackInfoReturnable<Boolean> callback) {
        if(Minecraft.getMinecraft() != null && Minecraft.getMinecraft().player != null) {
            final Freecam freecam = (Freecam) ModuleManager.getModuleByName("Freecam");

            if(freecam != null && freecam.isEnabled()) {
                callback.setReturnValue(false);
            }
        }
    }
}