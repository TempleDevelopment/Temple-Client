package xyz.templecheats.templeclient.features.module.modules.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.event.events.network.PacketEvent;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.mixins.accessor.IMixinSPacketEntityVelocity;
import xyz.templecheats.templeclient.mixins.accessor.IMixinSPacketExplosion;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;

public final class Velocity extends Module {
    /*
     * Settings
     */
    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", this, Mode.CancelPacket);
    private final IntSetting horizontalVelocity = new IntSetting("Horizontal", this, 1, 100, 100);
    private final IntSetting verticalVelocity = new IntSetting("Vertical", this, 1, 100, 100);
    private final BooleanSetting explosions = new BooleanSetting("Explosions", this, true);
    private boolean cancelVelocity = false;

    /*
     * Variables
     */

    public final Minecraft mc = Minecraft.getMinecraft();

    public Velocity() {
        super("Velocity", "Reduces knockback velocity", Keyboard.KEY_NONE, Category.Combat);
        registerSettings(explosions, horizontalVelocity, verticalVelocity, mode);
    }

    @Listener
    public void onPacketReceive(PacketEvent.Receive event) {
        if (mc.player == null) return;
        if (mode.value() == Mode.CancelPacket) {
            if (event.getPacket() instanceof SPacketEntityVelocity && ((SPacketEntityVelocity) event.getPacket()).getEntityID() == this.mc.player.getEntityId() || event.getPacket() instanceof SPacketExplosion || event.getPacket() instanceof EntityFishHook) {
                event.setCanceled(true);
            }
        } else {
            if (event.getPacket() instanceof SPacketEntityVelocity) {
                SPacketEntityVelocity velocityPacket = (SPacketEntityVelocity) event.getPacket();
                if (velocityPacket.getEntityID() == mc.player.getEntityId()) {
                    switch (mode.value()) {
                        case Normal:
                            if (this.horizontalVelocity.intValue() == 0 && this.verticalVelocity.intValue() == 0) {
                                event.setCanceled(true);
                            } else {
                                ((IMixinSPacketEntityVelocity) velocityPacket).setMotionX(velocityPacket.getMotionX() / 100 * this.horizontalVelocity.intValue());
                                ((IMixinSPacketEntityVelocity) velocityPacket).setMotionZ(velocityPacket.getMotionZ() / 100 * this.horizontalVelocity.intValue());
                                ((IMixinSPacketEntityVelocity) velocityPacket).setMotionY(velocityPacket.getMotionY() / 100 * this.verticalVelocity.intValue());
                            }
                            break;
                        case Grim:
                            event.setCanceled(true);
                            cancelVelocity = true;
                            break;
                    }
                }
            } else if (event.getPacket() instanceof SPacketExplosion && this.explosions.booleanValue()) {
                SPacketExplosion explosionPacket = (SPacketExplosion) event.getPacket();
                switch (mode.value()) {
                    case Normal:
                        if (this.horizontalVelocity.intValue() == 0 && this.verticalVelocity.intValue() == 0) {
                            ((IMixinSPacketExplosion) explosionPacket).setMotionX(0);
                            ((IMixinSPacketExplosion) explosionPacket).setMotionY(0);
                            ((IMixinSPacketExplosion) explosionPacket).setMotionZ(0);
                        } else {
                            ((IMixinSPacketExplosion) explosionPacket).setMotionX(explosionPacket.getMotionX() / 100 * this.horizontalVelocity.intValue());
                            ((IMixinSPacketExplosion) explosionPacket).setMotionY(explosionPacket.getMotionY() / 100 * this.verticalVelocity.intValue());
                            ((IMixinSPacketExplosion) explosionPacket).setMotionZ(explosionPacket.getMotionZ() / 100 * this.horizontalVelocity.intValue());
                        }
                        break;
                    case Grim:
                        event.setCanceled(true);
                        cancelVelocity = true;
                        break;
                }
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START && cancelVelocity && mode.value() == Mode.Grim) {
            cancelVelocity = false;
            float yaw = mc.player.rotationYaw;
            float pitch = mc.player.rotationPitch;
            mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX, mc.player.getEntityBoundingBox().minY, mc.player.posZ, yaw, pitch, mc.player.onGround));
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
        }
    }

    public enum Mode {
        Normal,
        CancelPacket,
        Grim
    }
}