package xyz.templecheats.templeclient.api.util.autocrystal;

import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class ACUtil {
    public static CrystalInfo.PlaceInfo calculateBestPlacement(ACSettings settings, PlayerInfo target, List<BlockPos> possibleLocations) {
        double x = settings.playerPos.x;
        double y = settings.playerPos.y;
        double z = settings.playerPos.z;
        
        BlockPos best = null;
        float bestDamage = 0f;
        for(BlockPos pos : possibleLocations) {
            if(target.entity.getDistanceSq((double) pos.getX() + 0.5, (double) pos.getY() + 1.0, (double) pos.getZ() + 0.5) <= settings.enemyRangeSq) {
                float currentDamage = DamageUtil.calculateDamageThreaded(pos, target);
                if(currentDamage == bestDamage) {
                    if(best == null || pos.distanceSq(x, y, z) < best.distanceSq(x, y, z)) {
                        bestDamage = currentDamage;
                        best = pos;
                    }
                } else if(currentDamage > bestDamage) {
                    bestDamage = currentDamage;
                    best = pos;
                }
            }
        }
        
        if(best != null) {
            if(bestDamage >= settings.minDamage || ((target.health <= settings.facePlaceHealth || target.lowArmour) && bestDamage >= settings.minFacePlaceDamage)) {
                return new CrystalInfo.PlaceInfo(bestDamage, target, best);
            }
        }
        
        return null;
    }
    
    public static CrystalInfo.BreakInfo calculateBestBreakable(ACSettings settings, PlayerInfo target, List<EntityEnderCrystal> crystals) {
        double x = settings.playerPos.x;
        double y = settings.playerPos.y;
        double z = settings.playerPos.z;
        
        EntityEnderCrystal best = null;
        float bestDamage = 0f;
        for(EntityEnderCrystal crystal : crystals) {
            float currentDamage = DamageUtil.calculateDamageThreaded(crystal, target);
            if(currentDamage == bestDamage) {
                if(best == null || crystal.getDistanceSq(x, y, z) < best.getDistanceSq(x, y, z)) {
                    bestDamage = currentDamage;
                    best = crystal;
                }
            } else if(currentDamage > bestDamage) {
                bestDamage = currentDamage;
                best = crystal;
            }
        }
        
        if(best != null) {
            return new CrystalInfo.BreakInfo(bestDamage, target, best);
        }
        
        return null;
    }
}
