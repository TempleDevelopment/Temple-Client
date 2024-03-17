package xyz.templecheats.templeclient.event.events.network;

import net.minecraft.network.Packet;
import xyz.templecheats.templeclient.event.EventCancellable;

public class PacketEvent extends EventCancellable {

    private final Packet packet;

    public PacketEvent(Packet packet) {
        this.packet = packet;
    }

    public Packet getPacket() {
        return packet;
    }

    public static class Receive extends PacketEvent {
        public Receive(Packet packet) {
            super(packet);
        }
    }

    public static class Send extends PacketEvent {
        public Send(Packet packet) {
            super(packet);
        }
    }
}