package xyz.templecheats.templeclient.mixins;

import net.minecraft.network.play.server.SPacketExplosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = SPacketExplosion.class)
public interface IMixinSPacketExplosion {
    @Accessor("motionX")
    void setMotionX(final float motionX);

    @Accessor("motionY")
    void setMotionY(final float motionY);

    @Accessor("motionZ")
    void setMotionZ(final float motionZ);
}