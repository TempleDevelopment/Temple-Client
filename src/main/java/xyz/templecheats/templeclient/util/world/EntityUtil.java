package xyz.templecheats.templeclient.util.world;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class EntityUtil {
    private static final Minecraft mc = Minecraft.getMinecraft();

    /****************************************************************
     *                  Sphere Calculation Methods
     ****************************************************************/

    public static List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        List<BlockPos> circleBlocks = new ArrayList<>();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        for (int x = cx - (int) r; x <= cx + r; x++) {
            for (int z = cz - (int) r; z <= cz + r; z++) {
                for (int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleBlocks.add(l);
                    }
                }
            }
        }
        return circleBlocks;
    }

    /****************************************************************
     *                  Entity Check Methods
     ****************************************************************/

    public static boolean basicChecksEntity(Entity pl) {
        return pl.getName().equals(mc.player.getName()) || pl.isDead;
    }

    public static boolean isMoving() {
        return (double) mc.player.moveForward != 0.0 || (double) mc.player.moveStrafing != 0.0;
    }

    /****************************************************************
     *                  Item Stack Methods
     ****************************************************************/

    public static int getDamagePercent(ItemStack stack) {
        return (int) ((stack.getMaxDamage() - stack.getItemDamage()) / Math.max(0.1, stack.getMaxDamage()) * 100.0f);
    }

    /****************************************************************
     *                  Entity Interpolation Methods
     ****************************************************************/

    public static Vec3d interpolateEntity(Entity entity, float ticks) {
        double x = interpolate(entity.posX, entity.lastTickPosX, ticks);
        double y = interpolate(entity.posY, entity.lastTickPosY, ticks);
        double z = interpolate(entity.posZ, entity.lastTickPosZ, ticks);
        return new Vec3d(x, y, z);
    }

    private static double interpolate(double now, double then, float ticks) {
        return then + (now - then) * ticks;
    }
}
