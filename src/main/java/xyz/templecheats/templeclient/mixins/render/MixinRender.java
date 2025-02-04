package xyz.templecheats.templeclient.mixins.render;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.event.events.render.FireEvent;
import xyz.templecheats.templeclient.event.events.render.NamePlateEvent;
import xyz.templecheats.templeclient.util.Globals;

@Mixin(Render.class)
public class MixinRender<T extends Entity> implements Globals {

    @Inject(method = "renderEntityOnFire", at = @At("HEAD"), cancellable = true)
    private void renderEntityOnFire(Entity entity, double x, double y, double z, float partialTicks, CallbackInfo ci) {
        final FireEvent fireEvent = new FireEvent();
        TempleClient.eventBus.dispatchEvent(fireEvent);
        if (fireEvent.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderLivingLabel", at = @At("HEAD"), cancellable = true)
    private void renderLivingLabel(T entityIn, String str, double x, double y, double z, int maxDistance, CallbackInfo ci) {
        final NamePlateEvent nameplateEvent = new NamePlateEvent(entityIn);
        TempleClient.eventBus.dispatchEvent(nameplateEvent);
        if (nameplateEvent.isCanceled()) {
            ci.cancel();
        }
    }
}