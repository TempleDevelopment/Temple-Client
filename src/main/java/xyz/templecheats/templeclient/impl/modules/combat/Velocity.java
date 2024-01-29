package xyz.templecheats.templeclient.impl.modules.combat;

import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import org.lwjgl.input.Keyboard;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.api.event.events.network.PacketEvent;
import xyz.templecheats.templeclient.impl.modules.Module;
import xyz.templecheats.templeclient.mixins.IMixinSPacketExplosion;

public class Velocity extends Module {
    public Velocity() {
        super("Velocity","Reduces knockback", Keyboard.KEY_NONE, Category.COMBAT);
    }

    @Listener
    public void onPacketRecieve(PacketEvent.Receive event) {
        if(event.getPacket() instanceof SPacketEntityVelocity && ((SPacketEntityVelocity) event.getPacket()).getEntityID() == mc.player.getEntityId()) {
            event.setCanceled(true);
        }

        if(event.getPacket() instanceof SPacketExplosion) {
            ((IMixinSPacketExplosion) event.getPacket()).setMotionX(0);
            ((IMixinSPacketExplosion) event.getPacket()).setMotionY(0);
            ((IMixinSPacketExplosion) event.getPacket()).setMotionZ(0);
        }
    }
}