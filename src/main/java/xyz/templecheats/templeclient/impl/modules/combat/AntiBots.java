package xyz.templecheats.templeclient.impl.modules.combat;

import xyz.templecheats.templeclient.impl.modules.Module;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class AntiBots extends Module {
    public AntiBots() {
        super("AntiBots","Tries to prevent bans", Keyboard.KEY_NONE, Category.Combat);
    }

    @SubscribeEvent
    public void onUpdate(RenderWorldLastEvent e) {
        try {
            for (EntityPlayer entityPlayer : mc.world.playerEntities) {
                if (entityPlayer.isInvisible() && entityPlayer != mc.player) {
                    mc.world.removeEntity(entityPlayer);
                }
            }
        } catch (Exception ex) {
        }
    }
}