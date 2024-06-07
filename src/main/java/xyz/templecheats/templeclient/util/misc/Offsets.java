package xyz.templecheats.templeclient.util.misc;

import net.minecraft.util.math.Vec3d;

public class Offsets {

    /****************************************************************
     *                      Surround Offsets
     ****************************************************************/

    // The BlockPos of (0, 0, 0) is the "center" / where the player's feet is
    public static final Vec3d[] SURROUND = {
            // layer below feet
            new Vec3d(0, -1, 0),
            new Vec3d(-1, -1, 0),
            new Vec3d(1, -1, 0),
            new Vec3d(0, -1, -1),
            new Vec3d(0, -1, 1),
            // layer at feet level
            new Vec3d(-1, 0, 0),
            new Vec3d(1, 0, 0),
            new Vec3d(0, 0, -1),
            new Vec3d(0, 0, 1)
    };

    // "anti city" surround places blocks two blocks out in each cardinal direction
    public static final Vec3d[] SURROUND_CITY = {
            // layer below feet
            new Vec3d(0, -1, 0),
            new Vec3d(-1, -1, 0),
            new Vec3d(1, -1, 0),
            new Vec3d(0, -1, -1),
            new Vec3d(0, -1, 1),
            // layer at feet level
            new Vec3d(-1, 0, 0),
            new Vec3d(1, 0, 0),
            new Vec3d(0, 0, -1),
            new Vec3d(0, 0, 1),
            // anti city layer
            new Vec3d(-2, 0, 0),
            new Vec3d(2, 0, 0),
            new Vec3d(0, 0, -2),
            new Vec3d(0, 0, 2)
    };

    /****************************************************************
     *                      AutoTrap Offsets
     ****************************************************************/
    public static final Vec3d[] AUTO_TRAP = {
            // layer below feet
            new Vec3d(0, -1, -1), new Vec3d(1, -1, 0),
            new Vec3d(0, -1, 1), new Vec3d(-1, -1, 0),
            new Vec3d(0, 0, -1), new Vec3d(1, 0, 0),
            new Vec3d(0, 0, 1), new Vec3d(-1, 0, 0),
            new Vec3d(0, 1, -1), new Vec3d(1, 1, 0),
            new Vec3d(0, 1, 1), new Vec3d(-1, 1, 0),
            new Vec3d(0, 2, -1), new Vec3d(0, 2, 1),
            new Vec3d(0, 2, 0)
    };

    /****************************************************************
     *                      SelfTrap Offsets
     ****************************************************************/
    public static final Vec3d[] TRAPSIMPLE = {
            new Vec3d(-1, 0, 0),
            new Vec3d(1, 0, 0),
            new Vec3d(0, 0, -1),
            new Vec3d(0, 0, 1),
            new Vec3d(1, 1, 0),
            new Vec3d(0, 1, -1),
            new Vec3d(0, 1, 1),
            new Vec3d(-1, 1, 0),
            new Vec3d(-1, 2, 0),
            new Vec3d(-1, 3, 0),
            new Vec3d(0, 3, 0)
    };
}
