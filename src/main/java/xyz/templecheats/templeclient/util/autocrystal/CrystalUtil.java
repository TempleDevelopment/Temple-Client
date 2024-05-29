package xyz.templecheats.templeclient.util.autocrystal;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import xyz.templecheats.templeclient.features.module.modules.combat.AutoCrystal;
import xyz.templecheats.templeclient.util.world.EntityUtil;

import java.util.List;
import java.util.stream.Collectors;

public class CrystalUtil {

    /****************************************************************
     *                      Constants
     ****************************************************************/

    private static final Minecraft mc = Minecraft.getMinecraft();

    /****************************************************************
     *                      Crystal Placement Methods
     ****************************************************************/

    /**
     * Determines if a crystal can be placed at the given block position.
     *
     * @param blockPos      The block position to check.
     * @param server        The server version to consider.
     * @param ignoreCrystals Whether to ignore existing crystals in the area.
     * @return True if a crystal can be placed, false otherwise.
     */
    public static boolean canPlaceCrystal(BlockPos blockPos, AutoCrystal.Server server, boolean ignoreCrystals) {
        if (notValidBlock(mc.world.getBlockState(blockPos).getBlock())) return false;

        BlockPos posUp = blockPos.up();

        if (server != AutoCrystal.Server.OneTwelve) {
            if (!mc.world.isAirBlock(posUp)) return false;
        } else {
            if (notValidMaterial(mc.world.getBlockState(posUp).getMaterial()) ||
                    notValidMaterial(mc.world.getBlockState(posUp.up()).getMaterial())) {
                return false;
            }
        }

        AxisAlignedBB box = new AxisAlignedBB(
                posUp.getX(), posUp.getY(), posUp.getZ(),
                posUp.getX() + 1.0, posUp.getY() + (server == AutoCrystal.Server.Crystalpvp_cc ? 1.0 : 2.0), posUp.getZ() + 1.0
        );

        if (ignoreCrystals) {
            return mc.world.getEntitiesWithinAABB(Entity.class, box, entity -> !(entity.isDead || entity instanceof EntityEnderCrystal)).isEmpty();
        } else {
            return mc.world.getEntitiesWithinAABB(Entity.class, box, Entity::isEntityAlive).isEmpty();
        }
    }

    /****************************************************************
     *                      Block and Material Validation Methods
     ****************************************************************/

    /**
     * Checks if the given block is not a valid block for placing crystals.
     *
     * @param block The block to check.
     * @return True if the block is not valid, false otherwise.
     */
    public static boolean notValidBlock(Block block) {
        return block != Blocks.BEDROCK && block != Blocks.OBSIDIAN;
    }

    /**
     * Checks if the given material is not a valid material for placing crystals.
     *
     * @param material The material to check.
     * @return True if the material is not valid, false otherwise.
     */
    public static boolean notValidMaterial(Material material) {
        return material.isLiquid() || !material.isReplaceable();
    }

    /****************************************************************
     *                      Crystal Block Finding Methods
     ****************************************************************/

    /**
     * Finds all block positions within the given range where crystals can be placed.
     *
     * @param placeRange The range to check for valid crystal placement blocks.
     * @param server     The server version to consider.
     * @return A list of valid block positions for placing crystals.
     */
    public static List<BlockPos> findCrystalBlocks(float placeRange, AutoCrystal.Server server) {
        return EntityUtil.getSphere(mc.player.getPosition(), placeRange, (int) placeRange, false, true, 0)
                .stream()
                .filter(pos -> CrystalUtil.canPlaceCrystal(pos, server, false))
                .collect(Collectors.toList());
    }

    /**
     * Finds all block positions within the given range where crystals can be placed, excluding existing crystals.
     *
     * @param placeRange The range to check for valid crystal placement blocks.
     * @param server     The server version to consider.
     * @return A list of valid block positions for placing crystals, excluding existing crystals.
     */
    public static List<BlockPos> findCrystalBlocksExcludingCrystals(float placeRange, AutoCrystal.Server server) {
        return EntityUtil.getSphere(mc.player.getPosition(), placeRange, (int) placeRange, false, true, 0)
                .stream()
                .filter(pos -> CrystalUtil.canPlaceCrystal(pos, server, true))
                .collect(Collectors.toList());
    }
}
