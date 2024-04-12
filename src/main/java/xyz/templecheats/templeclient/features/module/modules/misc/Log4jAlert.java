package xyz.templecheats.templeclient.features.module.modules.misc;

import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;

public class Log4jAlert extends Module {
    /*
     * Settings
     */
    private final BooleanSetting DisconnectOnReceive = new BooleanSetting("Disconnect", this, false);

    public Log4jAlert() {
        super("Log4jAlert", "Alerts you when someone sends a log4j exploit in chat", Keyboard.KEY_NONE, Category.Misc);
        this.registerSettings(DisconnectOnReceive);
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        String text = event.getMessage().getUnformattedText();
        if (text.contains("${") || text.contains("$<") || text.contains("$:-") || text.contains("jndi:ldap") || text.contains("$(")) {
            event.setCanceled(true);
            String playerName = text.split(":")[0];
            mc.player.sendMessage(new TextComponentString(TextFormatting.RED + "[TempleClient] " + TextFormatting.WHITE + playerName + " used log4j exploit"));
            if (DisconnectOnReceive.booleanValue()) {
                mc.player.connection.getNetworkManager().closeChannel(new TextComponentString("Disconnected due to log4j exploit"));
            }
        }
    }

    public boolean isDisconnectOnReceive() {
        return DisconnectOnReceive.booleanValue();
    }

    public void setDisconnectOnReceive(boolean disconnectOnReceive) {
        DisconnectOnReceive.setValue(disconnectOnReceive);
    }
}