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

import java.util.List;
import java.util.stream.Collectors;

public class CrystalUtil {
    private static final Minecraft mc = Minecraft.getMinecraft();
    
    public static boolean canPlaceCrystal(BlockPos blockPos, AutoCrystal.Server server, boolean ignoreCrystals) {
        if(notValidBlock(mc.world.getBlockState(blockPos).getBlock())) return false;
        
        BlockPos posUp = blockPos.up();
        
        if(server != AutoCrystal.Server.OneTwelve) {
            if(!mc.world.isAirBlock(posUp)) return false;
        } else {
            if(notValidMaterial(mc.world.getBlockState(posUp).getMaterial())
                    || notValidMaterial(mc.world.getBlockState(posUp.up()).getMaterial())) {
                return false;
            }
        }
        
        AxisAlignedBB box = new AxisAlignedBB(
                posUp.getX(), posUp.getY(), posUp.getZ(),
                posUp.getX() + 1.0, posUp.getY() + (server == AutoCrystal.Server.Crystalpvp_cc ? 1.0 : 2.0), posUp.getZ() + 1.0
        );
        
        if(ignoreCrystals) {
            return mc.world.getEntitiesWithinAABB(Entity.class, box, entity -> !(entity.isDead || entity instanceof EntityEnderCrystal)).isEmpty();
        } else {
            return mc.world.getEntitiesWithinAABB(Entity.class, box, Entity::isEntityAlive).isEmpty();
        }
    }
    
    public static boolean notValidBlock(Block block) {
        return block != Blocks.BEDROCK && block != Blocks.OBSIDIAN;
    }
    
    public static boolean notValidMaterial(Material material) {
        return material.isLiquid() || !material.isReplaceable();
    }
    
    public static List<BlockPos> findCrystalBlocks(float placeRange, AutoCrystal.Server server) {
        return EntityUtil.getSphere(mc.player.getPosition(), placeRange, (int) placeRange, false, true, 0)
                         .stream()
                         .filter(pos -> CrystalUtil.canPlaceCrystal(pos, server, false))
                         .collect(Collectors.toList());
    }
    
    public static List<BlockPos> findCrystalBlocksExcludingCrystals(float placeRange, AutoCrystal.Server server) {
        return EntityUtil.getSphere(mc.player.getPosition(), placeRange, (int) placeRange, false, true, 0)
                         .stream()
                         .filter(pos -> CrystalUtil.canPlaceCrystal(pos, server, true))
                         .collect(Collectors.toList());
    }
}