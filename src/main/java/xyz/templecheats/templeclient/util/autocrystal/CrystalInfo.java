package xyz.templecheats.templeclient.util.autocrystal;

import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.math.BlockPos;
import xyz.templecheats.templeclient.util.player.PlayerInfo;

public class CrystalInfo {

    /****************************************************************
     *                      Instance Variables
     ****************************************************************/

    public final float damage;
    public final PlayerInfo target;

    /****************************************************************
     *                      Constructors
     ****************************************************************/

    private CrystalInfo(float damage, PlayerInfo target) {
        this.damage = damage;
        this.target = target;
    }

    /****************************************************************
     *                      Inner Classes
     ****************************************************************/

    /**
     * Class to store information about breaking a crystal.
     */
    public static class BreakInfo extends CrystalInfo {
        public final EntityEnderCrystal crystal;

        public BreakInfo(float damage, PlayerInfo target, EntityEnderCrystal crystal) {
            super(damage, target);
            this.crystal = crystal;
        }
    }

    /**
     * Class to store information about placing a crystal.
     */
    public static class PlaceInfo extends CrystalInfo {
        public final BlockPos pos;

        public PlaceInfo(float damage, PlayerInfo target, BlockPos pos) {
            super(damage, target);
            this.pos = pos;
        }
    }
}
