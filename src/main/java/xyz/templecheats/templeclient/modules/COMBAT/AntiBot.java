package xyz.templecheats.templeclient.modules.COMBAT;

import xyz.templecheats.templeclient.modules.Module;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class AntiBot extends Module {
    public AntiBot() {
        super("AntiBots", Keyboard.KEY_NONE, Category.COMBAT);
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