package xyz.templecheats.templeclient.features.module.modules.combat;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBed;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.manager.InventoryManager;
import xyz.templecheats.templeclient.util.player.DamageUtil;
import xyz.templecheats.templeclient.util.time.TimerUtil;
import xyz.templecheats.templeclient.util.world.BlockUtil;
import xyz.templecheats.templeclient.util.world.EntityUtil;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BedAura extends Module {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    private final EnumSetting<Mode> attackMode = new EnumSetting<>("Mode", this, Mode.Own);
    private final DoubleSetting attackRange = new DoubleSetting("Attack Range", this, 0, 7, 4);
    private final IntSetting breakDelay = new IntSetting("Break Delay", this, 0, 20, 1);
    private final IntSetting placeDelay = new IntSetting("Place Delay", this, 0, 20, 1);
    private final DoubleSetting targetRange = new DoubleSetting("Target Range", this, 0, 6, 4);
    private final BooleanSetting rotate = new BooleanSetting("Rotate", this, true);
    private final BooleanSetting disableNone = new BooleanSetting("Disable No Bed", this, false);
    private final BooleanSetting autoSwitch = new BooleanSetting("Switch", this, true);
    private final BooleanSetting antiSuicide = new BooleanSetting("Anti Suicide", this, false);
    private final IntSetting antiSuicideHealth = new IntSetting("Suicide Health", this, 1, 36, 14);
    private final IntSetting minDamage = new IntSetting("Min Damage", this, 1, 36, 5);

    /****************************************************************
     *                      Variables
     ****************************************************************/
    private boolean hasNone = false;
    private int oldSlot = -1;
    private final ObjectSet<BlockPos> placedPos = new ObjectOpenHashSet<>();
    private final TimerUtil breakTimer = new TimerUtil();
    private final TimerUtil placeTimer = new TimerUtil();

    public BedAura() {
        super("BedAura", "Automatically places and breaks beds for bed pvp", Keyboard.KEY_NONE, Category.Combat);
        this.registerSettings(rotate, disableNone, autoSwitch, antiSuicide,
                attackRange, breakDelay, placeDelay, targetRange, antiSuicideHealth, minDamage,
                attackMode);
    }

    @Override
    public void onEnable() {
        hasNone = false;
        placedPos.clear();

        if (mc.player == null || mc.world == null) {
            disable();
            return;
        }

        int bedSlot = InventoryManager.findFirstItemSlot(ItemBed.class, 0, 8);

        if (mc.player.inventory.currentItem != bedSlot && bedSlot != -1 && autoSwitch.booleanValue()) {
            oldSlot = mc.player.inventory.currentItem;
            mc.player.inventory.currentItem = bedSlot;
        } else if (bedSlot == -1) {
            hasNone = true;
        }
    }

    @Override
    public void onDisable() {
        placedPos.clear();

        if (mc.player == null || mc.world == null) {
            return;
        }

        if (autoSwitch.booleanValue() && mc.player.inventory.currentItem != oldSlot && oldSlot != -1) {
            mc.player.inventory.currentItem = oldSlot;
        }

        if (hasNone && disableNone.booleanValue());

        hasNone = false;
        oldSlot = -1;
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null || mc.player.dimension == 0) {
            disable();
            return;
        }

        int bedSlot = InventoryManager.findFirstItemSlot(ItemBed.class, 0, 8);

        if (mc.player.inventory.currentItem != bedSlot && bedSlot != -1 && autoSwitch.booleanValue()) {
            oldSlot = mc.player.inventory.currentItem;
            mc.player.inventory.currentItem = bedSlot;
        } else if (bedSlot == -1) {
            hasNone = true;
        }

        if (antiSuicide.booleanValue() && (mc.player.getHealth() + mc.player.getAbsorptionAmount()) < antiSuicideHealth.intValue()) {
            return;
        }

        if (breakTimer.getTimePassed() / 50L >= breakDelay.intValue()) {
            breakTimer.reset();
            breakBed();
        }

        if (hasNone) {
            if (disableNone.booleanValue()) {
                disable();
                return;
            }
            return;
        }

        if (mc.player.inventory.getStackInSlot(mc.player.inventory.currentItem).getItem() != Items.BED) {
            return;
        }

        if (placeTimer.getTimePassed() / 50L >= placeDelay.intValue()) {
            placeTimer.reset();
            placeBed();
        }
    }

    private void breakBed() {
        for (TileEntity tileEntity : findBedEntities(mc.player)) {
            if (!(tileEntity instanceof TileEntityBed)) {
                continue;
            }

            if (rotate.booleanValue()) {
                BlockUtil.faceVectorPacketInstant(new Vec3d(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ()), true);
            }

            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(tileEntity.getPos(), EnumFacing.UP, EnumHand.OFF_HAND, 0, 0, 0));
            return;
        }
    }

    private void placeBed() {
        for (EntityPlayer entityPlayer : findTargetEntities(mc.player)) {
            if (entityPlayer.isDead) {
                continue;
            }

            List<BedPlacement> targetPos = findTargetPlacePos(entityPlayer);

            if (targetPos.isEmpty()) {
                continue;
            }

            for (BedPlacement placement : targetPos) {
                if (placement.upPos.getDistance((int) mc.player.posX, (int) mc.player.posY, (int) mc.player.posZ) > attackRange.doubleValue()) {
                    continue;
                }

                if (mc.world.getBlockState(placement.upPos).getBlock() != Blocks.AIR) {
                    continue;
                }

                if (entityPlayer.getPosition() == placement.upPos) {
                    continue;
                }

                if (placement.damage < minDamage.intValue()) {
                    continue;
                }

                if (mc.world.getBlockState(placement.upPos.east()).getBlock() == Blocks.AIR) {
                    placeBedFinal(placement.upPos, 90, EnumFacing.DOWN);
                    return;
                } else if (mc.world.getBlockState(placement.upPos.west()).getBlock() == Blocks.AIR) {
                    placeBedFinal(placement.upPos, -90, EnumFacing.DOWN);
                    return;
                } else if (mc.world.getBlockState(placement.upPos.north()).getBlock() == Blocks.AIR) {
                    placeBedFinal(placement.upPos, 0, EnumFacing.DOWN);
                    return;
                } else if (mc.world.getBlockState(placement.upPos.south()).getBlock() == Blocks.AIR) {
                    placeBedFinal(placement.upPos, 180, EnumFacing.SOUTH);
                    return;
                }
            }
        }
    }

    private NonNullList<TileEntity> findBedEntities(EntityPlayer entityPlayer) {
        NonNullList<TileEntity> bedEntities = NonNullList.create();

        mc.world.loadedTileEntityList.stream()
                .filter(tileEntity -> tileEntity instanceof TileEntityBed)
                .filter(tileEntity -> tileEntity.getDistanceSq(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ) <= (attackRange.doubleValue() * attackRange.doubleValue()))
                .filter(this::isOwn)
                .forEach(bedEntities::add);

        bedEntities.sort(Comparator.comparing(tileEntity -> tileEntity.getDistanceSq(entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ)));
        return bedEntities;
    }

    private boolean isOwn(TileEntity tileEntity) {
        if (attackMode.value().name().equalsIgnoreCase("Normal")) {
            return true;
        } else if (attackMode.value().name().equalsIgnoreCase("Own")) {
            for (BlockPos blockPos : placedPos) {
                if (blockPos.getDistance(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ()) <= 3) {
                    return true;
                }
            }
        }

        return false;
    }

    private NonNullList<EntityPlayer> findTargetEntities(EntityPlayer entityPlayer) {
        NonNullList<EntityPlayer> targetEntities = NonNullList.create();

        mc.world.playerEntities.stream()
                .filter(entityPlayer1 -> !EntityUtil.basicChecksEntity(entityPlayer1))
                .filter(entityPlayer1 -> entityPlayer1.getDistance(entityPlayer) <= targetRange.doubleValue())
                .sorted(Comparator.comparing(entityPlayer1 -> entityPlayer1.getDistance(entityPlayer)))
                .forEach(targetEntities::add);

        return targetEntities;
    }

    private List<BedPlacement> findTargetPlacePos(EntityPlayer entityPlayer) {
        return EntityUtil.getSphere(mc.player.getPosition(), (int) attackRange.doubleValue(), (int) attackRange.doubleValue(), false, true, 0)
                .stream()
                .filter(this::canPlaceBed)
                .map(blockPos -> new BedPlacement(blockPos, entityPlayer))
                .sorted(Comparator.comparingDouble(b -> -b.damage))
                .collect(Collectors.toList());
    }

    private boolean canPlaceBed(BlockPos blockPos) {
        if (mc.world.getBlockState(blockPos.up()).getBlock() != Blocks.AIR) {
            return false;
        }

        if (mc.world.getBlockState(blockPos).getBlock() == Blocks.AIR) {
            return false;
        }

        return mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos)).isEmpty();
    }

    private void placeBedFinal(BlockPos blockPos, int direction, EnumFacing enumFacing) {
        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(direction, 0, mc.player.onGround));

        if (mc.world.getBlockState(blockPos).getBlock() != Blocks.AIR) {
            return;
        }

        BlockPos neighbourPos = blockPos.offset(enumFacing);
        EnumFacing oppositeFacing = enumFacing.getOpposite();

        Vec3d vec3d = new Vec3d(neighbourPos).add(0.5, 0.5, 0.5).add(new Vec3d(oppositeFacing.getDirectionVec()).scale(0.5));

        if (rotate.booleanValue()) {
            BlockUtil.faceVectorPacketInstant(vec3d, true);
        }

        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
        mc.playerController.processRightClickBlock(mc.player, mc.world, neighbourPos, oppositeFacing, vec3d, EnumHand.MAIN_HAND);
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
        placedPos.add(blockPos);
    }

    public enum Mode {
        Normal,
        Own
    }

    private static class BedPlacement {
        public final BlockPos blockPos;
        public final BlockPos upPos;
        public final float damage;

        public BedPlacement(BlockPos blockPos, EntityPlayer entityPlayer) {
            this.blockPos = blockPos;
            this.upPos = blockPos.up();
            this.damage = DamageUtil.calculateDamage(upPos.getX(), upPos.getY(), upPos.getZ(), entityPlayer);
        }
    }
}