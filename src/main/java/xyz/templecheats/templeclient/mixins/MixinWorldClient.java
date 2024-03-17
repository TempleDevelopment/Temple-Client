package xyz.templecheats.templeclient.mixins;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.event.events.world.EntityEvent;

@Mixin(value = WorldClient.class)
public class MixinWorldClient {
    @Inject(method = "addEntityToWorld", at = @At("TAIL"))
    public void addEntityToWorld(int entityID, Entity entityToSpawn, CallbackInfo callback) {
        if(entityToSpawn != null) {
            final EntityEvent.Add event = new EntityEvent.Add(entityToSpawn);
            TempleClient.eventBus.dispatchEvent(event);
        }
    }
    
    @Inject(method = "removeEntityFromWorld", at = @At("HEAD"))
    public void removeEntityFromWorld(int entityID, CallbackInfoReturnable<Entity> callback) {
        final Entity entity = WorldClient.class.cast(this).getEntityByID(entityID);
        
        if(entity != null) {
            final EntityEvent.Delete event = new EntityEvent.Delete(entity);
            TempleClient.eventBus.dispatchEvent(event);
        }
    }
}
