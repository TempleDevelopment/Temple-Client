package xyz.templecheats.templeclient.features.module.modules.world;

import net.minecraft.block.Block;
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
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.world.BlockPosWithFacing;
import xyz.templecheats.templeclient.util.color.impl.GradientShader;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;

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
    private Timer timer;

    public Scaffold() {
        super("Scaffold", "Automatically places blocks under your feet", Keyboard.KEY_NONE, Category.World);

        registerSettings(rotate, autoSwap, eChestHolding, render, tower, fill, outline, opacity);
        timer = new Timer();
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

    // Dayum Fuck Dis Shiet Code
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

        float f = (float) (Math.toDegrees(Math.atan2(d3, d)) - 90.0f);
        float f2 = (float) (-Math.toDegrees(Math.atan2(d2, d6)));

        float[] ret = new float[2];
        ret[0] = mc.player.rotationYaw + MathHelper.wrapDegrees((float) (f - mc.player.rotationYaw));
        ret[1] = mc.player.rotationPitch + MathHelper.wrapDegrees((float) (f2 - mc.player.rotationPitch));

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

    // didnt even bother tbh
    @Listener
    public void onMotionUpdate(MotionEvent event) {
        {
            block31:
            {
                BlockPos blockPos;
                Scaffold scaffold;
                int n;
                block37:
                {
                    block36:
                    {
                        block35:
                        {
                            block34:
                            {
                                block33:
                                {
                                    block30:
                                    {
                                        BlockPos blockPos2;
                                        block32:
                                        {
                                            block29:
                                            {
                                                block28:
                                                {
                                                    block27:
                                                    {
                                                        block26:
                                                        {
                                                            if (this.countValidBlocks() <= 0) break block26;
                                                            if (Double.compare(mc.player.posY, 257.0) <= 0)
                                                                break block27;
                                                        }
                                                        this.currentBlock = null;
                                                        return;
                                                    }
                                                    if (this.countValidBlocks() <= 0) break block28;
                                                    if (autoSwap.booleanValue()) break block29;
                                                    if (mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock) break block29;
                                                }
                                            }
                                            if (event.getStage() != EventStageable.EventStage.PRE) break block30;
                                            this.currentBlock = null;
                                            if (mc.player.isSneaking()) break block31;
                                            int n2 = this.findBlockToPlace();
                                            if (n2 == -1) break block31;
                                            Item item = mc.player.inventory.getStackInSlot(n2).getItem();
                                            if (!(item instanceof ItemBlock)) break block31;
                                            Block block = ((ItemBlock) item).getBlock();
                                            boolean bl = block.getDefaultState().isFullBlock();
                                            double d = bl ? 1.0 : 0.01;
                                            blockPos2 = new BlockPos(mc.player.posX, mc.player.posY - d, mc.player.posZ);
                                            if (!mc.world.getBlockState(blockPos2).getMaterial().isReplaceable())
                                                break block31;
                                            if (bl) break block32;
                                            if (!blockCheck(n2)) break block31;
                                        }
                                        Scaffold scaffold2 = this;
                                        scaffold2.currentBlock = this.checkNearBlocksExtended(blockPos2);
                                        if (scaffold2.currentBlock != null) {
                                            if (this.rotate.booleanValue()) {
                                                float[] rotations = getRotations(currentBlock.blockPos, currentBlock.enumFacing);
                                                event.setYaw(rotations[0]);
                                                event.setPitch(rotations[1]);
                                                return;
                                            }
                                        }
                                        break block31;
                                    }
                                    if (this.currentBlock == null) break block31;
                                    n = mc.player.inventory.currentItem;
                                    if (!(mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock))
                                        break block33;
                                    if (this.isBlockValid(((ItemBlock) mc.player.getHeldItemMainhand().getItem()).getBlock()))
                                        break block34;
                                }
                                if (autoSwap.booleanValue()) {
                                    int n3 = this.findBlockToPlace();
                                    if (n3 != -1) {
                                        mc.player.inventory.currentItem = n3;
                                        mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
                                    }
                                }
                            }
                            if (!mc.player.movementInput.jump) break block35;
                            if (mc.player.moveForward != 0.0f) break block35;
                            if (mc.player.moveStrafing != 0.0f) break block35;
                            if (!tower.booleanValue()) break block35;
                            mc.player.setVelocity(0.0, 0.42, 0.0);

                            if (!timer.passed(1500)) break block36;
                            mc.player.motionY = -0.28;
                            Scaffold scaffold3 = this;
                            scaffold = scaffold3;
                            timer.reset();
                            break block37;
                        }
                        timer.reset();
                    }
                    scaffold = this;
                }

                System.out.println("Watafak");
                BlockPos blockPos3 = blockPos = scaffold.currentBlock.blockPos;
                boolean bl = mc.world.getBlockState(blockPos).getBlock().onBlockActivated(mc.world, blockPos3, mc.world.getBlockState(blockPos3), mc.player, EnumHand.MAIN_HAND, EnumFacing.DOWN, 0.0f, 0.0f, 0.0f);
                if (bl) {
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                }
                mc.playerController.processRightClickBlock(mc.player, mc.world, blockPos, this.currentBlock.enumFacing, new Vec3d((double) blockPos.getX() + Math.random(), mc.world.getBlockState((BlockPos) blockPos).getSelectedBoundingBox((World) mc.world, (BlockPos) blockPos).maxY - 0.01, (double) blockPos.getZ() + Math.random()), EnumHand.MAIN_HAND);
                mc.player.swingArm(EnumHand.MAIN_HAND);
                if (bl) {
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                }
                mc.player.inventory.currentItem = n;
                mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
            }
        }
    }

    public class Timer {
        private long time;
        long startTime = System.currentTimeMillis();
        long delay = 0L;
        boolean paused = false;

        public boolean isPassed() {
            return !this.paused && System.currentTimeMillis() - this.startTime >= this.delay;
        }

        public void setDelay(long l) {
            this.delay = l;
        }

        public long getTimePassed() {
            return System.currentTimeMillis() - this.time;
        }


        public Timer() {
            this.time = -1L;
        }

        public final boolean passed(final long delay) {
            return passed(delay, false);
        }

        public boolean passed(final long delay, final boolean reset) {
            if (reset) this.reset();
            return System.currentTimeMillis() - this.time >= delay;
        }

        public final void reset() {
            this.time = System.currentTimeMillis();
            this.startTime = System.currentTimeMillis();
        }
    }
}