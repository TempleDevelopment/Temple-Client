package xyz.templecheats.templeclient.impl.modules.combat;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.api.event.events.player.MotionEvent;
import xyz.templecheats.templeclient.impl.modules.Module;
import xyz.templecheats.templeclient.api.event.EventStageable;
import xyz.templecheats.templeclient.impl.modules.world.Scaffold;


public class Surround extends Module {

    private BlockPos placePos;
    public Surround() {
        super("Surround", "Surrounds you with blocks", Keyboard.KEY_NONE, Category.Combat);
    }

    @Listener
    public void onMotion(MotionEvent event) {
        final EnumHand placeHand;
        if(mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemBlock) {
            placeHand = EnumHand.MAIN_HAND;
        } else if(mc.player.getHeldItem(EnumHand.OFF_HAND).getItem() instanceof ItemBlock) {
            placeHand = EnumHand.MAIN_HAND;
        } else {
            return;
        }

        switch(event.getStage()) {
            case PRE:
                final BlockPos playerPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);

                for (EnumFacing direction : EnumFacing.values()) {
                    if (direction != EnumFacing.UP && direction != EnumFacing.DOWN) {
                        final BlockPos targetPos = playerPos.offset(direction);
                        if (this.canPlaceBlock(targetPos)) {
                            this.placePos = targetPos.down();
                            break;
                        }
                    }
                }

                if(this.placePos != null) {
                    final float[] rotations = Scaffold.rotations(this.placePos);
                    event.setYaw(rotations[0]);
                    event.setPitch(rotations[1]);
                }
                break;
            case POST:
                if(this.placePos != null) {

                    mc.playerController.processRightClickBlock(mc.player, mc.world, this.placePos, EnumFacing.UP, new Vec3d(this.placePos).add(0.5, 1, 0.5), placeHand);
                    this.placePos = null;
                }
                break;
        }
    }
    private boolean canPlaceBlock(BlockPos pos) {
        IBlockState state = mc.world.getBlockState(pos);
        IBlockState stateBelow = mc.world.getBlockState(pos.down());
        boolean canReplace = state.getBlock().isReplaceable(mc.world, pos) || state.getMaterial().isLiquid();
        boolean isSolidBelow = stateBelow.isFullCube() || stateBelow.isOpaqueCube();
        return canReplace && isSolidBelow;
    }
}