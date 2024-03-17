package xyz.templecheats.templeclient.features.module.modules.combat;

import net.minecraft.block.Block;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;
import org.lwjgl.input.Keyboard;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.event.events.player.MotionEvent;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.rotation.RotationUtil;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;
import xyz.templecheats.templeclient.util.time.TimerUtil;


public class Surround extends Module {
    /**
     * Variables
     */
    private BlockPos placePos;
    private EnumFacing placeFace;
    private TimerUtil timer = new TimerUtil();

    /**
     * Setings
     */
    private final BooleanSetting strictDir = new BooleanSetting("Strict Dir", this, false);
    private final DoubleSetting placeDelay = new DoubleSetting("Place Delay", this, 0d, 150d, 10d);

    public Surround() {
        super("Surround", "Surrounds you with blocks", Keyboard.KEY_NONE, Category.Combat);

        registerSettings(strictDir, placeDelay);
    }


    @Listener
    public void onMotion(MotionEvent event) {
        final EnumHand placeHand;
        int newSlot = -1;
        int oldSlot = mc.player.inventory.currentItem;

        ItemStack stack;
        if ((stack = mc.player.getHeldItem(EnumHand.MAIN_HAND)).getItem() instanceof ItemBlock && Block.getBlockFromItem(stack.getItem()) instanceof BlockObsidian) {
            placeHand = EnumHand.MAIN_HAND;
        } else if ((stack = mc.player.getHeldItem(EnumHand.OFF_HAND)).getItem() instanceof ItemBlock && Block.getBlockFromItem(stack.getItem()) instanceof BlockObsidian) {
            placeHand = EnumHand.OFF_HAND;
        } else {
            for (int i = 0; i < 9; i++) {
                stack = mc.player.inventory.getStackInSlot(i);
                if (stack.getItem() instanceof ItemBlock && Block.getBlockFromItem(stack.getItem()) instanceof BlockObsidian) {
                    newSlot = i;
                    break;
                }
            }

            if (newSlot == -1) {
                return;
            }

            placeHand = EnumHand.MAIN_HAND;
        }

        switch (event.getStage()) {
            case PRE:
                final BlockPos playerPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);

                for (EnumFacing direction : EnumFacing.values()) {
                    if (direction == EnumFacing.UP || direction == EnumFacing.DOWN) {
                        continue;
                    }

                    final BlockPos targetPos = playerPos.offset(direction);

                    if (!this.canPlaceBlock(targetPos)) {
                        continue;
                    }

                    for (EnumFacing supportDir : EnumFacing.values()) {
                        final BlockPos supportPos = targetPos.offset(supportDir);

                        if (this.isSupportBlock(supportPos, supportDir.getOpposite())) {
                            this.placePos = supportPos;
                            this.placeFace = supportDir.getOpposite();
                            break;
                        }

                        if (this.canPlaceBlock(supportPos)) {
                            for (EnumFacing extendDir : EnumFacing.values()) {
                                final BlockPos extendPos = supportPos.offset(extendDir);

                                if (this.isSupportBlock(extendPos, extendDir.getOpposite())) {
                                    this.placePos = extendPos;
                                    this.placeFace = extendDir.getOpposite();
                                    break;
                                }
                            }
                        }
                    }

                    if (this.placePos != null) {
                        break;
                    }
                }

                if (this.placePos != null) {
                    final float[] rotations = RotationUtil.rotations(this.placePos);
                    event.setYaw(rotations[0]);
                    event.setPitch(rotations[1]);
                }
                break;
            case POST:
                if (this.placePos == null || !timer.hasReached((long) placeDelay.doubleValue())) {
                    return;
                }

                if (newSlot != -1) {
                    mc.player.inventory.currentItem = newSlot;
                }

                final Vec3i dir = this.placeFace.getDirectionVec();
                final Vec3d vec = new Vec3d(this.placePos).add(dir.getX() / 2.0, dir.getY() / 2.0, dir.getZ() / 2.0);
                mc.playerController.processRightClickBlock(mc.player, mc.world, this.placePos, this.placeFace, vec, placeHand);
                mc.player.swingArm(placeHand);
                this.placePos = null;

                mc.player.inventory.currentItem = oldSlot;
                timer.reset();
                break;
        }
    }

    private boolean canPlaceBlock(BlockPos pos) {
        if (this.isSupportBlock(pos, null)) {
            return false;
        }

        final AxisAlignedBB box = new AxisAlignedBB(
                pos.getX(), pos.getY(), pos.getZ(),
                pos.getX() + 1.0, pos.getY() + 1.0, pos.getZ() + 1.0
        );

        if (!mc.world.getEntitiesWithinAABB(Entity.class, box, Entity::isEntityAlive).isEmpty()) {
            return false;
        }

        final IBlockState state = mc.world.getBlockState(pos);
        return state.getBlock().isReplaceable(mc.world, pos) || state.getMaterial().isLiquid();
    }

    private boolean isSupportBlock(BlockPos pos, EnumFacing face) {
        final IBlockState state = mc.world.getBlockState(pos);
        boolean isSolid = state.isFullCube() || state.isOpaqueCube();

        if (strictDir.booleanValue() && face != null && isSolid) {
            Vec3d eyesPos = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
            Vec3d vec = new Vec3d(pos).add(0.5 * face.getDirectionVec().getX(),
                    0.5 * face.getDirectionVec().getY(),
                    0.5 * face.getDirectionVec().getZ());
            RayTraceResult rayTraceResult = mc.world.rayTraceBlocks(eyesPos, vec, false, false, false);

            if (rayTraceResult != null && rayTraceResult.getBlockPos().equals(pos) && rayTraceResult.sideHit == face) {
                return true;
            }
            return false;
        }

        return isSolid;
    }
}