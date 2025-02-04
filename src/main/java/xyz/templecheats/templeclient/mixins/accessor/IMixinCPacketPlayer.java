package xyz.templecheats.templeclient.mixins.accessor;

import net.minecraft.network.play.client.CPacketPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = CPacketPlayer.class)
public interface IMixinCPacketPlayer {
    @Accessor("x")
    void setX(double x);

    @Accessor("y")
    void setY(double y);

    @Accessor("z")
    void setZ(double z);

    @Accessor("yaw")
    void setYaw(float yaw);

    @Accessor("pitch")
    void setPitch(float pitch);

    @Accessor("moving")
    boolean isMoving();

    @Accessor("rotating")
    boolean isRotating();

    @Accessor(value = "onGround")
    void setOnGround(boolean onGround);
}