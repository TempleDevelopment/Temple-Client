package xyz.templecheats.templeclient.features.module.modules.combat;

import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockWeb;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.manager.InventoryManager;
import xyz.templecheats.templeclient.util.misc.Offsets;
import xyz.templecheats.templeclient.util.player.PlacementUtil;
import xyz.templecheats.templeclient.util.player.PlayerUtil;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.util.render.shader.impl.GradientShader;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;
import xyz.templecheats.templeclient.util.time.TimerUtil;
import xyz.templecheats.templeclient.util.world.BlockUtil;

public class Surround extends Module {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    private final EnumSetting<JumpMode> jumpMode = new EnumSetting<>("Jump", this, JumpMode.Continue);
    private final EnumSetting<Pattern> offsetMode = new EnumSetting<>("Pattern", this, Pattern.Normal);
    private final IntSetting delayTicks = new IntSetting("Tick Delay", this, 0, 10, 3);
    private final IntSetting blocksPerTick = new IntSetting("Blocks Per Tick", this, 0, 8, 4);
    private final BooleanSetting rotate = new BooleanSetting("Rotate", this, true);
    private final BooleanSetting centerPlayer = new BooleanSetting("Center Player", this, false);
    private final BooleanSetting sneakOnly = new BooleanSetting("Sneak Only", this, false);
    private final BooleanSetting disableNoBlock = new BooleanSetting("Disable No Obby", this, true);
    private final BooleanSetting offhandObby = new BooleanSetting("Offhand Obby", this, false);
    private final BooleanSetting silent = new BooleanSetting("Silent Switch", this, false);
    private final BooleanSetting render = new BooleanSetting("Render", this, true);
    private final BooleanSetting fill = new BooleanSetting("Box Fill", this, true);
    private final BooleanSetting outline = new BooleanSetting("Box Outline", this, true);
    private final DoubleSetting opacity = new DoubleSetting("RenderOpacity", this, 0.0, 1, 0.5);

    /****************************************************************
     *                      Variables
     ****************************************************************/
    private final TimerUtil delayTimer = new TimerUtil();
    private Vec3d centeredBlock = Vec3d.ZERO;
    private int oldSlot = -1;
    private int offsetSteps = 0;
    private boolean outOfTargetBlock = false;
    private boolean activedOff = false;
    private boolean isSneaking = false;
    private BlockPos currentBlock;
    private boolean finished;

    public Surround() {
        super("Surround", "Automatically surrounds your feet with obsidian", 0, Category.Combat);
        this.registerSettings(
                rotate, centerPlayer, sneakOnly, disableNoBlock, offhandObby, silent,
                delayTicks, blocksPerTick,
                jumpMode, offsetMode, render, fill, outline, opacity);
    }

    @Override
    public void onEnable() {
        PlacementUtil.onEnable();
        if (mc.player == null || mc.world == null) {
            disable();
            return;
        }

        if (centerPlayer.booleanValue() && mc.player.onGround) {
            mc.player.motionX = 0;
            mc.player.motionZ = 0;
        }

        centeredBlock = BlockUtil.getCenterOfBlock(mc.player.posX, mc.player.posY, mc.player.posZ);
        oldSlot = mc.player.inventory.currentItem;
        finished = false;
    }

    @Override
    public void onDisable() {
        PlacementUtil.onDisable();
        if (mc.player == null || mc.world == null) return;

        if (oldSlot != mc.player.inventory.currentItem && oldSlot != -1 && oldSlot != 9) {
            mc.player.inventory.currentItem = oldSlot;
            oldSlot = -1;
        }

        if (isSneaking) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            isSneaking = false;
        }

        if (offhandObby.booleanValue() && AutoTotem.isActive()) {
            AutoTotem.removeObsidian();
            activedOff = false;
        }

        centeredBlock = Vec3d.ZERO;
        outOfTargetBlock = false;
        currentBlock = null;
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) {
            disable();
            return;
        }

        if (sneakOnly.booleanValue() && !mc.player.isSneaking()) {
            return;
        }

        if (!(mc.player.onGround) && !isPlayerInWeb()) {
            switch (jumpMode.value()) {
                case Pause:
                    return;
                case Disable:
                    disable();
                    return;
                default:
                    break;
            }
        }

        if (blocksPerTick.intValue() == blocksPerTick.intValue() && delayTicks.intValue() == 0) {
            blocksPerTick.setValue(blocksPerTick.intValue() / 2);
            delayTicks.setValue(1);
        }

        int targetBlockSlot = InventoryManager.findObsidianSlot(offhandObby.booleanValue(), activedOff);

        if ((outOfTargetBlock || targetBlockSlot == -1) && disableNoBlock.booleanValue()) {
            outOfTargetBlock = true;
            disable();
            return;
        }

        activedOff = true;

        if (centerPlayer.booleanValue() && centeredBlock != Vec3d.ZERO && mc.player.onGround) {
            PlayerUtil.centerPlayer(centeredBlock);
        }

        while (delayTimer.getTimePassed() / 50L >= delayTicks.intValue()) {
            delayTimer.reset();

            int blocksPlaced = 0;

            while (blocksPlaced <= blocksPerTick.intValue()) {
                int maxSteps;
                Vec3d[] offsetPattern;

                switch (offsetMode.value()) {
                    case AntiCity:
                        offsetPattern = Offsets.SURROUND_CITY;
                        maxSteps = Offsets.SURROUND_CITY.length;
                        break;
                    default:
                        offsetPattern = Offsets.SURROUND;
                        maxSteps = Offsets.SURROUND.length;
                        break;
                }

                if (offsetSteps >= maxSteps) {
                    offsetSteps = 0;
                    break;
                }

                BlockPos offsetPos = new BlockPos(offsetPattern[offsetSteps]);
                BlockPos targetPos = new BlockPos(mc.player.getPositionVector()).add(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());

                boolean tryPlacing = true;

                if (mc.player.posY % 1 > 0.2) {
                    targetPos = new BlockPos(targetPos.getX(), targetPos.getY() + 1, targetPos.getZ());
                }

                if (!mc.world.getBlockState(targetPos).getMaterial().isReplaceable()) {
                    tryPlacing = false;
                }

                for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(targetPos))) {
                    if (entity instanceof EntityPlayer) {
                        tryPlacing = false;
                        break;
                    }
                }

                if (tryPlacing && placeBlock(targetPos)) {
                    blocksPlaced++;
                    currentBlock = targetPos;
                }

                offsetSteps++;

                if (isSneaking) {
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                    isSneaking = false;
                }

                if (blocksPlaced == 0 && offsetSteps >= maxSteps) {
                    finished = true;
                }
            }
        }
    }

    private boolean placeBlock(BlockPos pos) {
        EnumHand handSwing = EnumHand.MAIN_HAND;

        int targetBlockSlot = InventoryManager.findObsidianSlot(offhandObby.booleanValue(), activedOff);

        if (targetBlockSlot == -1) {
            outOfTargetBlock = true;
            return false;
        }

        if (targetBlockSlot == 9) {
            activedOff = true;
            if (mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock && ((ItemBlock) mc.player.getHeldItemOffhand().getItem()).getBlock() instanceof BlockObsidian) {
                handSwing = EnumHand.OFF_HAND;
            } else return false;
        }

        if (mc.player.inventory.currentItem != targetBlockSlot && targetBlockSlot != 9) {
            if (silent.booleanValue()) {
                mc.player.connection.sendPacket(new CPacketHeldItemChange(targetBlockSlot));
            } else {
                mc.player.inventory.currentItem = targetBlockSlot;
            }
        }

        boolean placed = PlacementUtil.place(pos, handSwing, rotate.booleanValue(), true);

        if (silent.booleanValue()) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
        }
        return placed;
    }

    private boolean isPlayerInWeb() {
        BlockPos playerPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
        return mc.world.getBlockState(playerPos).getBlock() instanceof BlockWeb || mc.world.getBlockState(playerPos.up()).getBlock() instanceof BlockWeb;
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (render.booleanValue() && currentBlock != null && !finished) {
            GradientShader.setup((float) opacity.doubleValue());
            if (fill.booleanValue())
                RenderUtil.boxShader(currentBlock);
            if (outline.booleanValue())
                RenderUtil.outlineShader(currentBlock);
            GradientShader.finish();
        }
    }

    public enum JumpMode {
        Continue,
        Pause,
        Disable
    }

    public enum Pattern {
        Normal,
        AntiCity
    }
}