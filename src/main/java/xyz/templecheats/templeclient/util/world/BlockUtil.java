package xyz.templecheats.templeclient.util.world;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import xyz.templecheats.templeclient.util.Globals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BlockUtil implements Globals {

    public static final List<Block> resistantBlocks = Arrays.asList(
            Blocks.OBSIDIAN,
            Blocks.ANVIL,
            Blocks.ENCHANTING_TABLE,
            Blocks.ENDER_CHEST,
            Blocks.BEACON
    );

    public static final List<Block> unbreakableBlocks = Arrays.asList(
            Blocks.BEDROCK,
            Blocks.COMMAND_BLOCK,
            Blocks.CHAIN_COMMAND_BLOCK,
            Blocks.BARRIER,
            Blocks.PORTAL
    );

    public enum Resistance {
        REPLACEABLE,
        BREAKABLE,
        RESISTANT,
        UNBREAKABLE,
        NONE
    }

    private static final Minecraft mc = Minecraft.getMinecraft();

    /****************************************************************
     *                  Methods for Block Resistance
     ****************************************************************/

    public static boolean isBreakable(BlockPos position) {
        return !getResistance(position).equals(Resistance.UNBREAKABLE);
    }

    public static Resistance getResistance(BlockPos position) {
        Block block = mc.world.getBlockState(position).getBlock();
        if (block != null) {
            if (resistantBlocks.contains(block)) {
                return Resistance.RESISTANT;
            } else if (unbreakableBlocks.contains(block)) {
                return Resistance.UNBREAKABLE;
            } else if (block.getDefaultState().getMaterial().isReplaceable()) {
                return Resistance.REPLACEABLE;
            } else {
                return Resistance.BREAKABLE;
            }
        }
        return Resistance.NONE;
    }

    /****************************************************************
     *                  Position and Distance Methods
     ****************************************************************/

    public static BlockPos getPosition() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }

    public static float distance(BlockPos pos) {
        return (float) Math.sqrt(mc.player.getDistanceSq(pos));
    }

    public static boolean is(BlockPos pos, Block block) {
        return mc.world.getBlockState(pos).getBlock().equals(block);
    }

    public static boolean air(final BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR);
    }

    public static List<BlockPos> getBlocksInRadius(final double range) {
        if (mc.player == null) return Collections.emptyList();
        List<BlockPos> positions = new ArrayList<>();

        for (int x = MathHelper.floor(mc.player.posX - range), maxX = MathHelper.ceil(mc.player.posX + range); x < maxX; x++) {
            for (int y = MathHelper.floor(mc.player.posY - range), maxY = MathHelper.ceil(mc.player.posY + range); y < maxY; y++) {
                for (int z = MathHelper.floor(mc.player.posZ - range), maxZ = MathHelper.ceil(mc.player.posZ + range); z < maxZ; z++) {
                    if (mc.player.getDistanceSq(x + 0.5, y + 1, z + 0.5) <= range * range) {
                        positions.add(new BlockPos(x, y, z));
                    }
                }
            }
        }

        return positions;
    }

    /****************************************************************
     *                  GameSense BlockUtil Methods
     ****************************************************************/

    public static IBlockState getState(BlockPos pos) {
        return mc.world.getBlockState(pos);
    }

    public static Block getBlock(BlockPos pos) {
        return getState(pos).getBlock();
    }

    public static Block getBlock(double x, double y, double z) {
        return mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
    }

    public static boolean canBeClicked(BlockPos pos) {
        return getBlock(pos).canCollideCheck(getState(pos), false);
    }

    public static void faceVectorPacketInstant(Vec3d vec, Boolean roundAngles) {
        float[] rotations = getNeededRotations2(vec);
        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rotations[0], roundAngles ? MathHelper.normalizeAngle((int) rotations[1], 360) : rotations[1], mc.player.onGround));
    }

    private static float[] getNeededRotations2(Vec3d vec) {
        Vec3d eyesPos = getEyesPos();

        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

        return new float[]{
                mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw),
                mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - mc.player.rotationPitch)
        };
    }

    public static Vec3d getEyesPos() {
        return new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
    }

    public static EnumFacing getPlaceableSide(BlockPos pos) {
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbour = pos.offset(side);
            if (!mc.world.getBlockState(neighbour).getBlock().canCollideCheck(mc.world.getBlockState(neighbour), false)) {
                continue;
            }
            IBlockState blockState = mc.world.getBlockState(neighbour);
            if (!blockState.getMaterial().isReplaceable()) {
                return side;
            }
        }
        return null;
    }

    public static EnumFacing getPlaceableSideExlude(BlockPos pos, ArrayList<EnumFacing> excluding) {
        for (EnumFacing side : EnumFacing.values()) {
            if (!excluding.contains(side)) {
                BlockPos neighbour = pos.offset(side);
                if (!mc.world.getBlockState(neighbour).getBlock().canCollideCheck(mc.world.getBlockState(neighbour), false)) {
                    continue;
                }
                IBlockState blockState = mc.world.getBlockState(neighbour);
                if (!blockState.getMaterial().isReplaceable()) {
                    return side;
                }
            }
        }
        return null;
    }

    public static Vec3d getCenterOfBlock(double playerX, double playerY, double playerZ) {
        double newX = Math.floor(playerX) + 0.5;
        double newY = Math.floor(playerY);
        double newZ = Math.floor(playerZ) + 0.5;
        return new Vec3d(newX, newY, newZ);
    }
}
