package xyz.templecheats.templeclient.features.module.modules.movement;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;

public class Parkour extends Module {
    /*
     * Settings
     */
    private final BooleanSetting slabs = new BooleanSetting("Slabs", this, true);

    public Parkour() {
        super("Parkour", "Jumps when on the edge of blocks", Keyboard.KEY_NONE, Category.Movement);
        registerSettings(slabs);
    }

    @Override
    public void onUpdate() {
        if (mc.player.onGround && !mc.player.isSneaking() && !mc.player.noClip) {
            BlockPos pos = new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ);
            Block block = mc.world.getBlockState(pos).getBlock();

            if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0D, -0.5D, 0.0D)).isEmpty()) {
                if (block instanceof BlockSlab && !slabs.booleanValue()) {
                    return;
                }
                mc.player.jump();
            }
        }
    }
}