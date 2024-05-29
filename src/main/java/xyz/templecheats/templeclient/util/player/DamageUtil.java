package xyz.templecheats.templeclient.util.player;

import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;

public class DamageUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    /****************************************************************
     *                  Damage Calculation Methods
     ****************************************************************/

    public static float calculateDamage(double posX, double posY, double posZ, Entity entity) {
        float finalDamage = 1.0f;
        try {
            float doubleExplosionSize = 12.0F;
            double distancedSize = entity.getDistance(posX, posY, posZ) / (double) doubleExplosionSize;
            double blockDensity = entity.world.getBlockDensity(new Vec3d(posX, posY, posZ), entity.getEntityBoundingBox());
            double v = (1.0D - distancedSize) * blockDensity;
            float damage = (float) ((int) ((v * v + v) / 2.0D * 7.0D * (double) doubleExplosionSize + 1.0D));

            if (entity instanceof EntityLivingBase) {
                finalDamage = getBlastReduction((EntityLivingBase) entity, getDamageMultiplied(damage), new Explosion(mc.world, null, posX, posY, posZ, 6F, false, true));
            }
        } catch (NullPointerException ignored) {
        }

        return finalDamage;
    }

    private static float getDamageMultiplied(float damage) {
        int diff = mc.world.getDifficulty().getId();
        return damage * (diff == 0 ? 0 : (diff == 2 ? 1 : (diff == 1 ? 0.5f : 1.5f)));
    }

    public static float calculateDamageThreaded(EntityEnderCrystal crystal, PlayerInfo playerInfo) {
        return calculateDamageThreaded(crystal.posX, crystal.posY, crystal.posZ, playerInfo);
    }

    public static float calculateDamageThreaded(BlockPos pos, PlayerInfo playerInfo) {
        return calculateDamageThreaded(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, playerInfo);
    }

    public static float calculateDamageThreaded(double posX, double posY, double posZ, PlayerInfo playerInfo) {
        float finalDamage = 1.0f;
        try {
            float doubleExplosionSize = 12.0F;
            double distancedSize = playerInfo.entity.getDistance(posX, posY, posZ) / (double) doubleExplosionSize;
            double blockDensity = playerInfo.entity.world.getBlockDensity(new Vec3d(posX, posY, posZ), playerInfo.entity.getEntityBoundingBox());
            double v = (1.0D - distancedSize) * blockDensity;
            float damage = (float) ((int) ((v * v + v) / 2.0D * 7.0D * (double) doubleExplosionSize + 1.0D));

            finalDamage = getBlastReductionThreaded(playerInfo, getDamageMultiplied(damage));
        } catch (NullPointerException ignored) {
        }

        return finalDamage;
    }

    public static float calculateDamageThreaded(double posX, double posY, double posZ, EntityLivingBase playerInfo) {
        float finalDamage = 1.0f;
        try {
            float doubleExplosionSize = 12.0F;
            double distancedSize = playerInfo.getDistance(posX, posY, posZ) / (double) doubleExplosionSize;
            double blockDensity = playerInfo.world.getBlockDensity(new Vec3d(posX, posY, posZ), playerInfo.getEntityBoundingBox());
            double v = (1.0D - distancedSize) * blockDensity;
            float damage = (float) ((int) ((v * v + v) / 2.0D * 7.0D * (double) doubleExplosionSize + 1.0D));

            finalDamage = getBlastReductionThreaded(playerInfo, getDamageMultiplied(damage), new Explosion(mc.world, null, posX, posY, posZ, 6F, false, true));
        } catch (NullPointerException ignored) {
        }

        return finalDamage;
    }

    /****************************************************************
     *                  Blast Reduction Methods
     ****************************************************************/

    public static float getBlastReduction(EntityLivingBase entity, float damage, Explosion explosion) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer ep = (EntityPlayer) entity;
            DamageSource ds = DamageSource.causeExplosionDamage(explosion);
            damage = CombatRules.getDamageAfterAbsorb(damage, (float) ep.getTotalArmorValue(), (float) ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());

            int k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
            float f = MathHelper.clamp(k, 0.0F, 20.0F);
            damage *= 1.0F - f / 25.0F;

            if (entity.isPotionActive(Potion.getPotionById(11))) {
                damage = damage - (damage / 4);
            }
            damage = Math.max(damage, 0.0F);
            return damage;
        }
        damage = CombatRules.getDamageAfterAbsorb(damage, (float) entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        return damage;
    }

    public static float getBlastReductionThreaded(final EntityLivingBase entity, final float damageI, final Explosion explosion) {
        float damage = damageI;
        if (entity instanceof EntityPlayer) {
            final EntityPlayer ep = (EntityPlayer) entity;
            final DamageSource ds = DamageSource.causeExplosionDamage(explosion);
            damage = CombatRules.getDamageAfterAbsorb(damage, (float) ep.getTotalArmorValue(), (float) ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
            int k = 0;
            try {
                k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
            } catch (Exception ex) {
            }
            final float f = MathHelper.clamp((float) k, 0.0f, 20.0f);
            damage *= 1.0f - f / 25.0f;
            if (entity.isPotionActive(MobEffects.RESISTANCE)) {
                damage -= damage / 4.0f;
            }
            damage = Math.max(damage, 0.0f);
            return damage;
        }
        damage = CombatRules.getDamageAfterAbsorb(damage, (float) entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        return damage;
    }

    public static float getBlastReductionThreaded(PlayerInfo playerInfo, float damage) {
        damage = CombatRules.getDamageAfterAbsorb(damage, playerInfo.totalArmourValue, playerInfo.armourToughness);

        float f = MathHelper.clamp(playerInfo.enchantModifier, 0.0F, 20.0F);
        damage *= 1.0F - f / 25.0F;

        if (playerInfo.hasResistance) {
            damage = damage - (damage / 4);
        }
        damage = Math.max(damage, 0.0F);
        return damage;
    }
}
