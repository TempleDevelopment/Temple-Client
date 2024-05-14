package xyz.templecheats.templeclient.features.module.modules.combat;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;

public class HoleFiller extends Module {
    /*
     * Settings
     */
    private final IntSetting range = new IntSetting("Range", this, 1, 5, 3);
    private final IntSetting radius = new IntSetting("Radius", this, 1, 10, 5);

    public HoleFiller() {
        super("HoleFiller", "Automatically places blocks in holes", Keyboard.KEY_NONE, Category.Combat);
        registerSettings(range, radius);
    }

    public int a(final Block block) {
        return a(new ItemStack(block).getItem());
    }

    public int a(final Item item) {
        try {
            for (int i = 0; i < 9; ++i) {
                if (item == mc.player.inventory.getStackInSlot(i).getItem()) {
                    return i;
                }
            }
        } catch (Exception ex) {}
        return -1;
    }

    public void a(final BlockPos blockPos) {
        a(EnumHand.MAIN_HAND, blockPos);
    }

    public void a(final EnumHand enumHand, final BlockPos blockPos) {
        final Vec3d vec3d = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
        for (final EnumFacing enumFacing: EnumFacing.values()) {
            final BlockPos offset = blockPos.offset(enumFacing);
            final EnumFacing opposite = enumFacing.getOpposite();
            if (mc.world.getBlockState(offset).getBlock().canCollideCheck(mc.world.getBlockState(offset), false)) {
                final Vec3d add = new Vec3d(offset).add(0.5, 0.5, 0.5)
                        .add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
                if (vec3d.squareDistanceTo(add) <= 18.0625) {
                    final double n = add.x - vec3d.x;
                    final double n2 = add.y - vec3d.y;
                    final double n3 = add.z - vec3d.z;
                    final float[] array = {
                            mc.player.rotationYaw + MathHelper.wrapDegrees(
                                    (float) Math.toDegrees(Math.atan2(n3, n)) - 90.0f - mc.player.rotationYaw),
                            mc.player.rotationPitch + MathHelper
                                    .wrapDegrees((float)(-Math.toDegrees(Math.atan2(n2, Math.sqrt(n * n + n3 * n3)))) -
                                            mc.player.rotationPitch)
                    };
                    mc.player.connection
                            .sendPacket((Packet) new CPacketPlayer.Rotation(array[0], array[1], mc.player.onGround));
                    mc.player.connection.sendPacket((Packet) new CPacketEntityAction((Entity) mc.player,
                            CPacketEntityAction.Action.START_SNEAKING));
                    mc.playerController.processRightClickBlock(mc.player, mc.world, offset, opposite, add, enumHand);
                    mc.player.swingArm(enumHand);
                    mc.player.connection.sendPacket((Packet) new CPacketEntityAction((Entity) mc.player,
                            CPacketEntityAction.Action.STOP_SNEAKING));
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc.player == null || mc.world == null)
            return;
        for (final EntityPlayer entityPlayer: mc.world.playerEntities) {
            if (!entityPlayer.getUniqueID().equals(mc.player.getUniqueID())) {
                final double doubleValue = this.radius.intValue();
                final BlockPos position = entityPlayer.getPosition();
                for (final BlockPos blockPos: BlockPos.getAllInBox(
                        position.add(-doubleValue, -doubleValue, -doubleValue),
                        position.add(doubleValue, doubleValue, doubleValue))) {
                    if (mc.player.getDistanceSqToCenter(blockPos) > this.range.intValue()) {
                        continue;
                    }
                    if (!mc.world.getBlockState(blockPos).getMaterial().isReplaceable() ||
                            !mc.world.getBlockState(blockPos.add(0, 1, 0)).getMaterial().isReplaceable() ||
                            (!mc.world.getBlockState(blockPos.add(0, -1, 0)).getMaterial().isSolid() ||
                                    !mc.world.getBlockState(blockPos.add(1, 0, 0)).getMaterial().isSolid() ||
                                    !mc.world.getBlockState(blockPos.add(0, 0, 1)).getMaterial().isSolid() ||
                                    !mc.world.getBlockState(blockPos.add(-1, 0, 0)).getMaterial().isSolid() ||
                                    !mc.world.getBlockState(blockPos.add(0, 0, -1)).getMaterial().isSolid() ||
                                    mc.world.getBlockState(blockPos.add(0, 0, 0)).getMaterial() != Material.AIR ||
                                    mc.world.getBlockState(blockPos.add(0, 1, 0)).getMaterial() != Material.AIR ||
                                    mc.world.getBlockState(blockPos.add(0, 2, 0)).getMaterial() != Material.AIR) ||
                            !mc.world.getEntitiesWithinAABB((Class) Entity.class, new AxisAlignedBB(blockPos))
                                    .isEmpty()) {
                        continue;
                    }
                    final int a = a(Blocks.OBSIDIAN);
                    if (a == -1) {
                        continue;
                    }
                    final int currentItem = mc.player.inventory.currentItem;
                    mc.player.inventory.currentItem = a;
                    a(blockPos);
                    mc.player.inventory.currentItem = currentItem;
                }
            }
        }
    }
}