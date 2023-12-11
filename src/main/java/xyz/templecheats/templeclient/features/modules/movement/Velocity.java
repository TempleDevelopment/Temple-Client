package xyz.templecheats.templeclient.features.modules.movement;

import xyz.templecheats.templeclient.features.modules.Module;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class Velocity extends Module {
    public Velocity() {
        super("Velocity", Keyboard.KEY_NONE, Category.COMBAT);
    }

    @SubscribeEvent
    public void onUpdate(TickEvent.PlayerTickEvent e) {
        if (mc.player.hurtTime > 0) {
            mc.player.connection.sendPacket(new CPacketPlayer.Position(0, 0, 0, true));


            mc.player.setPosition(mc.player.posX, mc.player.posY - 0.26, mc.player.posZ);
            mc.player.setPosition(mc.player.posX, mc.player.posY + 0.3, mc.player.posZ);


            mc.player.motionX = 0;
            mc.player.motionY = 0;
            mc.player.motionZ = 0;


        }
    }
}