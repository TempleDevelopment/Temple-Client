package xyz.templecheats.templeclient.manager;

import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.event.events.network.PacketEvent;
import xyz.templecheats.templeclient.event.events.player.MotionEvent;
import xyz.templecheats.templeclient.event.events.player.RenderRotationsEvent;
import xyz.templecheats.templeclient.mixins.accessor.ICPacketPlayer;
import xyz.templecheats.templeclient.util.Globals;
import xyz.templecheats.templeclient.util.rotation.RotationUtil;

public class RotationManager implements Globals {

    /****************************************************************
     *                  Rotation State Variables
     ****************************************************************/

    private final RotationUtil serverRotation = new RotationUtil(Float.NaN, Float.NaN);
    private RotationUtil rotation = new RotationUtil(Float.NaN, Float.NaN);
    private long stay = 0;

    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    /****************************************************************
     *                  Event Handlers
     ****************************************************************/

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (System.currentTimeMillis() - stay >= 250 && rotation.isValid()) {
            rotation = new RotationUtil(Float.NaN, Float.NaN);
        }
    }

    @Listener
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer) {
            CPacketPlayer packet = (CPacketPlayer) event.getPacket();
            if (((ICPacketPlayer) packet).isRotating()) {
                serverRotation.setYaw(packet.getYaw(0));
                serverRotation.setPitch(packet.getPitch(0));
            }
        }
    }

    @Listener
    public void onMotionUpdate(MotionEvent event) {
        if (rotation.isValid()) {
            event.setOnGround(mc.player.onGround);
            event.setX(mc.player.posX);
            event.setY(mc.player.getEntityBoundingBox().minY);
            event.setZ(mc.player.posZ);

            event.setYaw(rotation.getYaw());
            event.setPitch(rotation.getPitch());

            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onRenderRotations(RenderRotationsEvent event) {
        if (rotation.isValid()) {
            event.setCanceled(true);
            event.setYaw(serverRotation.getYaw());
            event.setPitch(serverRotation.getPitch());
        }
    }

    /****************************************************************
     *                  Rotation Management Methods
     ****************************************************************/

    public void setRotation(RotationUtil in) {
        rotation = in;
        stay = System.currentTimeMillis();
    }

    public RotationUtil getRotation() {
        return rotation;
    }

    public RotationUtil getServerRotation() {
        return serverRotation;
    }
}
