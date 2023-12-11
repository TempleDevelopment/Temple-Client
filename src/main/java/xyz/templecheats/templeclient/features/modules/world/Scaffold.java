package xyz.templecheats.templeclient.features.modules.world;

import xyz.templecheats.templeclient.features.modules.Module;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
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

                mc.playerController.processRightClickBlock(mc.player, mc.world, playerPos, EnumFacing.UP, mc.player.getLookVec(), EnumHand.MAIN_HAND);
                lastBlockPos = playerPos;
            }
        } else {
            lastBlockPos = null;
        }
    }
}
