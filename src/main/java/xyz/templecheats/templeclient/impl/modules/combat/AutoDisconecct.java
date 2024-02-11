package xyz.templecheats.templeclient.impl.modules.combat;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.impl.modules.Module;
import xyz.templecheats.templeclient.api.setting.Setting;

public class AutoDisconecct extends Module {

    private boolean shouldDisconnect = false;

    public AutoDisconecct() {
        super("AutoDisconnect","Automatically disconnects when health is low enough", Keyboard.KEY_NONE, Category.Combat);
        Setting disconnectHealth = new Setting("Health", this, 10, 1, 20, true);
        TempleClient.settingsManager.rSetting(disconnectHealth);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onUpdate() {
        int healthToDisconnect = TempleClient.settingsManager.getSettingByName(this.getName(), "Health").getValInt();
        if (mc.player.getHealth() <= healthToDisconnect && !shouldDisconnect) {
            shouldDisconnect = true;
            mc.world.sendQuittingDisconnectingPacket();
        }
    }

    @SubscribeEvent
    public void onClientConnectedToServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if (shouldDisconnect) {
            shouldDisconnect = false;
            this.toggled = false;
        }
    }
}
