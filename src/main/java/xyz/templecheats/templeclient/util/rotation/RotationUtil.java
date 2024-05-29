package xyz.templecheats.templeclient.util.rotation;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import xyz.templecheats.templeclient.util.Globals;

public class RotationUtil implements Globals {

    private static final Minecraft mc = Minecraft.getMinecraft();

    /****************************************************************
     *                  Rotation Calculation Methods
     ****************************************************************/

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

    public static RotationUtil rotationCalculate(BlockPos to) {
        double deltaX = to.getX() - mc.player.getPositionEyes(1).x;
        double deltaY = to.getY() - mc.player.getPositionEyes(1).y;
        double deltaZ = to.getZ() - mc.player.getPositionEyes(1).z;

        float yaw = (float) Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90;
        float pitch = (float) Math.toDegrees(-Math.atan2(deltaY, Math.hypot(deltaX, deltaZ)));

        return new RotationUtil(MathHelper.wrapDegrees(yaw), MathHelper.wrapDegrees(pitch));
    }

    public static float[] getRotations(EntityLivingBase entity) {
        double x = entity.posX - mc.player.posX;
        double y = entity.posY - (mc.player.posY + mc.player.getEyeHeight());
        double z = entity.posZ - mc.player.posZ;

        double u = MathHelper.sqrt(x * x + z * z);

        float yaw = (float) (MathHelper.atan2(z, x) * (180D / Math.PI) - 90.0F);
        float pitch = (float) (-MathHelper.atan2(y, u) * (180D / Math.PI));

        return new float[]{yaw, pitch};
    }

    private static float[] getRotations2(Vec3d vec) {
        Vec3d eyesPos = getEyesPos();

        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

        return new float[]{
                mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw),
                mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - mc.player.rotationPitch)
        };
    }

    public static float[] getRotations3(BlockPos blockPos, EnumFacing enumFacing) {
        Vec3d vec3d = new Vec3d((double) blockPos.getX() + 0.5, mc.world.getBlockState(blockPos).getSelectedBoundingBox(mc.world, blockPos).maxY - 0.01, (double) blockPos.getZ() + 0.5);
        vec3d = vec3d.add(new Vec3d(enumFacing.getDirectionVec()).scale(0.5));

        Vec3d vec3d2 = getEyePosition();

        double d = vec3d.x - vec3d2.x;
        double d2 = vec3d.y - vec3d2.y;
        double d3 = vec3d.z - vec3d2.z;
        double d4 = d;
        double d5 = d3;
        double d6 = Math.sqrt(d4 * d4 + d5 * d5);

        float f = (float) (Math.toDegrees(Math.atan2(d3, d)) - 90.0f);
        float f2 = (float) (-Math.toDegrees(Math.atan2(d2, d6)));

        float[] ret = new float[2];
        ret[0] = mc.player.rotationYaw + MathHelper.wrapDegrees((float) (f - mc.player.rotationYaw));
        ret[1] = mc.player.rotationPitch + MathHelper.wrapDegrees((float) (f2 - mc.player.rotationPitch));

        return ret;
    }

    /****************************************************************
     *                  Block Interaction Methods
     ****************************************************************/

    public static EnumFacing getPlaceableSide(BlockPos pos) {
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbour = pos.offset(side);

            if (!mc.world.getBlockState(neighbour).getBlock().canCollideCheck(mc.world.getBlockState(neighbour), false)) {
                continue;
            }

            IBlockState blockState = mc.world.getBlockState(neighbour);
            if (!blockState.getMaterial().isReplaceable()) {
                return side;
            }
        }

        return null;
    }

    public static Block getBlock(BlockPos pos) {
        return getState(pos).getBlock();
    }

    public static IBlockState getState(BlockPos pos) {
        return mc.world.getBlockState(pos);
    }

    public static boolean canBeClicked(BlockPos pos) {
        return getBlock(pos).canCollideCheck(getState(pos), false);
    }

    /****************************************************************
     *                  Packet Sending Methods
     ****************************************************************/

    public static void faceVectorPacketInstant(Vec3d vec) {
        float[] rotations = getRotations2(vec);
        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rotations[0], rotations[1], mc.player.onGround));
    }

    /****************************************************************
     *                  Position and Angle Helpers
     ****************************************************************/

    public static Vec3d getEyesPos() {
        return new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
    }

    public static Vec3d getEyePosition() {
        return new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
    }

    /****************************************************************
     *                  RotationUtil Class
     ****************************************************************/

    private float yaw, pitch;
    private final Rotate rotate;

    public RotationUtil(float yaw, float pitch, Rotate rotate) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.rotate = rotate;
    }

    public RotationUtil(float yaw, float pitch) {
        this(yaw, pitch, Rotate.None);
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float in) {
        yaw = in;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float in) {
        pitch = in;
    }

    public Rotate getRotation() {
        return rotate;
    }

    public boolean isValid() {
        return !Float.isNaN(getYaw()) && !Float.isNaN(getPitch());
    }

    public enum Rotate {
        Packet,
        Client,
        None
    }
}
