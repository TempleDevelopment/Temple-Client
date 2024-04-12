package xyz.templecheats.templeclient.features.module.modules.misc;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.StringSetting;

public class AutoRespawn extends Module {
    /*
     * Variables
     */
    private long deathTime = 0;
    private final BooleanSetting autoKit = new BooleanSetting("AutoKit", this, false);
    private final StringSetting command = new StringSetting("Command", this, "/kit temple");

    public AutoRespawn() {
        super("AutoRespawn", "Automatically respawn when you die", Keyboard.KEY_NONE, Category.Misc);
        registerSettings(autoKit, command);
    }

    @Override
    public void onUpdate() {
        if (this.isEnabled()) {
            if (Minecraft.getMinecraft().player.isDead) {
                if (deathTime == 0) deathTime = System.currentTimeMillis();

                if (System.currentTimeMillis() - deathTime > 100) {
                    Minecraft.getMinecraft().player.respawnPlayer();
                    if (autoKit.booleanValue() && deathTime++ >= 20) {
                        if (!command.getStringValue().startsWith("/")) return;
                        Minecraft.getMinecraft().player.sendChatMessage(command.getStringValue());
                    }
                    deathTime = 0;
                }
            } else {
                deathTime = 0;
            }
        }
    }
}