package xyz.templecheats.templeclient.util.rotation;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RotationUtil {
    static Minecraft mc = Minecraft.getMinecraft();
    public static float[] rotations(Entity entity) {
        double x = entity.posX - mc.player.posX;
        double y = entity.posY - (mc.player.posY + mc.player.getEyeHeight());
        double z = entity.posZ - mc.player.posZ;

        double u = MathHelper.sqrt(x * x + z * z);

        float u2 = (float)(MathHelper.atan2(z, x) * (180D / Math.PI) - 90.0F);
        float u3 = (float)(-MathHelper.atan2(y, u) * (180D / Math.PI));

        return new float[] {
                u2,
                u3
        };
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