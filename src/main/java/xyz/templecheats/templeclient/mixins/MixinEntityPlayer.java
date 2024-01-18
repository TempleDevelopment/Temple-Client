package xyz.templecheats.templeclient.mixins;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = EntityPlayer.class)
public abstract class MixinEntityPlayer {
    @Shadow
    public abstract GameProfile getGameProfile();
}