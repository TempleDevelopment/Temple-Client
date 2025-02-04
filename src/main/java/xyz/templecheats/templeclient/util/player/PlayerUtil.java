package xyz.templecheats.templeclient.util.player;

import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import xyz.templecheats.templeclient.util.world.BlockUtil;
import xyz.templecheats.templeclient.util.world.EntityUtil;

import java.util.ArrayList;

public class PlayerUtil {
    private static final Minecraft mc = Minecraft.getMinecraft();

    /****************************************************************
     *                   Player Information Methods
     ****************************************************************/

    public static float getHealth() {
        return mc.player.getHealth() + mc.player.getAbsorptionAmount();
    }

    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }

    public static EntityPlayer findLookingPlayer(double rangeMax) {
        ArrayList<EntityPlayer> listPlayer = new ArrayList<>();
        for (EntityPlayer playerSin : mc.world.playerEntities) {
            if (EntityUtil.basicChecksEntity(playerSin))
                continue;
            if (mc.player.getDistance(playerSin) <= rangeMax) {
                listPlayer.add(playerSin);
            }
        }

        EntityPlayer target = null;
        Vec3d positionEyes = mc.player.getPositionEyes(mc.getRenderPartialTicks());
        Vec3d rotationEyes = mc.player.getLook(mc.getRenderPartialTicks());
        int precision = 2;
        for (int i = 0; i < (int) rangeMax; i++) {
            for (int j = precision; j > 0; j--) {
                for (EntityPlayer targetTemp : listPlayer) {
                    AxisAlignedBB playerBox = targetTemp.getEntityBoundingBox();
                    double xArray = positionEyes.x + (rotationEyes.x * i) + rotationEyes.x / j;
                    double yArray = positionEyes.y + (rotationEyes.y * i) + rotationEyes.y / j;
                    double zArray = positionEyes.z + (rotationEyes.z * i) + rotationEyes.z / j;
                    if (playerBox.maxY >= yArray && playerBox.minY <= yArray
                            && playerBox.maxX >= xArray && playerBox.minX <= xArray
                            && playerBox.maxZ >= zArray && playerBox.minZ <= zArray) {
                        target = targetTemp;
                    }
                }
            }
        }

        return target;
    }

    /****************************************************************
     *                    Player Movement Methods
     ****************************************************************/

    public static void centerPlayer(Vec3d centeredBlock) {

        double xDeviation = Math.abs(centeredBlock.x - mc.player.posX);
        double zDeviation = Math.abs(centeredBlock.z - mc.player.posZ);

        if (xDeviation <= 0.1 && zDeviation <= 0.1) {
            centeredBlock = Vec3d.ZERO;
        } else {
            double newX = -2;
            double newZ = -2;
            int xRel = (mc.player.posX < 0 ? -1 : 1);
            int zRel = (mc.player.posZ < 0 ? -1 : 1);
            if (BlockUtil.getBlock(mc.player.posX, mc.player.posY - 1, mc.player.posZ) instanceof BlockAir) {
                if (Math.abs((mc.player.posX % 1)) * 1E2 <= 30) {
                    newX = Math.round(mc.player.posX - (0.3 * xRel)) + 0.5 * -xRel;
                } else if (Math.abs((mc.player.posX % 1)) * 1E2 >= 70) {
                    newX = Math.round(mc.player.posX + (0.3 * xRel)) - 0.5 * -xRel;
                }
                if (Math.abs((mc.player.posZ % 1)) * 1E2 <= 30) {
                    newZ = Math.round(mc.player.posZ - (0.3 * zRel)) + 0.5 * -zRel;
                } else if (Math.abs((mc.player.posZ % 1)) * 1E2 >= 70) {
                    newZ = Math.round(mc.player.posZ + (0.3 * zRel)) - 0.5 * -zRel;
                }
            }

            if (newX == -2)
                if (mc.player.posX > Math.round(mc.player.posX)) {
                    newX = Math.round(mc.player.posX) + 0.5;
                }
                // (mc.player.posX % 1)*1E2 < 30
                else if (mc.player.posX < Math.round(mc.player.posX)) {
                    newX = Math.round(mc.player.posX) - 0.5;
                } else {
                    newX = mc.player.posX;
                }

            if (newZ == -2)
                if (mc.player.posZ > Math.round(mc.player.posZ)) {
                    newZ = Math.round(mc.player.posZ) + 0.5;
                } else if (mc.player.posZ < Math.round(mc.player.posZ)) {
                    newZ = Math.round(mc.player.posZ) - 0.5;
                } else {
                    newZ = mc.player.posZ;
                }

            mc.player.connection.sendPacket(new CPacketPlayer.Position(newX, mc.player.posY, newZ, true));
            mc.player.setPosition(newX, mc.player.posY, newZ);
        }
    }
}
