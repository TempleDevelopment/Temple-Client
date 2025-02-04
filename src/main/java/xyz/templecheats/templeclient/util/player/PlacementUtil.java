/*
 * This PlacementUtil was made by GameSense, and was modified.
 */
package xyz.templecheats.templeclient.util.player;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import xyz.templecheats.templeclient.features.module.modules.combat.AutoCrystal;
import xyz.templecheats.templeclient.manager.InventoryManager;
import xyz.templecheats.templeclient.manager.ModuleManager;
import xyz.templecheats.templeclient.util.world.BlockUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class PlacementUtil {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static int placementConnections = 0;
    private static boolean isSneaking = false;

    /****************************************************************
     *                  Initialization Methods
     ****************************************************************/

    public static void onEnable() {
        placementConnections++;
    }

    public static void onDisable() {
        placementConnections--;
        if (placementConnections == 0) {
            if (isSneaking) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                isSneaking = false;
            }
        }
    }

    /****************************************************************
     *                  Block Placement Methods
     ****************************************************************/

    public static boolean place(BlockPos blockPos, EnumHand hand, boolean rotate) {
        return placeBlock(blockPos, hand, rotate, true, null);
    }

    public static boolean place(BlockPos blockPos, EnumHand hand, boolean rotate, ArrayList<EnumFacing> forceSide) {
        return placeBlock(blockPos, hand, rotate, true, forceSide);
    }

    public static boolean place(BlockPos blockPos, EnumHand hand, boolean rotate, boolean checkAction) {
        return placeBlock(blockPos, hand, rotate, checkAction, null);
    }

    public static boolean placeBlock(BlockPos blockPos, EnumHand hand, boolean rotate, boolean checkAction, ArrayList<EnumFacing> forceSide) {
        EntityPlayerSP player = mc.player;
        WorldClient world = mc.world;
        PlayerControllerMP playerController = mc.playerController;

        if (player == null || world == null || playerController == null) return false;
        if (!world.getBlockState(blockPos).getMaterial().isReplaceable()) return false;

        EnumFacing side = forceSide != null ? BlockUtil.getPlaceableSideExlude(blockPos, forceSide) : BlockUtil.getPlaceableSide(blockPos);
        if (side == null) return false;

        BlockPos neighbour = blockPos.offset(side);
        EnumFacing opposite = side.getOpposite();
        if (!BlockUtil.canBeClicked(neighbour)) return false;

        Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = world.getBlockState(neighbour).getBlock();

        if (rotate) {
            BlockUtil.faceVectorPacketInstant(hitVec, true);
        }

        boolean stoppedAC = false;

        if (ModuleManager.getModule(AutoCrystal.class).isEnabled()) {
            AutoCrystal.stopAC = true;
            stoppedAC = true;
        }

        EnumActionResult action = playerController.processRightClickBlock(player, world, neighbour, opposite, hitVec, hand);
        if (!checkAction || action == EnumActionResult.SUCCESS) {
            player.swingArm(hand);
            setRightClickDelayTimer(4);
        }

        if (stoppedAC) {
            AutoCrystal.stopAC = false;
        }

        return action == EnumActionResult.SUCCESS;
    }

    /****************************************************************
     *                  Utility Methods
     ****************************************************************/

    private static void setRightClickDelayTimer(int value) {
        try {
            Field rightClickDelayTimerField = Minecraft.class.getDeclaredField("rightClickDelayTimer");
            rightClickDelayTimerField.setAccessible(true);
            rightClickDelayTimerField.set(mc, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
