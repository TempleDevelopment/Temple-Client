package xyz.templecheats.templeclient.features.module.modules.combat;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;
import xyz.templecheats.templeclient.util.world.HoleUtil;

import java.util.HashMap;

public class Anchor extends Module {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    private final BooleanSetting guarantee = new BooleanSetting("Guarantee Hole", this, true);
    private final IntSetting activateHeight = new IntSetting("Activate Height", this, 1, 5, 2);

    /****************************************************************
     *                      Variables
     ****************************************************************/
    private BlockPos playerPos;

    public Anchor() {
        super("Anchor", "Stops your movement when you are over a hole", Keyboard.KEY_NONE, Category.Combat);
        registerSettings(guarantee, activateHeight);
    }

    @Override
    public void onUpdate() {
        if (mc.player == null) {
            return;
        }

        if (mc.player.posY < 0) {
            return;
        }

        double blockX = Math.floor(mc.player.posX);
        double blockZ = Math.floor(mc.player.posZ);

        double offsetX = Math.abs(mc.player.posX - blockX);
        double offsetZ = Math.abs(mc.player.posZ - blockZ);

        if (guarantee.booleanValue() && (offsetX < 0.3f || offsetX > 0.7f || offsetZ < 0.3f || offsetZ > 0.7f)) {
            return;
        }

        playerPos = new BlockPos(blockX, mc.player.posY, blockZ);

        if (mc.world.getBlockState(playerPos).getBlock() != Blocks.AIR) {
            return;
        }

        BlockPos currentBlock = playerPos.down();
        for (int i = 0; i < activateHeight.intValue(); i++) {
            currentBlock = currentBlock.down();
            if (mc.world.getBlockState(currentBlock).getBlock() != Blocks.AIR) {
                HashMap<HoleUtil.BlockOffset, HoleUtil.BlockSafety> sides = HoleUtil.getUnsafeSides(currentBlock.up());
                sides.entrySet().removeIf(entry -> entry.getValue() == HoleUtil.BlockSafety.RESISTANT);
                if (sides.size() == 0) {
                    mc.player.motionX = 0f;
                    mc.player.motionZ = 0f;
                }
            }
        }
    }
}
