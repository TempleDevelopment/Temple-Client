package xyz.templecheats.templeclient.util.autocrystal;

import net.minecraft.util.math.Vec3d;
import xyz.templecheats.templeclient.features.module.modules.combat.AutoCrystal;
import xyz.templecheats.templeclient.util.player.PlayerInfo;

public class ACSettings {
    // Range settings
    public final double enemyRangeSq;
    public final double breakRangeSq;
    public final double wallsRangeSq;
    public final float placeRange;

    // Damage settings
    public final float minDamage;
    public final float minFacePlaceDamage;
    public final float maxSelfDamage;
    public final float facePlaceHealth;

    // Miscellaneous settings
    public final boolean raytrace;
    public final AutoCrystal.Server server;
    public final AutoCrystal.Priority crystalPriority;

    // Player information
    public final PlayerInfo player;
    public final Vec3d playerPos;

    /**
     * Constructor to initialize the AutoCrystal settings.
     *
     * @param enemyRange         Maximum range to consider enemies.
     * @param range              Maximum range for placing crystals.
     * @param wallsRange         Maximum range through walls.
     * @param minDamage          Minimum damage required to place a crystal.
     * @param minFacePlaceDamage Minimum damage for face-placing.
     * @param maxSelfDamage      Maximum self-damage allowed.
     * @param facePlaceHealth    Health threshold for face-placing.
     * @param raytrace           Whether to use ray tracing for line of sight checks.
     * @param server             Server version.
     * @param crystalPriority    Priority setting for crystal placement and breaking.
     * @param player             Player information.
     * @param playerPos          Player position in the world.
     */
    public ACSettings(double enemyRange, double range, double wallsRange, double minDamage, double minFacePlaceDamage, double maxSelfDamage, double facePlaceHealth, boolean raytrace, AutoCrystal.Server server, AutoCrystal.Priority crystalPriority, PlayerInfo player, Vec3d playerPos) {
        this.enemyRangeSq = enemyRange * enemyRange;
        this.breakRangeSq = range * range;
        this.wallsRangeSq = wallsRange * wallsRange;
        this.placeRange = (float) range;

        this.minDamage = (float) minDamage;
        this.minFacePlaceDamage = (float) minFacePlaceDamage;
        this.maxSelfDamage = (float) maxSelfDamage;
        this.facePlaceHealth = (float) facePlaceHealth;

        this.raytrace = raytrace;

        this.server = server;
        this.crystalPriority = crystalPriority;

        this.player = player;
        this.playerPos = playerPos;
    }
}
