package xyz.templecheats.templeclient.mixins.accessor;

import net.minecraft.network.play.server.SPacketEntityVelocity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SPacketEntityVelocity.class)
public interface IMixinSPacketEntityVelocity {
    @Accessor("motionX")
    @Mutable
    void setMotionX(int motionX);

    @Accessor("motionY")
    @Mutable
    void setMotionY(int motionY);

    @Accessor("motionZ")
    @Mutable
    void setMotionZ(int motionZ);
}
