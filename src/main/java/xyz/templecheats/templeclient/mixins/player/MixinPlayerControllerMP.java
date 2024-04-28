package xyz.templecheats.templeclient.mixins.player;

import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.event.events.player.AttackEvent;
import xyz.templecheats.templeclient.event.events.player.BlockResetEvent;

@Mixin(PlayerControllerMP.class)
public class MixinPlayerControllerMP {
    @Inject(method = "attackEntity", at = @At("HEAD"), cancellable = true)
    public void attackEntityPre(EntityPlayer playerIn , Entity targetEntity , CallbackInfo ci) {
        if (targetEntity == null) return;
        AttackEvent event = new AttackEvent.Pre(targetEntity);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) ci.cancel();
    }

    @Inject(method = "attackEntity", at = @At("RETURN"))
    public void attackEntityPost(EntityPlayer playerIn , Entity targetEntity , CallbackInfo ci) {
        if (targetEntity == null) return;
        AttackEvent event = new AttackEvent.Post(targetEntity);
        MinecraftForge.EVENT_BUS.post(event);
    }

    @Inject(method = "resetBlockRemoving", at = @At(value = "HEAD"), cancellable = true)
    private void onResetBlockRemoving(CallbackInfo info) {
        BlockResetEvent blockResetEvent = new BlockResetEvent();
        MinecraftForge.EVENT_BUS.post(blockResetEvent);

        if (blockResetEvent.isCanceled()) {
            info.cancel();
        }
    }
}