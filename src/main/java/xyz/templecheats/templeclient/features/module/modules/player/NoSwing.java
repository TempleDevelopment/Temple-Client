package xyz.templecheats.templeclient.features.module.modules.player;

import net.minecraft.network.play.client.CPacketAnimation;
import org.lwjgl.input.Keyboard;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.event.events.network.PacketEvent;
import xyz.templecheats.templeclient.features.module.Module;

public class NoSwing extends Module {

    public NoSwing() {
        super("NoSwing", "Disables swinging", Keyboard.KEY_NONE, Category.Player);
    }

    @Listener
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketAnimation) {
            event.setCanceled(true);
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
