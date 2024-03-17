package xyz.templecheats.templeclient.features.module.modules.misc;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;

public class AutoRespawn extends Module {

    private long deathTime = 0;

    public AutoRespawn() {
        super("AutoRespawn","Automatically respawn after death", Keyboard.KEY_NONE, Category.Miscelleaneous);
    }

    @Override
    public void onUpdate() {
        if (this.isEnabled()) {
            if (Minecraft.getMinecraft().player.isDead) {
                if (deathTime == 0) deathTime = System.currentTimeMillis();

                if (System.currentTimeMillis() - deathTime > 100) {
                    Minecraft.getMinecraft().player.respawnPlayer();
                    deathTime = 0;
                }
            } else {
                deathTime = 0;
            }
        }
    }
}
