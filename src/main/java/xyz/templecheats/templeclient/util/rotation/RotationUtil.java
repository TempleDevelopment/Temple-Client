package xyz.templecheats.templeclient.util.rotation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class RotationUtil {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static Vec2f getRotationTo(Vec3d posTo) {
        EntityPlayerSP player = mc.player;
        return player != null ? getRotationTo(player.getPositionEyes(1.0f), posTo) : Vec2f.ZERO;
    }

    public static Vec2f getRotationTo(Vec3d posFrom, Vec3d posTo) {
        return getRotationFromVec(posTo.subtract(posFrom));
    }

    public static Vec2f getRotationFromVec(Vec3d vec) {
        double lengthXZ = Math.hypot(vec.x, vec.z);
        double yaw = normalizeAngle(Math.toDegrees(Math.atan2(vec.z, vec.x)) - 90.0);
        double pitch = normalizeAngle(Math.toDegrees(-Math.atan2(vec.y, lengthXZ)));

        return new Vec2f((float) yaw, (float) pitch);
    }

    public static double normalizeAngle(double angle) {
        angle %= 360.0;

        if (angle >= 180.0) {
            angle -= 360.0;
        }

        if (angle < -180.0) {
            angle += 360.0;
        }

        return angle;
    }

    public static float[] rotations(BlockPos pos) {
        final Vec3d vec = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

        double x = vec.x - mc.player.posX;
        double y = vec.y - (mc.player.posY + mc.player.getEyeHeight());
        double z = vec.z - mc.player.posZ;

        double u = MathHelper.sqrt(x * x + z * z);

        float u2 = (float)(MathHelper.atan2(z, x) * (180D / Math.PI) - 90.0F);
        float u3 = (float)(-MathHelper.atan2(y, u) * (180D / Math.PI));

        return new float[] {
                u2,
                u3
        };
    }

    public static Rotation rotationCalculate(BlockPos to) {
        double deltaX = to.getX() - mc.player.getPositionEyes(1).x;
        double deltaY = to.getY() - mc.player.getPositionEyes(1).y;
        double deltaZ = to.getZ() - mc.player.getPositionEyes(1).z;

        float yaw = (float) Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90;
        float pitch = (float) Math.toDegrees(-Math.atan2(deltaY, Math.hypot(deltaX, deltaZ)));

        return new Rotation(MathHelper.wrapDegrees(yaw), MathHelper.wrapDegrees(pitch));
    }

    public static float[] getRotations(EntityLivingBase entity) {
        double x = entity.posX - mc.player.posX;
        double y = entity.posY - (mc.player.posY + mc.player.getEyeHeight());
        double z = entity.posZ - mc.player.posZ;

        double u = MathHelper.sqrt(x * x + z * z);

        float yaw = (float)(MathHelper.atan2(z, x) * (180D / Math.PI) - 90.0F);
        float pitch = (float)(-MathHelper.atan2(y, u) * (180D / Math.PI));

        return new float[] {
                yaw,
                pitch
        };
    }
}