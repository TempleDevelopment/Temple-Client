package xyz.templecheats.templeclient.impl.modules.movement;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.CPacketPlayer;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.api.setting.Setting;
import xyz.templecheats.templeclient.impl.modules.Module;

public class NoFall extends Module{
    private Setting fallDistanceSetting;

    public NoFall() {
        super("NoFall","Prevents fall damage", Keyboard.KEY_NONE, Module.Category.Movement);
        this.fallDistanceSetting = new Setting("Fall Distance", this, 1.5F, 0.0F, 3.0F, true);
        TempleClient.settingsManager.rSetting(fallDistanceSetting);
    }

    @Override
    public void onUpdate() {
        if (mc.player instanceof EntityPlayerSP) {
            EntityPlayerSP player = (EntityPlayerSP) mc.player;
            if (player.fallDistance > fallDistanceSetting.getValDouble()) {
                player.fallDistance = 0;
                CPacketPlayer packet = new CPacketPlayer(true);
                player.connection.sendPacket(packet);
            }
        }
    }
}