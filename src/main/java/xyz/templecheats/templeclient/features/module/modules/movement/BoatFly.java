package xyz.templecheats.templeclient.features.module.modules.movement;

import net.minecraft.entity.item.EntityBoat;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;

public class BoatFly extends Module {
    public BoatFly() {
        super("BoatFly", "Allows flying in a boat", Keyboard.KEY_NONE, Category.Movement);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (mc.player == null || mc.world == null) return;
        if (mc.player.getRidingEntity() instanceof EntityBoat) {
            EntityBoat boat = (EntityBoat) mc.player.getRidingEntity();
            boat.motionY = mc.gameSettings.keyBindJump.isKeyDown() ? 1 : 0;
        }
    }
}
