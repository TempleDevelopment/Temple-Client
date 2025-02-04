package xyz.templecheats.templeclient.features.module.modules.player;

import net.minecraft.network.play.client.CPacketCloseWindow;
import org.lwjgl.input.Keyboard;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.event.events.network.PacketEvent;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.mixins.accessor.ICPacketCloseWindow;

public class XCarry extends Module {

    public XCarry() {
        super("XCarry", "Hold items in your crafting table slots", Keyboard.KEY_NONE, Category.Player);
    }

    @Listener
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketCloseWindow) {
            ICPacketCloseWindow packet = (ICPacketCloseWindow) new CPacketCloseWindow();
            if (packet.getWindowId() == mc.player.inventoryContainer.windowId) {
                event.setCanceled(checkSlots());
            }
        }
    }

    public boolean checkSlots() {
        for (int i = 1; i <= 4; ++i) {
            if (!mc.player.inventoryContainer.getSlot(i).getStack().isEmpty()) {
                return true;
            }
        }
        return false;
    }
}