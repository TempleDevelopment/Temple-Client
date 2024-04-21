package xyz.templecheats.templeclient.features.module.modules.misc;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.StringSetting;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;

public class AutoRespawn extends Module {
    /*
     * Variables
     */
    private long deathTime = 0;
    private long commandTime = 0;
    private boolean hasRespawned = false;

    /*
     * Settings
     */
    private final BooleanSetting autoKit = new BooleanSetting("Auto Command", this, false);
    private final StringSetting command = new StringSetting("Command", this, "/kit temple");
    private final IntSetting delay = new IntSetting("Command Delay", this, 1, 10, 5);

    public AutoRespawn() {
        super("AutoRespawn", "Automatically respawn when you die", Keyboard.KEY_NONE, Category.Misc);
        registerSettings(command, autoKit, delay);
    }

    @Override
    public void onUpdate() {
        if (this.isEnabled()) {
            if (Minecraft.getMinecraft().player.isDead) {
                if (deathTime == 0) deathTime = System.currentTimeMillis();
                if (System.currentTimeMillis() - deathTime > 100) {
                    Minecraft.getMinecraft().player.respawnPlayer();
                    commandTime = System.currentTimeMillis();
                    hasRespawned = true;
                    deathTime = 0;
                }
            } else {
                if (autoKit.booleanValue() && hasRespawned && System.currentTimeMillis() - commandTime > delay.intValue() * 1000L) {
                    if (!command.getStringValue().startsWith("/")) return;
                    Minecraft.getMinecraft().player.sendChatMessage(command.getStringValue().replace("_", " ")); //when re-opening mc. the string setting will have a _ instead of space for some reason. this fixes it.
                    commandTime = 0;
                    hasRespawned = false;
                }
                deathTime = 0;
            }
        }
    }
}