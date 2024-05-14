package xyz.templecheats.templeclient.util.world;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import xyz.templecheats.templeclient.util.Globals;

import java.util.*;

public class BlockUtil implements Globals {
    public static final List<Block> resistantBlocks = Arrays.asList(
            Blocks.OBSIDIAN,
            Blocks.ANVIL,
            Blocks.ENCHANTING_TABLE,
            Blocks.ENDER_CHEST,
            Blocks.BEACON
    );

    // All blocks that are unbreakable with tools in survival mode
    public static final List<Block> unbreakableBlocks = Arrays.asList(
            Blocks.BEDROCK,
            Blocks.COMMAND_BLOCK,
            Blocks.CHAIN_COMMAND_BLOCK,
            Blocks.END_PORTAL_FRAME,
            Blocks.BARRIER,
            Blocks.PORTAL
    );

    public static boolean isBreakable(BlockPos position) {
        return !getResistance(position).equals(Resistance.UNBREAKABLE);
    }

    public static Resistance getResistance(BlockPos position) {
        Block block = mc.world.getBlockState(position).getBlock();
        if (block != null) {
            if (resistantBlocks.contains(block)) {
                return Resistance.RESISTANT;
            }
            else if (unbreakableBlocks.contains(block)) {
                return Resistance.UNBREAKABLE;
            }
            else if (block.getDefaultState().getMaterial().isReplaceable()) {
                return Resistance.REPLACEABLE;
            }
            else {
                return Resistance.BREAKABLE;
            }
        }
        return Resistance.NONE;
    }

    public static BlockPos getPosition() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }

    public static float distance(BlockPos pos) {
        return (float) Math.sqrt(mc.player.getDistanceSq(pos));
    }
    public static boolean is(BlockPos pos, Block block) {
        return mc.world.getBlockState(pos).getBlock().equals(block);
    }
    public static boolean isPlayerSafe(EntityPlayer entityPlayer) {
        final BlockPos pos = entityPlayer.getPosition();
        if (isNotIntersecting(entityPlayer)) {
            return isImmuneToExplosion(pos.north()) && isImmuneToExplosion(pos.east()) && isImmuneToExplosion(pos.south()) && isImmuneToExplosion(pos.west()) && isImmuneToExplosion(pos.down());
        } else {
            return isIntersectingSafe(entityPlayer);
        }
    }
    public static boolean isNotIntersecting(EntityPlayer entityPlayer) {
        final BlockPos pos = entityPlayer.getPosition();
        final AxisAlignedBB bb = entityPlayer.getEntityBoundingBox();
        return (!air(pos.north()) || !bb.intersects(new AxisAlignedBB(pos.north()))) && (!air(pos.east()) || !bb.intersects(new AxisAlignedBB(pos.east()))) && (!air(pos.south()) || !bb.intersects(new AxisAlignedBB(pos.south()))) && (!air(pos.west()) || !bb.intersects(new AxisAlignedBB(pos.west())));
    }

    public static boolean isIntersectingSafe(EntityPlayer entityPlayer) {
        final BlockPos pos = entityPlayer.getPosition();
        final AxisAlignedBB bb = entityPlayer.getEntityBoundingBox();
        if (air(pos.north()) && bb.intersects(new AxisAlignedBB(pos.north()))) {
            final BlockPos pos1 = pos.north();
            if (!isImmuneToExplosion(pos1.north()) || !isImmuneToExplosion(pos1.east()) || !isImmuneToExplosion(pos1.west()) || !isImmuneToExplosion(pos1.down()))
                return false;
        }
        if (air(pos.east()) && bb.intersects(new AxisAlignedBB(pos.east()))) {
            final BlockPos pos1 = pos.east();
            if (!isImmuneToExplosion(pos1.north()) || !isImmuneToExplosion(pos1.east()) || !isImmuneToExplosion(pos1.south()) || !isImmuneToExplosion(pos1.down()))
                return false;
        }
        if (air(pos.south()) && bb.intersects(new AxisAlignedBB(pos.south()))) {
            final BlockPos pos1 = pos.south();
            if (!isImmuneToExplosion(pos1.east()) || !isImmuneToExplosion(pos1.south()) || !isImmuneToExplosion(pos1.west()) || !isImmuneToExplosion(pos1.down()))
                return false;
        }
        if (air(pos.west()) && bb.intersects(new AxisAlignedBB(pos.west()))) {
            final BlockPos pos1 = pos.west();
            return isImmuneToExplosion(pos1.north()) && isImmuneToExplosion(pos1.south()) && isImmuneToExplosion(pos1.west()) && isImmuneToExplosion(pos1.down());
        }
        return true;
    }
    public static boolean isImmuneToExplosion(final BlockPos pos) {
        return mc.world.getBlockState(pos).getBlockHardness(mc.world, pos) == -1;
    }

    public static boolean air(final BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR);
    }


    public static List < EnumFacing > getPossibleSides(BlockPos pos) {
        ArrayList < EnumFacing > facings = new ArrayList < > ();
        if (mc.world == null || pos == null) {
            return facings;
        }
        for (EnumFacing side: EnumFacing.values()) {
            BlockPos neighbour = pos.offset(side);
            IBlockState blockState = mc.world.getBlockState(neighbour);
            if (!blockState.getBlock().canCollideCheck(blockState, false) || blockState.getMaterial().isReplaceable())
                continue;
            facings.add(side);
        }
        return facings;
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
    public enum Resistance {

        /**
         * Blocks that are able to be replaced by other blocks
         */
        REPLACEABLE,

        /**
         * Blocks that are able to be broken with tools in survival mode
         */
        BREAKABLE,

        /**
         * Blocks that are resistant to explosions
         */
        RESISTANT,

        /**
         * Blocks that are unbreakable with tools in survival mode
         */
        UNBREAKABLE,

        /**
         * Null equivalent
         */
        NONE
    }
}