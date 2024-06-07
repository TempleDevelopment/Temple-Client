package xyz.templecheats.templeclient.mixins.accessor;

import net.minecraft.network.play.server.SPacketExplosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SPacketExplosion.class)
public interface IMixinSPacketExplosion {
    @Accessor("motionX")
    @Mutable
    void setMotionX(float motionX);

    @Accessor("motionY")
    @Mutable
    void setMotionY(float motionY);

    @Accessor("motionZ")
    @Mutable
    void setMotionZ(float motionZ);
}
