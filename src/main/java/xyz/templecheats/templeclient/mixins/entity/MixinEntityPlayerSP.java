package xyz.templecheats.templeclient.mixins.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.MoverType;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.event.EventStageable;
import xyz.templecheats.templeclient.event.events.player.MotionEvent;
import xyz.templecheats.templeclient.event.events.player.MoveEvent;
import xyz.templecheats.templeclient.event.events.player.RotationUpdateEvent;
import xyz.templecheats.templeclient.features.module.modules.render.Freecam;
import xyz.templecheats.templeclient.manager.ModuleManager;

@Mixin(value = EntityPlayerSP.class)
public class MixinEntityPlayerSP extends MixinEntity {
    @Unique
    private float savedYaw, savedPitch;

    @Inject(method = "onUpdateWalkingPlayer", at = @At("HEAD"), cancellable = true)
    private void onUpdateWalkingPlayerHead(CallbackInfo callback) {
        RotationUpdateEvent rotationUpdateEvent = new RotationUpdateEvent();
        MinecraftForge.EVENT_BUS.post(rotationUpdateEvent);
        if (rotationUpdateEvent.isCanceled()) {
            callback.cancel();
        }
        final EntityPlayerSP player = EntityPlayerSP.class.cast(this);

        final MotionEvent event = new MotionEvent(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch, player.onGround, EventStageable.EventStage.PRE);
        TempleClient.eventBus.dispatchEvent(event);
        if (event.isCanceled()) {
            callback.cancel();
            return;
        }

        savedYaw = player.rotationYaw;
        savedPitch = player.rotationPitch;
        player.rotationYaw = event.getYaw();
        player.rotationPitch = event.getPitch();
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("TAIL"))
    private void onUpdateWalkingPlayerTail(CallbackInfo callback) {
        final EntityPlayerSP player = EntityPlayerSP.class.cast(this);

        TempleClient.eventBus.dispatchEvent(new MotionEvent(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch, player.onGround, EventStageable.EventStage.POST));

        player.rotationYaw = savedYaw;
        player.rotationPitch = savedPitch;
    }

    @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
    public void pushOutOfBlocks(double x, double y, double z, CallbackInfoReturnable<Boolean> callback) {
        if (Minecraft.getMinecraft() != null && Minecraft.getMinecraft().player != null) {
            Freecam freecam = ModuleManager.getModule(Freecam.class);

            if (freecam != null && freecam.isEnabled()) {
                callback.setReturnValue(false);
            }
        }
    }

    @Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;move(Lnet/minecraft/entity/MoverType;DDD)V"), require = 0)
    public void move(AbstractClientPlayer player, MoverType type, double x, double y, double z) {
        final MoveEvent event = new MoveEvent(x, y, z);
        TempleClient.eventBus.dispatchEvent(event);
        if (event.isCanceled()) {
            return;
        }

        super.move(type, event.x, event.y, event.z);
    }
}