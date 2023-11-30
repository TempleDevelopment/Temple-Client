package xyz.templecheats.templeclient.features.modules.combat;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.modules.Module;

public class AutoCrystal extends Module {
    public AutoCrystal() {
        super("AutoCrystal", Keyboard.KEY_NONE, Category.COMBAT);
    }

    @Override
    public void onUpdate() {
        for (Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityEnderCrystal && mc.player.getDistance(entity) <= 7) {
                mc.playerController.attackEntity(mc.player, entity);
                mc.player.swingArm(EnumHand.MAIN_HAND);
                placeCrystalIfNoneNearby();
            }
        }
    }

    private void placeCrystalIfNoneNearby() {
        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos blockPos = mc.player.getPosition().offset(facing);
            if (canPlaceCrystal(blockPos)) {
                mc.playerController.processRightClickBlock(mc.player, mc.world, blockPos, facing.getOpposite(), Vec3d.ZERO, EnumHand.MAIN_HAND);
                break;
            }
        }
    }

    private boolean canPlaceCrystal(BlockPos pos) {
        Block block = mc.world.getBlockState(pos).getBlock();
        Block blockBelow = mc.world.getBlockState(pos.down()).getBlock();

        ItemStack stack = mc.player.getHeldItem(EnumHand.MAIN_HAND);
        if (!stack.isEmpty() && stack.getItem() == Items.END_CRYSTAL) {
            return block instanceof BlockAir && blockBelow == Blocks.BEDROCK;
        }

        return false;
    }
}
