package xyz.templecheats.templeclient.util.player;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;

public class PlayerInfo {

    /****************************************************************
     *                      Constants
     ****************************************************************/

    private static final Potion RESISTANCE = Potion.getPotionById(11);
    private static final DamageSource EXPLOSION_SOURCE = (new DamageSource("explosion")).setDifficultyScaled().setExplosion();

    /****************************************************************
     *                      Fields
     ****************************************************************/

    public final EntityPlayer entity;
    public final float totalArmourValue;
    public final float armourToughness;
    public final float health;
    public final int enchantModifier;
    public final boolean hasResistance;
    public final boolean lowArmour;

    /****************************************************************
     *                      Constructors
     ****************************************************************/

    /**
     * Constructs a PlayerInfo object with the given entity and armor percentage threshold.
     *
     * @param entity       The player entity.
     * @param armorPercent The armor percentage threshold to determine low armor status.
     */
    public PlayerInfo(EntityPlayer entity, float armorPercent) {
        this.entity = entity;

        this.totalArmourValue = entity.getTotalArmorValue();
        this.armourToughness = (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue();
        this.health = entity.getHealth() + entity.getAbsorptionAmount();
        this.enchantModifier = EnchantmentHelper.getEnchantmentModifierDamage(entity.getArmorInventoryList(), EXPLOSION_SOURCE);

        this.hasResistance = entity.isPotionActive(RESISTANCE);
        this.lowArmour = checkLowArmour(armorPercent);
    }

    /**
     * Constructs a PlayerInfo object with the given entity and low armor status.
     *
     * @param entity    The player entity.
     * @param lowArmour The low armor status.
     */
    public PlayerInfo(EntityPlayer entity, boolean lowArmour) {
        this.entity = entity;

        this.totalArmourValue = entity.getTotalArmorValue();
        this.armourToughness = (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue();
        this.health = entity.getHealth() + entity.getAbsorptionAmount();
        this.enchantModifier = EnchantmentHelper.getEnchantmentModifierDamage(entity.getArmorInventoryList(), EXPLOSION_SOURCE);

        this.hasResistance = entity.isPotionActive(RESISTANCE);
        this.lowArmour = lowArmour;
    }

    /****************************************************************
     *                      Helper Methods
     ****************************************************************/

    /**
     * Checks if the player's armor is below the given percentage threshold.
     *
     * @param armorPercent The armor percentage threshold.
     * @return True if the player's armor is below the threshold, false otherwise.
     */
    private boolean checkLowArmour(float armorPercent) {
        for (ItemStack stack : entity.getArmorInventoryList()) {
            if ((1.0f - ((float) stack.getItemDamage() / (float) stack.getMaxDamage())) < armorPercent) {
                return true;
            }
        }
        return false;
    }
}
