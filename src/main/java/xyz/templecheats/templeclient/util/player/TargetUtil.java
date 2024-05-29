package xyz.templecheats.templeclient.util.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static xyz.templecheats.templeclient.util.Globals.mc;

public class TargetUtil {

    /****************************************************************
     *                      Entity Targeting Methods
     ****************************************************************/

    public static ArrayList<Entity> getItems(boolean items) {
        ArrayList<Entity> itemList = new ArrayList<>();
        for (Entity entity : mc.player.world.loadedEntityList) {
            if (entity instanceof EntityItem && items) {
                itemList.add(entity);
            }
        }
        return itemList;
    }

    public static ArrayList<EntityLivingBase> getTargetList(boolean players, boolean hostile, boolean animal, boolean invisible) {
        Predicate<EntityLivingBase> filterPredicate = e -> {
            if ((e == mc.getRenderViewEntity()) || (e == mc.player) || (e.isInvisible() && !invisible) || e.isDead || (e.getHealth() <= 0)) {
                return false;
            }

            if ((e instanceof EntityPlayer) && (players && !((EntityPlayer) e).isSpectator())) {
                return true;
            }
            if (hostile && isHostile(e)) {
                return true;
            }
            return animal && isPassive(e);
        };

        return mc.player.world.loadedEntityList.stream()
                .filter(EntityLivingBase.class::isInstance)
                .map(EntityLivingBase.class::cast)
                .filter(filterPredicate)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static void updateEntityList(LinkedHashSet<Entity> entityList, boolean crystal, boolean self, boolean player, boolean items, boolean hostiles, boolean animals, double range) {
        entityList.clear();
        entityList.addAll(getTargetList(player, hostiles, animals, true));
        entityList.addAll(getItems(items));
        if (crystal) {
            for (Entity entity : mc.world.loadedEntityList) {
                if (entity instanceof EntityEnderCrystal) {
                    entityList.add(entity);
                }
            }
        }
        if (self && mc.gameSettings.thirdPersonView != 0) {
            entityList.add(mc.player);
        }
        entityList.removeIf(entity -> entity.getPositionVector().distanceTo(mc.player.getPositionVector()) > range);
    }

    /****************************************************************
     *                    Helper Methods
     ****************************************************************/

    private static boolean isPassive(EntityLivingBase entity) {
        return entity instanceof EntityAnimal ||
                entity instanceof EntityAgeable ||
                entity instanceof EntityAmbientCreature ||
                entity instanceof EntitySquid;
    }

    private static boolean isHostile(EntityLivingBase entity) {
        return entity instanceof EntityMob ||
                entity instanceof EntityShulker ||
                entity instanceof EntityIronGolem ||
                entity instanceof EntityDragon ||
                entity instanceof EntityGhast;
    }
}
