package xyz.templecheats.templeclient.features.modules.movement;

import xyz.templecheats.templeclient.features.modules.Module;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class ClickTP extends Module {
    public ClickTP() {
        super("ClickTP", Keyboard.KEY_NONE, Category.MOVEMENT);
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        if (isEnabled()) {
            // Get the player's position
            BlockPos playerPos = event.getEntityPlayer().getPosition();

            // Get the target block's position (adjust as needed)
            BlockPos targetPos = event.getPos();

            // Calculate the distance between player and target
            double distance = playerPos.distanceSq(targetPos);

            // Check if the player is within a 3-block radius
            if (distance <= 9) {
                // Teleport the player to the target position
                event.getEntityPlayer().setPositionAndUpdate(targetPos.getX() + 0.5, targetPos.getY() + 1, targetPos.getZ() + 0.5);
            }
        }
    }
}
