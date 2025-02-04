package xyz.templecheats.templeclient.features.module.modules.combat;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.render.Freecam;
import xyz.templecheats.templeclient.util.misc.Offsets;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.util.render.shader.impl.GradientShader;
import xyz.templecheats.templeclient.util.rotation.RotationUtil;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SelfTrap extends Module {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    private final BooleanSetting rotate = new BooleanSetting("Rotate", this, true);
    private final IntSetting bps = new IntSetting("Blocks Per Second", this, 0, 10, 5);
    private final IntSetting delay = new IntSetting("Delay", this, 0, 10, 0);
    private final BooleanSetting render = new BooleanSetting("Render", this, true);
    private final BooleanSetting fill = new BooleanSetting("Box Fill", this, true);
    private final BooleanSetting outline = new BooleanSetting("Box Outline", this, true);
    private final DoubleSetting opacity = new DoubleSetting("Opacity", this, 0.0, 1, 0.5);

    /****************************************************************
     *                      Variables
     ****************************************************************/
    private EntityPlayer closestTarget;
    private String lastTickTargetName;
    private int playerHotbarSlot = -1;
    private int lastHotbarSlot = -1;
    private int delayStep = 0;
    private boolean isSneaking = false;
    private int offsetStep = 0;
    private boolean firstRun;
    private boolean finished;
    private BlockPos currentBlock;

    public SelfTrap() {
        super("SelfTrap", "Automatically traps yourself with obsidian", Keyboard.KEY_NONE, Category.Combat);
        registerSettings(rotate, bps, delay, render, fill, outline, opacity);
    }

    public void onEnable() {

        if (mc.player == null) {
            this.disable();
            return;
        }
        firstRun = true;
        playerHotbarSlot = mc.player.inventory.currentItem;
        lastHotbarSlot = -1;
    }

    @Override
    public void onDisable() {

        if (mc.player == null) {
            return;
        }

        if (lastHotbarSlot != playerHotbarSlot && playerHotbarSlot != -1) {
            mc.player.inventory.currentItem = playerHotbarSlot;
        }

        if (isSneaking) {
            mc.player.connection
                    .sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            isSneaking = false;
        }

        playerHotbarSlot = -1;
        lastHotbarSlot = -1;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc.player == null) {
            return;
        }

        if (Freecam.isFreecamActive()) {
            return;
        }

        if (!firstRun) {
            if (delayStep < delay.intValue()) {
                delayStep++;
                return;
            } else {
                delayStep = 0;
            }
        }

        findClosestTarget();

        if (closestTarget == null) {
            if (firstRun) {
                firstRun = false;
            }
            return;
        }

        if (firstRun) {
            firstRun = false;
            lastTickTargetName = closestTarget.getName();

        } else if (!lastTickTargetName.equals(closestTarget.getName())) {
            lastTickTargetName = closestTarget.getName();
            offsetStep = 0;
        }

        List<Vec3d> placeTargets = new ArrayList<>();
        Collections.addAll(placeTargets, Offsets.TRAPSIMPLE);

        int blocksPlaced = 0;

        while (blocksPlaced < bps.intValue()) {

            if (offsetStep >= placeTargets.size()) {
                offsetStep = 0;
                break;
            }

            BlockPos offsetPos = new BlockPos(placeTargets.get(offsetStep));
            BlockPos targetPos = new BlockPos(closestTarget.getPositionVector()).down().add(offsetPos.getX(),
                    offsetPos.getY(), offsetPos.getZ());

            if (placeBlockInRange(targetPos)) {
                blocksPlaced++;
            }

            offsetStep++;
        }

        if (blocksPlaced > 0) {

            if (lastHotbarSlot != playerHotbarSlot && playerHotbarSlot != -1) {
                mc.player.inventory.currentItem = playerHotbarSlot;
                lastHotbarSlot = playerHotbarSlot;
            }

            if (isSneaking) {
                mc.player.connection
                        .sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                isSneaking = false;
            }
        }
    }

    private boolean placeBlockInRange(BlockPos pos) {

        Block block = mc.world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
            return false;
        }

        for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos))) {
            if (!(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb)) {
                return false;
            }
        }

        EnumFacing side = RotationUtil.getPlaceableSide(pos);

        if (side == null) {
            return false;
        }

        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();

        if (!RotationUtil.canBeClicked(neighbour)) {
            return false;
        }

        Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();

        int obiSlot = findObiInHotbar();

        if (obiSlot == -1) {
            this.disable();
        }

        if (lastHotbarSlot != obiSlot) {
            mc.player.inventory.currentItem = obiSlot;
            lastHotbarSlot = obiSlot;
        }

        if (rotate.booleanValue()) {
            RotationUtil.faceVectorPacketInstant(hitVec);
        }

        mc.playerController.processRightClickBlock(mc.player, mc.world, neighbour, opposite, hitVec,
                EnumHand.MAIN_HAND);
        mc.player.swingArm(EnumHand.MAIN_HAND);

        currentBlock = pos;

        return true;
    }

    private int findObiInHotbar() {

        int slot = -1;
        for (int i = 0; i < 9; i++) {

            ItemStack stack = mc.player.inventory.getStackInSlot(i);

            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock)) {
                continue;
            }

            Block block = ((ItemBlock) stack.getItem()).getBlock();
            if (block instanceof BlockObsidian) {
                slot = i;
                break;
            }

        }
        return slot;
    }

    private void findClosestTarget() {
        List<EntityPlayer> playerList = mc.world.playerEntities;
        closestTarget = null;

        for (EntityPlayer target : playerList) {

            if (target == mc.player) {
                closestTarget = target;
            }
        }
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
}