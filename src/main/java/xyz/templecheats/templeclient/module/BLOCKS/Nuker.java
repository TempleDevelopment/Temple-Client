package xyz.templecheats.templeclient.module.BLOCKS;

import xyz.templecheats.templeclient.module.Module;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.util.Random;

public class Nuker extends Module {
    private final Random random = new Random();

    public Nuker() {
        super("Nuker", Keyboard.KEY_N, Category.BLOCKS); // Adjust the key binding if needed
    }

    @SubscribeEvent
    public void onUpdate(RenderWorldLastEvent e) {
        breakRandomBlocksAroundPlayer();
    }

    private void breakRandomBlocksAroundPlayer() {
        Minecraft mc = Minecraft.getMinecraft();
        int range = 5; // Adjust the range as needed

        BlockPos playerPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);

        for (int i = 0; i < 10; i++) { // Break up to 10 blocks
            int offsetX = random.nextInt(range * 2 + 1) - range;
            int offsetY = random.nextInt(range * 2 + 1) - range;
            int offsetZ = random.nextInt(range * 2 + 1) - range;

            BlockPos targetPos = playerPos.add(offsetX, offsetY, offsetZ);

            IBlockState state = mc.world.getBlockState(targetPos);
            mc.playerController.onPlayerDamageBlock(targetPos, EnumFacing.UP); // You can adjust EnumFacing as needed
            mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }
}
