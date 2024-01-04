package xyz.templecheats.templeclient.impl.modules.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.impl.modules.Module;

public class Scaffold extends Module {
    public Scaffold() {
        super("Scaffold", Keyboard.KEY_NONE, Category.WORLD);
    }

    @Override
    public void onUpdate() {
        if (this.isEnabled()) {
            Entity p = mc.player;
            BlockPos bp = new BlockPos(p.posX, p.getEntityBoundingBox().minY - 1, p.posZ);
            if (valid(bp.down())) place(bp.down(), EnumFacing.UP);
            else if (valid(bp.add(-1, 0, 0))) place(bp.add(-1, 0, 0), EnumFacing.EAST);
            else if (valid(bp.add(1, 0, 0))) place(bp.add(1, 0, 0), EnumFacing.WEST);
            else if (valid(bp.add(0, 0, -1))) place(bp.add(0, 0, -1), EnumFacing.SOUTH);
            else if (valid(bp.add(0, 0, 1))) place(bp.add(0, 0, 1), EnumFacing.NORTH);
            else if (valid(bp.add(1, 0, 1))) {
                if (valid(bp.add(0, 0, 1))) place(bp.add(0, 0, 1), EnumFacing.NORTH);
                place(bp.add(1, 0, 1), EnumFacing.EAST);
            } else if (valid(bp.add(-1, 0, 1))) {
                if (valid(bp.add(-1, 0, 0))) place(bp.add(-1, 0, 0), EnumFacing.WEST);
                place(bp.add(-1, 0, 1), EnumFacing.SOUTH);
            } else if (valid(bp.add(-1, 0, -1))) {
                if (valid(bp.add(0, 0, -1))) place(bp.add(0, 0, -1), EnumFacing.SOUTH);
                place(bp.add(-1, 0, -1), EnumFacing.WEST);
            } else if (valid(bp.add(1, 0, -1))) {
                if (valid(bp.add(1, 0, 0))) place(bp.add(1, 0, 0), EnumFacing.EAST);
                place(bp.add(1, 0, -1), EnumFacing.NORTH);
            }
        }
    }

    void place(BlockPos p, EnumFacing f) {
        EntityPlayerSP _p = mc.player;
        EnumHand hand = EnumHand.MAIN_HAND;
        if (_p.getHeldItemMainhand().getItem() instanceof ItemBlock) {
            _p.swingArm(hand);
            mc.playerController.processRightClickBlock(_p, mc.world, p.offset(f), f, Vec3d.ZERO, hand);
            double x = p.getX() + 0.25 - _p.posX;
            double z = p.getZ() + 0.25 - _p.posZ;
            double y = p.getY() + 0.25 - _p.posY;
            double distance = MathHelper.sqrt(x * x + z * z);
            float yaw = (float) (Math.atan2(z, x) * 180 / Math.PI - 90);
            float pitch = (float) -(Math.atan2(y, distance) * 180 / Math.PI);
            mc.getConnection().sendPacket(new CPacketPlayer.PositionRotation(_p.posX, _p.getEntityBoundingBox().minY, _p.posZ, yaw, pitch, _p.onGround));
        }
    }

    boolean valid(BlockPos p) {
        Block b = mc.world.getBlockState(p).getBlock();
        return !(b instanceof BlockLiquid) && b.getMaterial(null) != Material.AIR;
    }
}
