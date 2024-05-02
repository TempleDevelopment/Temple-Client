package xyz.templecheats.templeclient.util.world;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;
import xyz.templecheats.templeclient.util.Globals;

import java.util.*;

public class BlockUtil implements Globals {
    private static final Vec3i[] hole = new Vec3i[] {
            new Vec3i(-1, 0, 0),
            new Vec3i(1, 0, 0),
            new Vec3i(0, 0, 1),
            new Vec3i(0, 0, -1),
            new Vec3i(0, -1, 0),
    };

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

    public static boolean valid(BlockPos pos, boolean updated) {
        return mc.world.getBlockState(pos.up()).getBlock().equals(Blocks.AIR) &&
                (mc.world.getBlockState(pos.up().up()).getBlock().equals(Blocks.AIR) || updated) &&
                (mc.world.getBlockState(pos).getBlock().equals(Blocks.OBSIDIAN) ||
                        mc.world.getBlockState(pos).getBlock().equals(Blocks.BEDROCK));
    }

    public static BlockPos center() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }
    public static BlockPos getPosition() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }
    public static boolean empty(BlockPos pos) {
        return mc.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(new BlockPos(pos.getX() + 0.5f, pos.getY() + 1.0f, pos.getZ() + 0.5f))).isEmpty();
    }
    public static boolean isReplaceable(BlockPos pos) {
        return mc.world.getBlockState(pos).getMaterial().isReplaceable();
    }

    public static BlockPos getPosition(EntityPlayer entityPlayer) {
        return new BlockPos(Math.floor(entityPlayer.posX), Math.floor(entityPlayer.posY), Math.floor(entityPlayer.posZ));
    }

    public static boolean hasCrystal(BlockPos pos) {
        return !mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(new BlockPos(pos.getX() + 0.0f, pos.getY() + 1.5f, pos.getZ() + 0.0f))).isEmpty();
    }

    public static float distance(BlockPos pos) {
        return (float) Math.sqrt(mc.player.getDistanceSq(pos));
    }
    public static BlockPos center(BlockPos pos) {
        return pos.add(0.5f, 0.5f, 0.5f);
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

    public static float calculateEntityDamage(final EntityEnderCrystal crystal, final EntityPlayer entityPlayer) {
        return calculatePosDamage(crystal.posX, crystal.posY, crystal.posZ, entityPlayer);
    }

    public static float calculatePosDamage(final BlockPos position, final EntityPlayer entityPlayer) {
        return calculatePosDamage(position.getX() + 0.5, position.getY() + 1.0, position.getZ() + 0.5, entityPlayer);
    }

    @SuppressWarnings("ConstantConditions")
    public static float calculatePosDamage(final double posX, final double posY, final double posZ, final Entity entity) {
        final float doubleSize = 12.0F;
        final double size = entity.getDistance(posX, posY, posZ) / doubleSize;
        final Vec3d vec3d = new Vec3d(posX, posY, posZ);
        final double blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        final double value = (1.0D - size) * blockDensity;
        final float damage = (float)((int)((value * value + value) / 2.0D * 7.0D * doubleSize + 1.0D));
        double finalDamage = 1.0D;

        if (entity instanceof EntityLivingBase) {
            finalDamage = getBlastReduction((EntityLivingBase) entity, getMultipliedDamage(damage), new Explosion(mc.world, null, posX, posY, posZ, 6.0F, false, true));
        }

        return (float) finalDamage;
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

    public static EnumFacing getFirstFacing(BlockPos pos) {
        Iterator < EnumFacing > iterator = BlockUtil.getPossibleSides(pos).iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }

    private static float getMultipliedDamage(final float damage) {
        return damage * (mc.world.getDifficulty().getId() == 0 ? 0.0F : (mc.world.getDifficulty().getId() == 2 ? 1.0F : (mc.world.getDifficulty().getId() == 1 ? 0.5F : 1.5F)));
    }

    public static float getBlastReduction(final EntityLivingBase entity, final float damageI, final Explosion explosion) {
        float damage = damageI;
        final DamageSource ds = DamageSource.causeExplosionDamage(explosion);
        damage = CombatRules.getDamageAfterAbsorb(damage, entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        int k = 0;
        try {
            k = EnchantmentHelper.getEnchantmentModifierDamage(entity.getArmorInventoryList(), ds);
        } catch (Exception ignored) {}
        damage = damage * (1.0F - MathHelper.clamp(k, 0.0F, 20.0F) / 25.0F);

        if (entity.isPotionActive(MobEffects.RESISTANCE)) {
            damage = damage - (damage / 4);
        }

        return damage;
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