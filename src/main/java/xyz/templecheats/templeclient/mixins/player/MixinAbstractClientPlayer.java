package xyz.templecheats.templeclient.mixins.player;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.mixins.entity.MixinEntityPlayer;

import javax.annotation.Nullable;

@Mixin(value = AbstractClientPlayer.class)
public abstract class MixinAbstractClientPlayer extends MixinEntityPlayer {
    @Shadow
    @Nullable
    protected abstract NetworkPlayerInfo getPlayerInfo();

    @Inject(method = "getLocationCape", at = @At("HEAD"), cancellable = true)
    public void preGetLocationCape(CallbackInfoReturnable<ResourceLocation> callback) {
        final NetworkPlayerInfo info = this.getPlayerInfo();
        if (info == null) {
            return;
        }

        final ResourceLocation resourceLocation = TempleClient.capeManager.getCapeByUuid(getGameProfile().getId());
        if (resourceLocation != null) {
            callback.setReturnValue(resourceLocation);
        }
    }
}