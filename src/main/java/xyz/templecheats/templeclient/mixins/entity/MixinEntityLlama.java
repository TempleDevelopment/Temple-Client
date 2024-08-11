package xyz.templecheats.templeclient.mixins.entity;

import net.minecraft.entity.passive.EntityLlama;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.event.events.entity.SteerEntityEvent;

@Mixin(EntityLlama.class)
public class MixinEntityLlama {
    @Inject(method = "canBeSteered", at = @At("HEAD"), cancellable = true)
    public void canBeSteered(CallbackInfoReturnable<Boolean> cir) {
        SteerEntityEvent event = new SteerEntityEvent();
        TempleClient.eventBus.dispatchEvent(event);

        if (event.isCanceled()) {
            cir.cancel();
            cir.setReturnValue(true);
        }
    }
}
