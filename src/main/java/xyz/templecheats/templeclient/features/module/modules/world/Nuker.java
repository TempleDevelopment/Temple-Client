package xyz.templecheats.templeclient.features.module.modules.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;

import java.util.Random;

public class Nuker extends Module {
    /****************************************************************
     *                      Variables
     ****************************************************************/
    private static final Random random = new Random();

    public Nuker() {
        super("Nuker", "Destroy blocks around you", Keyboard.KEY_NONE, Category.World);
    }

    @SubscribeEvent
    public void onUpdate(RenderWorldLastEvent e) {
        breakRandomBlocksAroundPlayer();
    }

    private void breakRandomBlocksAroundPlayer() {
        Minecraft mc = Minecraft.getMinecraft();
        int range = 5;

        BlockPos playerPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);

        for (int i = 0; i < 10; i++) {
            int offsetX = random.nextInt(range * 2 + 1) - range;
            int offsetY = random.nextInt(range * 2 + 1) - range;
            int offsetZ = random.nextInt(range * 2 + 1) - range;

            BlockPos targetPos = playerPos.add(offsetX, offsetY, offsetZ);

            IBlockState state = mc.world.getBlockState(targetPos);
            mc.playerController.onPlayerDamageBlock(targetPos, EnumFacing.UP);
            mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }
}