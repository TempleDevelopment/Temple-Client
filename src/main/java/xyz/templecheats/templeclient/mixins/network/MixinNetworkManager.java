package xyz.templecheats.templeclient.mixins.network;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.event.events.network.PacketEvent;

@Mixin(value = NetworkManager.class)
public class MixinNetworkManager {
    @Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
    public void IchannelRead0(ChannelHandlerContext context, Packet<?> packet, CallbackInfo callback) {
        final PacketEvent.Receive event = new PacketEvent.Receive(packet);
        TempleClient.eventBus.dispatchEvent(event);
        if (event.isCanceled()) {
            callback.cancel();
        }
    }

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    public void IsendPacket(Packet<?> packet, CallbackInfo callback) {
        final PacketEvent.Send event = new PacketEvent.Send(packet);
        TempleClient.eventBus.dispatchEvent(event);
        if (event.isCanceled()) {
            callback.cancel();
        }
    }
}