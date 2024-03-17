package xyz.templecheats.templeclient.mixins;

import net.minecraft.client.gui.GuiIngame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.templecheats.templeclient.features.module.modules.client.hud.PotionHUD;

@Mixin(GuiIngame.class)
public class MixinPotionOverlay {
    @Inject(method = "renderPotionEffects", at = @At("HEAD"), cancellable = true)
    public void renderPotionEffects(CallbackInfo ci) {
        if (PotionHUD.INSTANCE.isEnabled()) {
            ci.cancel();
        }
    }
}
