package xyz.templecheats.templeclient.features.module.modules.world;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.event.EventStageable;
import xyz.templecheats.templeclient.event.events.player.MotionEvent;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.color.impl.GradientShader;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;
import xyz.templecheats.templeclient.util.time.TimerUtil;
import xyz.templecheats.templeclient.util.world.BlockPosWithFacing;

import java.awt.*;

public class Scaffold extends Module {
    /*
     * Settings
     */
    private final BooleanSetting rotate = new BooleanSetting("Rotate", this, true);
    private final BooleanSetting autoSwap = new BooleanSetting("AutoSwap", this, true);
    private final BooleanSetting eChestHolding = new BooleanSetting("eChestHolding", this, true);

    private final BooleanSetting tower = new BooleanSetting("Tower", this, true);
    private final BooleanSetting render = new BooleanSetting("Render", this, true);
    private final DoubleSetting opacity = new DoubleSetting("RenderOpacity", this, 0.0, 1, 0.5);
    private final BooleanSetting fill = new BooleanSetting("Box Fill", this, true);
    private final BooleanSetting outline = new BooleanSetting("Box Outline", this, true);

    /*
     * Variables
     */
    private BlockPosWithFacing currentBlock;
    private TimerUtil timer;

    public Scaffold() {
        super("Scaffold", "Automatically towers with blocks", Keyboard.KEY_NONE, Category.World);

        registerSettings(rotate, autoSwap, eChestHolding, render, tower, opacity, fill, outline);
        timer = new TimerUtil();
    }

    private boolean isBlockValid(Block block) {
        return block.getDefaultState().getMaterial().isSolid();
    }

    private BlockPosWithFacing checkNearBlocks(BlockPos blockPos) {
        if (isBlockValid(mc.world.getBlockState(blockPos.add(0, -1, 0)).getBlock()))
            return new BlockPosWithFacing(blockPos.add(0, -1, 0), EnumFacing.UP);
        else if (isBlockValid(mc.world.getBlockState(blockPos.add(-1, 0, 0)).getBlock()))
            return new BlockPosWithFacing(blockPos.add(-1, 0, 0), EnumFacing.EAST);
        else if (isBlockValid(mc.world.getBlockState(blockPos.add(1, 0, 0)).getBlock()))
            return new BlockPosWithFacing(blockPos.add(1, 0, 0), EnumFacing.WEST);
        else if (isBlockValid(mc.world.getBlockState(blockPos.add(0, 0, 1)).getBlock()))
            return new BlockPosWithFacing(blockPos.add(0, 0, 1), EnumFacing.NORTH);
        else if (isBlockValid(mc.world.getBlockState(blockPos.add(0, 0, -1)).getBlock()))
            return new BlockPosWithFacing(blockPos.add(0, 0, -1), EnumFacing.SOUTH);

        return null;
    }
    private BlockPosWithFacing checkNearBlocksExtended(BlockPos blockPos) { // TODO FUCKING OPTIMIZE!!!!!!
        BlockPosWithFacing ret = null;

        ret = checkNearBlocks(blockPos);
        if (ret != null) return ret;

        ret = checkNearBlocks(blockPos.add(-1, 0, 0));
        if (ret != null) return ret;

        ret = checkNearBlocks(blockPos.add(1, 0, 0));
        if (ret != null) return ret;

        ret = checkNearBlocks(blockPos.add(0, 0, 1));
        if (ret != null) return ret;

        ret = checkNearBlocks(blockPos.add(0, 0, -1));
        if (ret != null) return ret;

        ret = checkNearBlocks(blockPos.add(-2, 0, 0));
        if (ret != null) return ret;

        ret = checkNearBlocks(blockPos.add(2, 0, 0));
        if (ret != null) return ret;

        ret = checkNearBlocks(blockPos.add(0, 0, 2));
        if (ret != null) return ret;

        ret = checkNearBlocks(blockPos.add(0, 0, -2));
        if (ret != null) return ret;

        ret = checkNearBlocks(blockPos.add(0, -1, 0));
        BlockPos blockPos2 = blockPos.add(0, -1, 0);

        if (ret != null) return ret;

        ret = checkNearBlocks(blockPos2.add(1, 0, 0));
        if (ret != null) return ret;

        ret = checkNearBlocks(blockPos2.add(-1, 0, 0));
        if (ret != null) return ret;

        ret = checkNearBlocks(blockPos2.add(0, 0, 1));
        if (ret != null) return ret;

        return checkNearBlocks(blockPos2.add(0, 0, -1));
    }

    private int findBlockToPlace() {
        if (mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock) {
            if (isBlockValid(((ItemBlock) mc.player.getHeldItemMainhand().getItem()).getBlock()))
                return mc.player.inventory.currentItem;
        }

        int n = 0;
        int n2 = 0;

        while (true) {
            if (n2 >= 9) break;

            if (mc.player.inventory.getStackInSlot(n).getCount() != 0) {
                if (mc.player.inventory.getStackInSlot(n).getItem() instanceof ItemBlock) {
                    if (!eChestHolding.booleanValue() ||
                            (eChestHolding.booleanValue() && !mc.player.inventory.getStackInSlot(n).getItem().equals(Item.getItemFromBlock(Blocks.ENDER_CHEST)))) {
                        if (isBlockValid(((ItemBlock) mc.player.inventory.getStackInSlot(n).getItem()).getBlock()))
                            return n;
                    }
                }
            }

            n2 = ++n;
        }

        return -1;
    }

    private boolean blockCheck(int itemnum) {
        Item item = mc.player.inventory.getStackInSlot(itemnum).getItem();

        if (item instanceof ItemBlock) {
            Vec3d vec3d = mc.player.getPositionVector();
            Block block = ((ItemBlock) item).getBlock();

            return mc.world.rayTraceBlocks(vec3d, vec3d.add(0.0, -block.getDefaultState().getSelectedBoundingBox(mc.world, BlockPos.ORIGIN).maxY, 0.0), false, true, false) == null;
        }

        return false;
    }

    private int countValidBlocks() {
        int n = 36;
        int n2 = 0;

        while (true) {
            if (n >= 45) break;

            if (mc.player.inventoryContainer.getSlot(n).getHasStack()) {
                ItemStack itemStack = mc.player.inventoryContainer.getSlot(n).getStack();
                if (itemStack.getItem() instanceof ItemBlock) {
                    if (isBlockValid(((ItemBlock) itemStack.getItem()).getBlock()))
                        n2 += itemStack.getCount();
                }
            }

            n++;
        }

        return n2;
    }

    private Vec3d getEyePosition() {
        return new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
    }

    private float[] getRotations(BlockPos blockPos, EnumFacing enumFacing) {
        Vec3d vec3d = new Vec3d((double) blockPos.getX() + 0.5, mc.world.getBlockState(blockPos).getSelectedBoundingBox(mc.world, blockPos).maxY - 0.01, (double) blockPos.getZ() + 0.5);
        vec3d = vec3d.add(new Vec3d(enumFacing.getDirectionVec()).scale(0.5));

        Vec3d vec3d2 = getEyePosition();

        double d = vec3d.x - vec3d2.x;
        double d2 = vec3d.y - vec3d2.y;
        double d3 = vec3d.z - vec3d2.z;
        double d4 = d;
        double d5 = d3;
        double d6 = Math.sqrt(d4 * d4 + d5 * d5);

        float f = (float)(Math.toDegrees(Math.atan2(d3, d)) - 90.0f);
        float f2 = (float)(-Math.toDegrees(Math.atan2(d2, d6)));

        float[] ret = new float[2];
        ret[0] = mc.player.rotationYaw + MathHelper.wrapDegrees((float)(f - mc.player.rotationYaw));
        ret[1] = mc.player.rotationPitch + MathHelper.wrapDegrees((float)(f2 - mc.player.rotationPitch));

        return ret;
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent e) {
        if (render.booleanValue() && currentBlock != null) {
            GradientShader.setup((float) opacity.doubleValue());
            if (fill.booleanValue())
                RenderUtil.boxShader(currentBlock.blockPos);
            if (outline.booleanValue())
                RenderUtil.outlineShader(currentBlock.blockPos);

            GradientShader.finish();
        }
    }
    @Listener
    public void onMotionUpdate(MotionEvent event) {
        if (event.getStage() != EventStageable.EventStage.PRE) {
            return;
        }

        if (mc.player.isSneaking() || countValidBlocks() <= 0 || mc.player.posY > 256) {
            this.currentBlock = null;
            return;
        }

        BlockPos blockPos = new BlockPos(mc.player.posX, mc.player.posY - 1.0, mc.player.posZ);
        if (!mc.world.getBlockState(blockPos).getMaterial().isReplaceable()) {
            return;
        }

        this.currentBlock = this.checkNearBlocksExtended(blockPos);
        if (this.currentBlock == null) {
            return;
        }

        if (rotate.booleanValue()) {
            float[] rotations = getRotations(currentBlock.blockPos, currentBlock.enumFacing);
            event.setYaw(rotations[0]);
            event.setPitch(rotations[1]);
        }

        if (tower.booleanValue() && mc.player.movementInput.jump && mc.player.moveForward == 0.0f && mc.player.moveStrafing == 0.0f) {
            handleTowerMovement();
        }

        placeBlockUnderPlayer();
    }

    private void handleTowerMovement() {
        mc.player.setVelocity(0.0, 0.42, 0.0);
        if (timer.hasReached(1500)) {
            mc.player.motionY = -0.28;
            timer.reset();
        }
    }

    private void placeBlockUnderPlayer() {
        int originalSlot = mc.player.inventory.currentItem;
        if (autoSwap.booleanValue()) {
            int blockSlot = findBlockToPlace();
            if (blockSlot != -1 && blockSlot != originalSlot) {
                mc.player.inventory.currentItem = blockSlot;
                mc.player.connection.sendPacket(new CPacketHeldItemChange(blockSlot));
            }
        }

        BlockPos blockPos = currentBlock.blockPos;
        boolean actionResult = mc.world.getBlockState(blockPos).getBlock().onBlockActivated(mc.world, blockPos, mc.world.getBlockState(blockPos), mc.player, EnumHand.MAIN_HAND, EnumFacing.DOWN, 0.0f, 0.0f, 0.0f);

        if (actionResult) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
        }

        mc.playerController.processRightClickBlock(mc.player, mc.world, blockPos, currentBlock.enumFacing, new Vec3d(blockPos.getX() + Math.random(), mc.world.getBlockState(blockPos).getSelectedBoundingBox(mc.world, blockPos).maxY - 0.01, blockPos.getZ() + Math.random()), EnumHand.MAIN_HAND);
        mc.player.swingArm(EnumHand.MAIN_HAND);

        if (actionResult) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        }

        if (autoSwap.booleanValue() && originalSlot != mc.player.inventory.currentItem) {
            mc.player.inventory.currentItem = originalSlot;
            mc.player.connection.sendPacket(new CPacketHeldItemChange(originalSlot));
        }
    }
}