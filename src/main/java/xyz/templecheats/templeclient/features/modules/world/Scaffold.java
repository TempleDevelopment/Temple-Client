package xyz.templecheats.templeclient.features.modules.world;

import xyz.templecheats.templeclient.features.modules.Module;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class Scaffold extends Module {
    public Scaffold() {
        super("Scaffold", Keyboard.KEY_NONE, Category.WORLD);
    }

    private BlockPos lastBlockPos = null;

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock) {

            BlockPos playerPos = new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ);

            if (mc.world.isAirBlock(playerPos) && !playerPos.equals(lastBlockPos)) {
                double diffX = playerPos.getX() + 0.5 - mc.player.posX;
                double diffY = playerPos.getY() + 0.5 - (mc.player.posY + mc.player.getEyeHeight());
                double diffZ = playerPos.getZ() + 0.5 - mc.player.posZ;

                double dist = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);
                float yaw = (float)(MathHelper.atan2(diffZ, diffX) * (180D / Math.PI)) - 90F;
                float pitch = (float)-(MathHelper.atan2(diffY, dist) * (180D / Math.PI));

                mc.player.rotationYaw = yaw;
                mc.player.rotationPitch = pitch;

                mc.playerController.processRightClickBlock(mc.player, mc.world, playerPos, EnumFacing.UP, mc.player.getLookVec(), EnumHand.MAIN_HAND);
                lastBlockPos = playerPos;
            }
        } else {
            lastBlockPos = null;
        }
    }
}
