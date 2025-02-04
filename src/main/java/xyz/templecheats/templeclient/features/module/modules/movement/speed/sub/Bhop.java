package xyz.templecheats.templeclient.features.module.modules.movement.speed.sub;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.render.Freecam;
import xyz.templecheats.templeclient.util.world.EntityUtil;

public class Bhop extends Module {
    /****************************************************************
     *                      Variables
     ****************************************************************/
    private int counter = 0;

    public Bhop() {
        super("Bhop", "Automatically makes the player jump", Keyboard.KEY_NONE, Category.Movement, true);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (Freecam.isFreecamActive()) {
            return;
        }
        if (mc.player == null || mc.world == null) return;
        if (mc.player.onGround && EntityUtil.isMoving()) {
            if (counter < 2) {
                counter++;
                return;
            }

            mc.player.jump();
            counter = 0;
        }
    }
}