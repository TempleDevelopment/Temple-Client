package xyz.templecheats.templeclient.mixins.entity;

import net.minecraft.entity.passive.EntityPig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.event.events.entity.HorseSaddledEvent;
import xyz.templecheats.templeclient.event.events.entity.SteerEntityEvent;

@Mixin(EntityPig.class)
public class MixinEntityPig {
    @Inject(method = "canBeSteered", at = @At("HEAD"), cancellable = true)
    public void canBeSteered(CallbackInfoReturnable<Boolean> cir) {
        SteerEntityEvent event = new SteerEntityEvent();
        TempleClient.eventBus.dispatchEvent(event);

        if (event.isCanceled()) {
            cir.cancel();
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getSaddled", at = @At("HEAD"), cancellable = true)
    public void getSaddled(CallbackInfoReturnable<Boolean> cir) {
        HorseSaddledEvent event = new HorseSaddledEvent();
        TempleClient.eventBus.dispatchEvent(event);

        if (event.isCanceled()) {
            cir.cancel();
            cir.setReturnValue(true);
        }
    }
}