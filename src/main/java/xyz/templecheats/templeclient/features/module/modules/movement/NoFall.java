package xyz.templecheats.templeclient.features.module.modules.movement;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketPlayer;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;

public class NoFall extends Module {
    /*
     * Settings
     */
    private final DoubleSetting fallDistance = new DoubleSetting("Fall Distance", this, 0d, 3d, 1.5d);

    public NoFall() {
        super("NoFall", "Stops fall damage on some servers", Keyboard.KEY_NONE, Module.Category.Movement);

        registerSettings(fallDistance);
    }

    @Override
    public void onUpdate() {
        if (mc.player instanceof EntityPlayerSP) {
            EntityPlayerSP player = mc.player;
            if (player.fallDistance > fallDistance.floatValue()) {
                player.fallDistance = 0;
                CPacketPlayer packet = new CPacketPlayer(true);
                player.connection.sendPacket(packet);
            }
        }
    }
}