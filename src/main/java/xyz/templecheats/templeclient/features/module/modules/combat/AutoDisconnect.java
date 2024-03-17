package xyz.templecheats.templeclient.features.module.modules.combat;

import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;

public class AutoDisconnect extends Module {
    private final IntSetting healthThreshold = new IntSetting("Health", this, 1, 20, 10);

    private boolean shouldDisconnect = false;

    public AutoDisconnect() {
        super("AutoDisconnect","Automatically disconnects when health is low enough", Keyboard.KEY_NONE, Category.Combat);
        registerSettings(healthThreshold);
    }

    @Override
    public void onUpdate() {
        int healthToDisconnect = healthThreshold.intValue();
        if (mc.player.getHealth() <= healthToDisconnect && !shouldDisconnect) {
            shouldDisconnect = true;
            mc.player.connection.getNetworkManager().closeChannel(new TextComponentString("Disconnected due to low health"));
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
