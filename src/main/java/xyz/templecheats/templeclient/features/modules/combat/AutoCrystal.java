package xyz.templecheats.templeclient.features.modules.combat;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.features.modules.Module;
import xyz.templecheats.templeclient.gui.clickgui.setting.Setting;

import java.util.List;

public class AutoCrystal extends Module {
    private int crystalPlaceDelay = 0;
    private int placeDelayTicks = 20;
    private int placeRadius = 2;
    private long lastPlaceTime = 0;

    public AutoCrystal() {
        super("AutoCrystal", Keyboard.KEY_NONE, Category.COMBAT);
        Setting delay = new Setting("Delay", this, 10, 1, 20, true);
        Setting radius = new Setting("Radius", this, 2, 1, 6, true);
        TempleClient.instance.settingsManager.rSetting(delay);
        TempleClient.instance.settingsManager.rSetting(radius);
    }

    @Override
    public void onUpdate() {
        placeDelayTicks = TempleClient.instance.settingsManager.getSettingByName(this.getName(), "Delay").getValInt();

        List<Entity> entities = mc.world.getEntitiesWithinAABBExcludingEntity(null, mc.player.getEntityBoundingBox().grow(7));
        boolean playerNearby = false;

        for (Entity entity : entities) {
            if (entity instanceof EntityEnderCrystal) {
                if (mc.player.getDistance(entity) <= 6) {
                    mc.playerController.attackEntity(mc.player, entity);
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                }
                return;
            }
            if (entity instanceof EntityPlayer && entity != mc.player) {
                playerNearby = true;
            }
        }

        if (!playerNearby) {
            return;
        }

        if (System.currentTimeMillis() - lastPlaceTime < (placeDelayTicks * 50)) {
            return;
        }

        lastPlaceTime = System.currentTimeMillis();
        placeCrystalsAroundPlayer();
    }

    private void placeCrystalsAroundPlayer() {
        EntityPlayer target = getTargetPlayer();
        if (target == null) return;

        double maxExplosionRadius = 6.0;
        BlockPos targetPos = target.getPosition();

        BlockPos[] offsets = new BlockPos[]{
                new BlockPos(1, 0, 0),
                new BlockPos(-1, 0, 0),
                new BlockPos(0, 0, 1),
                new BlockPos(0, 0, -1),
                new BlockPos(1, 0, 1),
                new BlockPos(1, 0, -1),
                new BlockPos(-1, 0, 1),
                new BlockPos(-1, 0, -1)
        };

        for (BlockPos offset : offsets) {
            BlockPos pos = targetPos.add(offset);
            double distanceToPlayer = mc.player.getDistance(pos.getX(), pos.getY(), pos.getZ());

            if (distanceToPlayer <= maxExplosionRadius && canPlaceCrystal(pos)) {
                mc.playerController.processRightClickBlock(mc.player, mc.world, pos, EnumFacing.UP, Vec3d.ZERO, EnumHand.MAIN_HAND);
                return;
            }
        }
    }

    private EntityPlayer getTargetPlayer() {
        double range = 6.0;
        for (EntityPlayer player : mc.world.playerEntities) {
            if (player != mc.player && player.getDistance(mc.player) <= range) {
                return player;
            }
        }
        return null;
    }

    private boolean canPlaceCrystal(BlockPos pos) {
        Block block = mc.world.getBlockState(pos).getBlock();
        Block blockBelow = mc.world.getBlockState(pos.down()).getBlock();

        ItemStack stack = mc.player.getHeldItem(EnumHand.MAIN_HAND);
        if (!stack.isEmpty() && stack.getItem() == Items.END_CRYSTAL) {
            if (block instanceof BlockAir && blockBelow == Blocks.BEDROCK) {
                return true;
            }
            if (mc.world.getBlockState(pos).getBlock() instanceof BlockObsidian || block == Blocks.BEDROCK) {
                return true;
            }
        }

        return false;
    }
}
