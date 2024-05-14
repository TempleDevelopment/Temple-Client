package xyz.templecheats.templeclient.util.math;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public final class MathUtil {

    public static final Minecraft mc = Minecraft.getMinecraft();

    public static double degToRad(double deg) {
        return deg * (float)(Math.PI / 180.0f);
    }



    public static float[] calcAngle(Vec3d from, Vec3d to) {
        final double difX = to.x - from.x;
        final double difY = (to.y - from.y) * -1.0F;
        final double difZ = to.z - from.z;

        final double dist = MathHelper.sqrt(difX * difX + difZ * difZ);

        return new float[] {
                (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0f), (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist)))
        };
    }

    public static EnumFacing calcSide(BlockPos pos) {
        for (EnumFacing side: EnumFacing.values()) {
            BlockPos sideOffset = pos.offset(side);
            IBlockState offsetState = mc.world.getBlockState(sideOffset);
            if (!offsetState.getBlock().canCollideCheck(offsetState, false)) continue;
            if (!offsetState.getMaterial().isReplaceable()) return side;
        }
        return null;
    }

    public static double[] directionSpeed(double speed) {
        float forward = mc.player.movementInput.moveForward;
        float side = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();

        if (forward != 0) {
            if (side > 0) {
                yaw += (forward > 0 ? -45 : 45);
            } else if (side < 0) {
                yaw += (forward > 0 ? 45 : -45);
            }
            side = 0;

            //forward = clamp(forward, 0, 1);
            if (forward > 0) {
                forward = 1;
            } else if (forward < 0) {
                forward = -1;
            }
        }

        final double sin = Math.sin(Math.toRadians(yaw + 90));
        final double cos = Math.cos(Math.toRadians(yaw + 90));
        final double posX = (forward * speed * cos + side * speed * sin);
        final double posZ = (forward * speed * sin - side * speed * cos);
        return new double[] {
                posX,
                posZ
        };
    }

    public static float random(float min, float max) {
        return (float) (Math.random() * (max - min) + min);
    }


    public static double round(double value, int places) {
        if (places < 0) {
            return value;
        }
        return new BigDecimal(value).setScale(places, RoundingMode.HALF_UP).doubleValue();
    }


    public static float clamp(float val, float min, float max) {
        if (val <= min) {
            val = min;
        }
        if (val >= max) {
            val = max;
        }
        return val;
    }



    // linearly maps value from the range (a..b) to (c..d)
    public static double map(double value, double a, double b, double c, double d) {
        // first map value from (a..b) to (0..1)
        value = (value - a) / (b - a);
        // then map it from (0..1) to (c..d) and return it
        return c + value * (d - c);
    }

    public static double getDistanceToCenter(EntityPlayer player, BlockPos pos) {
        double deltaX = pos.getX() + 0.5 - player.posX;
        double deltaY = pos.getY() + 0.5 - player.posY;
        double deltaZ = pos.getZ() + 0.5 - player.posZ;

        return MathHelper.sqrt((deltaX * deltaX) + (deltaY * deltaY) + (deltaZ * deltaZ));
    }

    public static double getDistance(Vec3d pos, double x, double y, double z) {
        final double deltaX = pos.x - x;
        final double deltaY = pos.y - y;
        final double deltaZ = pos.z - z;
        return MathHelper.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
    }

    public static double getDistance(double x1, double z1, double x2, double z2) {
        double d0 = x1 - x2;
        double d1 = z1 - z2;
        return MathHelper.sqrt(d0 * d0 + d1 * d1);
    }

    public static double[] calcIntersection(double[] line, double[] line2) {
        final double a1 = line[3] - line[1];
        final double b1 = line[0] - line[2];
        final double c1 = a1 * line[0] + b1 * line[1];

        final double a2 = line2[3] - line2[1];
        final double b2 = line2[0] - line2[2];
        final double c2 = a2 * line2[0] + b2 * line2[1];

        final double delta = a1 * b2 - a2 * b1;

        return new double[] {
                (b2 * c1 - b1 * c2) / delta, (a1 * c2 - a2 * c1) / delta
        };
    }

    public static double lerp(double current, double target, double lerp) {
        current -= (current - target) * clamp((float) lerp, 0, 1);
        return current;
    }

    public static float lerp(float current, float target, float lerp) {
        current -= (current - target) * clamp(lerp, 0, 1);
        return current;
    }

    public static float coerceIn(float value, float min, float max) {
        return Math.min(Math.max(value, min), max);
    }

    public static float coerceIn(double value, double min, double max) {
        return (float) Math.min(Math.max(value, min), max);
    }

    public static float calculateGaussianValue(float x, float sigma) {
        double output = 1.0 / Math.sqrt(2.0 * Math.PI * (sigma * sigma));
        return (float) (output * Math.exp(-(x * x) / (2.0 * (sigma * sigma))));
    }


    public static int randomBetween(int min, int max) {
        return min + (new Random().nextInt() * (max - min));
    }
}