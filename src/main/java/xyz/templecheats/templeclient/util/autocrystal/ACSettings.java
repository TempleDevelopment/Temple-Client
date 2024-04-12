package xyz.templecheats.templeclient.util.autocrystal;

import net.minecraft.util.math.Vec3d;
import xyz.templecheats.templeclient.features.module.modules.combat.AutoCrystal;

public class ACSettings {
    public final double enemyRangeSq;
    public final double breakRangeSq;
    public final double wallsRangeSq;
    public final float placeRange;

    public final float minDamage;
    public final float minFacePlaceDamage;
    public final float maxSelfDamage;
    public final float facePlaceHealth;

    public final boolean raytrace;

    public final AutoCrystal.Server server;
    public final AutoCrystal.Priority crystalPriority;

    public final PlayerInfo player;
    public final Vec3d playerPos;

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