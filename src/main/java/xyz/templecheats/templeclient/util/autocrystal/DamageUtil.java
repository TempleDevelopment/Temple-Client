package xyz.templecheats.templeclient.util.autocrystal;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.CombatRules;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class DamageUtil {
    private static final Minecraft mc = Minecraft.getMinecraft();

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
            float damage = (float)((int)((v * v + v) / 2.0D * 7.0D * (double) doubleExplosionSize + 1.0D));

            finalDamage = getBlastReductionThreaded(playerInfo, getDamageMultiplied(damage));
        } catch (NullPointerException ignored) {}

        return finalDamage;
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

    private static float getDamageMultiplied(float damage) {
        int diff = mc.world.getDifficulty().getId();
        return damage * (diff == 0 ? 0 : (diff == 2 ? 1 : (diff == 1 ? 0.5f : 1.5f)));
    }
}