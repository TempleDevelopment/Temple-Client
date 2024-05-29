package xyz.templecheats.templeclient.features.module.modules.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.event.EventStageable;
import xyz.templecheats.templeclient.event.events.network.PacketEvent;
import xyz.templecheats.templeclient.event.events.player.MotionEvent;
import xyz.templecheats.templeclient.event.events.player.MoveEvent;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.movement.Safewalk;
import xyz.templecheats.templeclient.util.Globals;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.util.render.shader.impl.GradientShader;
import xyz.templecheats.templeclient.util.rotation.RotationUtil;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Scaffold extends Module {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    private final EnumSetting<BlockMode> blocks = new EnumSetting<>("Blocks", this, BlockMode.Blacklist);
    private final EnumSetting<RotateMode> rotate = new EnumSetting<>("Rotate", this, RotateMode.Instant);
    private final BooleanSetting autoSwap = new BooleanSetting("AutoSwap", this, true);
    private final EnumSetting<TowerMode> tower = new EnumSetting<>("Tower", this, TowerMode.Default);
    private final BooleanSetting safewalk = new BooleanSetting("Safewalk", this, true);
    private final BooleanSetting echestHolding = new BooleanSetting("Echest Holding", this, false);
    private final BooleanSetting render = new BooleanSetting("Render", this, true);
    private final DoubleSetting opacity = new DoubleSetting("RenderOpacity", this, 0.0, 1, 0.5);
    private final BooleanSetting fill = new BooleanSetting("Box Fill", this, true);
    private final BooleanSetting outline = new BooleanSetting("Box Outline", this, true);

    /****************************************************************
     *                      Timer
     ****************************************************************/
    private final Timer timer = new Timer();

    /****************************************************************
     *                      Block Lists
     ****************************************************************/
    private static final List<Block> SCAFFOLD_BLACKLIST = new ArrayList<>();
    private static final List<Block> SCAFFOLD_WHITELIST = new ArrayList<>();

    /****************************************************************
     *                      Current Block Data
     ****************************************************************/
    private Data blockData;

    /****************************************************************
     *                      Misc Variables
     ****************************************************************/
    private float lastYaw, lastPitch;
    private long lastTimeRotating = System.currentTimeMillis(), unused1 = System.currentTimeMillis();
    private int oldHeldItem = -1;
    private final Timer sneakPacketTimer = new Timer();
    private final Queue<Runnable> sneakPacketQueue = new ConcurrentLinkedQueue<>();
    private final WeakHashMap<Integer, Long> antiDesyncBlockPlaceAttempts = new WeakHashMap<>();

    public Scaffold() {
        super("Scaffold", "Automatically towers with blocks", Keyboard.KEY_NONE, Category.World);
        registerSettings(autoSwap, safewalk, echestHolding, blocks, rotate, tower, render, opacity, fill, outline);
        SCAFFOLD_BLACKLIST.addAll(Arrays.asList(Blocks.ANVIL, Blocks.AIR, Blocks.WATER, Blocks.FIRE, Blocks.FLOWING_WATER, Blocks.LAVA, Blocks.FLOWING_LAVA, Blocks.CHEST, Blocks.ANVIL, Blocks.ENCHANTING_TABLE, Blocks.CHEST, Blocks.GRAVEL, Blocks.WEB));
        SCAFFOLD_WHITELIST.addAll(Arrays.asList(Blocks.STONE, Blocks.COBBLESTONE, Blocks.OBSIDIAN));
    }

    @Listener
    public void onMotionUpdate(MotionEvent event) {
        if (getBlockCountHotbar() <= 0 || mc.player.posY > 257) {
            this.blockData = null;
            return;
        }

        if (getBlockCountHotbar() <= 0 || (!this.autoSwap.booleanValue() && !(mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock))) {
            return;
        }

        if (event.getStage().equals(EventStageable.EventStage.PRE)) {
            this.blockData = null;
            if (!mc.player.isSneaking()) {
                final int blockSlot = this.getHotbarSlot();

                if (blockSlot != -1) {
                    final Item item = mc.player.inventory.getStackInSlot(blockSlot).getItem();

                    if (item instanceof ItemBlock) {
                        final Block block = ((ItemBlock) item).getBlock();

                        final boolean fullBlock = block.getDefaultState().isFullBlock();

                        final double offset = fullBlock ? 1 : block.getDefaultState().getCollisionBoundingBox(mc.world, BlockPos.ORIGIN).maxY;

                        BlockPos blockBelow = new BlockPos(mc.player.posX, mc.player.posY - offset, mc.player.posZ);

                        if (mc.world.getBlockState(blockBelow).getMaterial().isReplaceable() && (fullBlock || canPlaceBlock(blockSlot))) {
                            this.blockData = this.getBlockData(blockBelow);
                            if (this.blockData != null && !this.rotate.value().equals(RotateMode.Off)) {
                                final float[] rotations = RotationUtil.getRotations3(this.blockData.position, this.blockData.face);

                                this.blockData.block = block;
                                this.lastTimeRotating = System.currentTimeMillis();
                                event.setYaw(this.lastYaw = rotations[0]);
                                event.setPitch(this.lastPitch = rotations[1]);

                            }
                        } else if (this.rotate.value().equals(RotateMode.Constant) && System.currentTimeMillis() - this.lastTimeRotating <= 1000L) {
                            event.setYaw(this.lastYaw);
                            event.setPitch(this.lastPitch);
                        }
                    }
                }
            }
        } else {

            final Runnable poll = this.sneakPacketQueue.poll();
            if (poll != null) {
                poll.run();
                return;
            }

            if (this.blockData == null) {
                return;
            }

            final BlockPos pos = this.blockData.position;
            final int heldItem = mc.player.inventory.currentItem;
            final int blockSlot = this.getHotbarSlot();
            final ItemStack heldStack = mc.player.inventory.getStackInSlot(blockSlot);
            final boolean fullBlock = heldStack.getItem() instanceof ItemBlock && ((ItemBlock) heldStack.getItem()).getBlock().getDefaultState().isFullBlock();
            final boolean heldStackActivated = heldStack.getItem() instanceof ItemBlock && ((ItemBlock) heldStack.getItem()).getBlock() instanceof BlockEnderChest;
            final boolean activated = mc.world.getBlockState(pos).getBlock().onBlockActivated(mc.world, pos, mc.world.getBlockState(pos), mc.player, EnumHand.MAIN_HAND, EnumFacing.DOWN, 0, 0, 0);
            final boolean tower = !this.tower.value().equals(TowerMode.Off) && mc.player.movementInput.jump && mc.player.moveForward == 0.0f && mc.player.moveStrafing == 0.0f && (!this.tower.value().equals(TowerMode.Strict) || (!activated && !heldStackActivated));

            if ((!(mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock) || !this.canUseBlock(((ItemBlock) mc.player.getHeldItemMainhand().getItem()).getBlock())) && this.autoSwap.booleanValue()) {

                if (blockSlot != -1) {
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem = blockSlot));
                }
            }

            if (tower) {
                mc.player.setVelocity(0, 0.42, 0);
                if (this.timer.passed(1500L)) {
                    mc.player.motionY = -0.28;
                    timer.reset();
                }
            } else {
                this.timer.reset();
            }

            final boolean lastBlockInStack = heldStack.getCount() == 1;

            if (activated) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            }

            mc.playerController.processRightClickBlock(mc.player, mc.world, pos, blockData.face, new Vec3d(pos.getX() + Math.random(), mc.world.getBlockState(pos).getSelectedBoundingBox(mc.world, pos).maxY - 0.01, pos.getZ() + Math.random()), EnumHand.MAIN_HAND);
            mc.player.swingArm(EnumHand.MAIN_HAND);

            if (fullBlock) {
                this.antiDesyncBlockPlaceAttempts.put(mc.player.inventory.currentItem, System.currentTimeMillis());
            }

            if (activated) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            }

            mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem = heldItem));

            if (lastBlockInStack && this.autoSwap.booleanValue()) {
                this.unused1 = System.currentTimeMillis();
            }
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent e) {
        if (render.booleanValue() && blockData != null) {
            GradientShader.setup((float) opacity.doubleValue());
            if (fill.booleanValue())
                RenderUtil.boxShader(blockData.position);
            if (outline.booleanValue())
                RenderUtil.outlineShader(blockData.position);
            GradientShader.finish();
        }
    }

    @Listener
    public void onMove(MoveEvent event) {
        if (this.safewalk.booleanValue()) {
            Safewalk.INSTANCE.safeWalk(event);
        }
    }

    @Listener
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketSetSlot) {
            final SPacketSetSlot packet = (SPacketSetSlot) event.getPacket();
            final int slot = packet.getSlot() - 36;
            if (slot >= 0 && slot < 9 && this.antiDesyncBlockPlaceAttempts.containsKey(slot)) {
                final long time = this.antiDesyncBlockPlaceAttempts.get(slot);
                if (System.currentTimeMillis() - time <= 2000L) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.timer.reset();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.antiDesyncBlockPlaceAttempts.clear();
    }

    private boolean canUseBlock(Block block) {
        switch (this.blocks.value()) {
            case Blacklist:
                return !SCAFFOLD_BLACKLIST.contains(block);
            case Whitelist:
                return SCAFFOLD_WHITELIST.contains(block);
            default:
                return block.getDefaultState().getMaterial().isSolid() || block instanceof BlockCarpet;
        }
    }

    private Data getBlockData(BlockPos pos) {
        final Data bd1 = checkCardinal(pos);
        if (bd1 != null) {
            return bd1;
        }

        final Data bd2 = checkCardinal(pos.add(-1, 0, 0));
        if (bd2 != null) {
            return bd2;
        }

        final Data bd3 = checkCardinal(pos.add(1, 0, 0));
        if (bd3 != null) {
            return bd3;
        }

        final Data bd4 = checkCardinal(pos.add(0, 0, 1));
        if (bd4 != null) {
            return bd4;
        }

        final Data bd5 = checkCardinal(pos.add(0, 0, -1));
        if (bd5 != null) {
            return bd5;
        }

        final Data bd6 = checkCardinal(pos.add(-2, 0, 0));
        if (bd6 != null) {
            return bd6;
        }

        final Data bd7 = checkCardinal(pos.add(2, 0, 0));
        if (bd7 != null) {
            return bd7;
        }

        final Data bd8 = checkCardinal(pos.add(0, 0, 2));
        if (bd8 != null) {
            return bd8;
        }

        final Data bd9 = checkCardinal(pos.add(0, 0, -2));
        if (bd9 != null) {
            return bd9;
        }

        final Data bd10 = checkCardinal(pos.add(0, -1, 0));
        final BlockPos pos10 = pos.add(0, -1, 0);
        if (bd10 != null) {
            return bd10;
        }

        final Data bd11 = checkCardinal(pos10.add(1, 0, 0));
        if (bd11 != null) {
            return bd11;
        }

        final Data bd12 = checkCardinal(pos10.add(-1, 0, 0));
        if (bd12 != null) {
            return bd12;
        }

        final Data bd13 = checkCardinal(pos10.add(0, 0, 1));
        if (bd13 != null) {
            return bd13;
        }

        return checkCardinal(pos10.add(0, 0, -1));
    }

    private Data checkCardinal(BlockPos pos) {
        if (this.canUseBlock(mc.world.getBlockState(pos.add(0, -1, 0)).getBlock())) {
            return new Data(pos.add(0, -1, 0), EnumFacing.UP);
        }
        if (this.canUseBlock(mc.world.getBlockState(pos.add(-1, 0, 0)).getBlock())) {
            return new Data(pos.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (this.canUseBlock(mc.world.getBlockState(pos.add(1, 0, 0)).getBlock())) {
            return new Data(pos.add(1, 0, 0), EnumFacing.WEST);
        }
        if (this.canUseBlock(mc.world.getBlockState(pos.add(0, 0, 1)).getBlock())) {
            return new Data(pos.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (this.canUseBlock(mc.world.getBlockState(pos.add(0, 0, -1)).getBlock())) {
            return new Data(pos.add(0, 0, -1), EnumFacing.SOUTH);
        }
        return null;
    }

    private static class Data {
        public BlockPos position;
        public EnumFacing face;
        public Block block = null;

        public Data(BlockPos position, EnumFacing face) {
            this.position = position;
            this.face = face;
        }
    }

    private int getBlockCountHotbar() {
        int blockCount = 0;
        for (int i = 36; i < 45; ++i) {
            if (Globals.mc.player.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = Globals.mc.player.inventoryContainer.getSlot(i).getStack();
                if (is.getItem() instanceof ItemBlock && this.canUseBlock(((ItemBlock) is.getItem()).getBlock())) {
                    blockCount += is.getCount();
                }
            }
        }
        return blockCount;
    }

    private int getHotbarSlot() {
        if (mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock && this.canUseBlock(((ItemBlock) mc.player.getHeldItemMainhand().getItem()).getBlock())) {
            return mc.player.inventory.currentItem;
        }

        for (int i = 0; i < 9; ++i) {
            if (mc.player.inventory.getStackInSlot(i).getCount() != 0 && (mc.player.inventory.getStackInSlot(i).getItem() instanceof ItemBlock) && (!this.echestHolding.booleanValue() || !mc.player.inventory.getStackInSlot(i).getItem().equals(Item.getItemFromBlock(Blocks.ENDER_CHEST))) && this.canUseBlock(((ItemBlock) mc.player.inventory.getStackInSlot(i).getItem()).getBlock())) {
                return i;
            }
        }
        return -1;
    }

    private boolean canPlaceBlock(int blockSlot) {
        final Item item = mc.player.inventory.getStackInSlot(blockSlot).getItem();

        if (item instanceof ItemBlock) {
            final Block block = ((ItemBlock) item).getBlock();
            final Vec3d posVec = mc.player.getPositionVector();
            final RayTraceResult result = mc.world.rayTraceBlocks(posVec, posVec.add(0, -block.getDefaultState().getSelectedBoundingBox(mc.world, BlockPos.ORIGIN).maxY, 0), false, true, false);
            return result == null;
        }

        return false;
    }

    public class Timer {
        private long time;
        long startTime = System.currentTimeMillis();
        long delay = 0L;
        boolean paused = false;

        public void setDelay(long l) {
            this.delay = l;
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

    private enum BlockMode {
        All,
        Blacklist,
        Whitelist
    }

    private enum RotateMode {
        Off,
        Instant,
        Constant
    }

    private enum TowerMode {
        Default,
        Strict,
        Off
    }
}
