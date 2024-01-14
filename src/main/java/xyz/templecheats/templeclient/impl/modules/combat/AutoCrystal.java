package xyz.templecheats.templeclient.impl.modules.combat;

import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.api.event.events.network.PacketEvent;
import xyz.templecheats.templeclient.api.event.events.player.MotionEvent;
import xyz.templecheats.templeclient.impl.gui.clickgui.setting.Setting;
import xyz.templecheats.templeclient.impl.modules.Module;

import java.util.ArrayList;
import java.util.List;

public class AutoCrystal extends Module {
    private int breakDelayTicks = 20;
    private int placeDelayTicks = 20;
    private long lastBreakTime = 0;
    private long lastPlaceTime = 0;
    private boolean attackPlayers = true;
    private boolean silent = false;
    private double radius = 4.5;
    private double minDamage = 6.0;
    private double maxSelfDamage = 4.0;
    private boolean rotate = true;
    private boolean autoSwitch = true;
    private String mode = "1.12";

    private EntityEnderCrystal curCrystal;
    private BlockPos curPos;
    private EnumHand placeHand;

    public AutoCrystal() {
        super("AutoCrystal", Keyboard.KEY_NONE, Category.COMBAT);

        ArrayList<String> options = new ArrayList<>();
        options.add("1.12");
        options.add("1.13+");
        options.add("crystalpvp.cc");

        Setting breakDelaySetting = new Setting("BreakDelay", this, 10, 0, 20, true);
        Setting placeDelaySetting = new Setting("PlaceDelay", this, 10, 0, 20, true);
        Setting radiusSetting = new Setting("Radius", this, 4.5, 1, 6, false);
        Setting rotate = new Setting("Rotate", this, true);
        Setting autoSwitchSetting = new Setting("AutoSwitch", this, true);
        Setting silentSetting = new Setting("Silent", this, false);
        Setting minDamageSetting = new Setting("MinDamage", this, 6.0, 0.1, 36.0, false);
        Setting maxSelfDamageSetting = new Setting("MaxSelfDamage", this, 6.0, 0.1, 36.0, false);


        TempleClient.settingsManager.rSetting(new Setting("Mode", this, options, "Mode"));
        TempleClient.settingsManager.rSetting(rotate);
        TempleClient.settingsManager.rSetting(autoSwitchSetting);
        TempleClient.settingsManager.rSetting(silentSetting);
        TempleClient.settingsManager.rSetting(minDamageSetting);
        TempleClient.settingsManager.rSetting(maxSelfDamageSetting);
        TempleClient.settingsManager.rSetting(breakDelaySetting);
        TempleClient.settingsManager.rSetting(placeDelaySetting);
        TempleClient.settingsManager.rSetting(radiusSetting);
    }

    @Listener
    public void onMotion(MotionEvent event) {
        switch(event.getStage()) {
            case PRE:
                this.mode = TempleClient.settingsManager.getSettingByName(this.getName(), "Mode").getValString();
                this.rotate = TempleClient.settingsManager.getSettingByName(this.name, "Rotate").getValBoolean();
                this.autoSwitch = TempleClient.settingsManager.getSettingByName(this.getName(), "AutoSwitch").getValBoolean();
                this.silent = TempleClient.settingsManager.getSettingByName(this.getName(), "Silent").getValBoolean();
                this.breakDelayTicks = TempleClient.settingsManager.getSettingByName(this.getName(), "BreakDelay").getValInt();
                this.placeDelayTicks = TempleClient.settingsManager.getSettingByName(this.getName(), "PlaceDelay").getValInt();
                this.radius = TempleClient.settingsManager.getSettingByName(this.getName(), "Radius").getValDouble();
                this.minDamage = TempleClient.settingsManager.getSettingByName(this.getName(), "MinDamage").getValDouble();
                this.maxSelfDamage = TempleClient.settingsManager.getSettingByName(this.getName(), "MaxSelfDamage").getValDouble();

                final EntityPlayer target = this.getTargetPlayer();
                if(target == null) {
                    return;
                }

                this.curCrystal = this.getTargetCrystal(target);

                if(this.curCrystal == null) {
                    final int crystalSlot = this.getCrystalHotbarSlot();

                    if(crystalSlot != -999 && (this.autoSwitch || crystalSlot == -1 || crystalSlot == mc.player.inventory.currentItem)) {
                        this.curPos = this.getTargetPos(target);
                        this.placeHand = crystalSlot != -1 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;

                        if(this.curPos != null && crystalSlot != -1) {
                            mc.player.inventory.currentItem = crystalSlot;
                        }
                    }
                }

                if((this.curCrystal == null && this.curPos == null) || !this.rotate) {
                    return;
                }

                final float[] rotations = this.curCrystal != null ? rotations(this.curCrystal) : rotations(this.curPos);
                event.setYaw(rotations[0]);
                event.setPitch(rotations[1]);
                break;
            case POST:
                if(this.curCrystal != null) {
                    if(this.silent) {
                        mc.player.connection.sendPacket(new CPacketUseEntity(this.curCrystal));
                        mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                    } else {
                        mc.playerController.attackEntity(mc.player, this.curCrystal);
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                    }
                    this.lastBreakTime = System.currentTimeMillis();
                } else if(this.curPos != null) {
                    if(this.silent) {
                        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(this.curPos, EnumFacing.UP, this.placeHand, 0, 0, 0));
                        mc.player.connection.sendPacket(new CPacketAnimation(this.placeHand));
                    } else {
                        mc.playerController.processRightClickBlock(mc.player, mc.world, this.curPos, EnumFacing.UP, Vec3d.ZERO, this.placeHand);
                        mc.player.swingArm(this.placeHand);
                    }
                    this.lastPlaceTime = System.currentTimeMillis();
                }

                this.curCrystal = null;
                this.curPos = null;
                break;
        }
    }

    @Listener
    public void onPacketRecieve(PacketEvent.Receive event) {
        if(event.getPacket() instanceof SPacketSoundEffect && mc.world != null) {
            final SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
            if(packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                for(Entity e : mc.world.loadedEntityList) {
                    if(e instanceof EntityEnderCrystal) {
                        if(e.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= 3.0F) {
                            e.setDead();
                        }
                    }
                }
            }
        }
    }

    private int getCrystalHotbarSlot() {
        final ItemStack mainHand = mc.player.getHeldItem(EnumHand.MAIN_HAND);
        if(!mainHand.isEmpty() && mainHand.getItem() == Items.END_CRYSTAL) {
            return mc.player.inventory.currentItem;
        }

        final ItemStack offHand = mc.player.getHeldItem(EnumHand.OFF_HAND);
        if(!offHand.isEmpty() && offHand.getItem() == Items.END_CRYSTAL) {
            return -1;
        }

        for(int slot = 0; slot < 9; slot++) {
            if(mc.player.inventory.getStackInSlot(slot).getItem() == Items.END_CRYSTAL) {
                return slot;
            }
        }

        return -999;
    }

    private EntityPlayer getTargetPlayer() {
        final double range = this.radius + 12;

        for(EntityPlayer player : mc.world.playerEntities) {
            if(!player.equals(mc.player) && player.getDistance(mc.player) <= range) {
                return player;
            }
        }

        return null;
    }

    private EntityEnderCrystal getTargetCrystal(EntityLivingBase target) {
        if(System.currentTimeMillis() - this.lastBreakTime < (this.breakDelayTicks * 50L)) {
            return null;
        }

        final List<Entity> entities = mc.world.getEntitiesWithinAABBExcludingEntity(mc.player, mc.player.getEntityBoundingBox().grow(7));

        for(Entity entity : entities) {
            if(entity instanceof EntityEnderCrystal) {
                if(mc.player.getDistance(entity) <= radius) {
                    return (EntityEnderCrystal) entity;
                }
            }
        }

        return null;
    }

    private BlockPos getTargetPos(EntityLivingBase target) {
        if(System.currentTimeMillis() - this.lastPlaceTime < (this.placeDelayTicks * 50L)) {
            return null;
        }

        final List<BlockPos> validPositions = getValidCrystalPositions(target.getPosition(), (float) this.radius, (int) this.radius, false, true, 0);
        final List<Pair<BlockPos, Float>> positionsAndDamage = new ArrayList<>();

        for(BlockPos pos : validPositions) {
            final float potentialDamage = calculateDamageForCrystalAtBlock(target, pos);

            if(potentialDamage < this.minDamage) {
                continue;
            }

            final float selfDamage = calculateDamageForCrystalAtBlock(mc.player, pos);

            if(selfDamage > this.maxSelfDamage) {
                continue;
            }

            if(selfDamage >= mc.player.getHealth() + mc.player.getAbsorptionAmount()) {
                continue;
            }

            positionsAndDamage.add(new Pair<>(pos, potentialDamage));
        }

        positionsAndDamage.sort((pair1, pair2) -> Float.compare(pair2.getRight(), pair1.getRight()));

        for(Pair<BlockPos, Float> pair : positionsAndDamage) {
            final BlockPos pos = pair.getLeft();
            if(mc.player.getDistance(pos.getX(), pos.getY(), pos.getZ()) <= radius) {
                return pos;
            }
        }

        return null;
    }

    public List<BlockPos> getValidCrystalPositions(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        List<BlockPos> validPositions = new ArrayList<>();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();

        for(int x = cx - (int) r; x <= cx + r; x++) {
            for(int z = cz - (int) r; z <= cz + r; z++) {
                for(int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if(dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        BlockPos potentialPos = new BlockPos(x, y + plus_y, z);
                        if(canPlaceCrystal(potentialPos)) {
                            validPositions.add(potentialPos);
                        }
                    }
                }
            }
        }
        return validPositions;
    }

    private boolean canPlaceCrystal(BlockPos pos) {
        final Block block = mc.world.getBlockState(pos).getBlock();

        if(block != Blocks.OBSIDIAN && block != Blocks.BEDROCK) {
            return false;
        }

        final BlockPos abovePos = pos.up();
        final Block aboveBlock = mc.world.getBlockState(abovePos).getBlock();

        if(!mc.world.isAirBlock(abovePos) && !aboveBlock.isReplaceable(mc.world, abovePos)) {
            return false;
        }

        if(this.mode.equals("1.12")) {
            final BlockPos above2Pos = abovePos.up();
            final Block above2Block = mc.world.getBlockState(above2Pos).getBlock();

            if(!mc.world.isAirBlock(above2Pos) && !above2Block.isReplaceable(mc.world, above2Pos)) {
                return false;
            }
        }

        final double x = pos.getX();
        final double y = pos.getY();
        final double z = pos.getZ();
        final double offset = this.mode.equals("crystalpvp.cc") ? 1.0 : 2.0;
        final List<Entity> entities = mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(x, y, z, x + 1.0, y + offset, z + 1.0));

        return entities.isEmpty();
    }

    private float calculateDamage(EntityEnderCrystal crystal, Entity entity) {
        return calculateDamage(crystal.posX, crystal.posY, crystal.posZ, entity);
    }

    private float calculateDamageForCrystalAtBlock(Entity entity, BlockPos blockPos) {
        double crystalPosX = blockPos.getX() + 0.5;
        double crystalPosY = blockPos.getY() + 1;
        double crystalPosZ = blockPos.getZ() + 0.5;

        return calculateDamage(crystalPosX, crystalPosY, crystalPosZ, entity);
    }

    private float calculateDamage(double posX, double posY, double posZ, Entity entity) {
        float doubleExplosionSize = 6.0F * 2.0F;
        double distancedsize = entity.getDistance(posX, posY, posZ) / (double) doubleExplosionSize;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        double v = (1.0D - distancedsize) * blockDensity;
        float damage = (float) ((int) ((v * v + v) / 2.0D * 7.0D * (double) doubleExplosionSize + 1.0D));
        double finald = 1;

        if(entity instanceof EntityLivingBase) {
            finald = getBlastReduction((EntityLivingBase) entity, getDamageMultiplied(damage));
        }
        return (float) finald;
    }

    private static final DamageSource EXPLOSION_SOURCE = new DamageSource("explosion").setDifficultyScaled().setExplosion();

    private float getBlastReduction(EntityLivingBase entity, float damage) {
        damage = CombatRules.getDamageAfterAbsorb(damage, entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS)
                .getAttributeValue());
        int test = 0;
        try {
            test = EnchantmentHelper.getEnchantmentModifierDamage(entity.getArmorInventoryList(), EXPLOSION_SOURCE);
        } catch(Exception ignored) {
        }
        damage *= 1.0f - MathHelper.clamp(test, 0.0f, 20.0f) / 25.0f;
        if(entity.isPotionActive(MobEffects.RESISTANCE)) {
            return damage - damage / 4.0f;
        }
        return damage;
    }

    private float getDamageMultiplied(float damage) {
        switch(mc.world.getDifficulty()) {
            case EASY:
                damage = Math.min(damage / 2.0F + 1.0F, damage);
                break;
            case NORMAL:
                break;
            case PEACEFUL:
            case HARD:
                damage = damage * 3.0F / 2.0F;
                break;
        }

        return damage;
    }

    public static float[] rotations(Entity entity) {
        double x = entity.posX - mc.player.posX;
        double y = entity.posY - (mc.player.posY + mc.player.getEyeHeight());
        double z = entity.posZ - mc.player.posZ;

        double u = MathHelper.sqrt(x * x + z * z);

        float u2 = (float) (MathHelper.atan2(z, x) * (180D / Math.PI) - 90.0F);
        float u3 = (float) (-MathHelper.atan2(y, u) * (180D / Math.PI));

        return new float[]{u2, u3};
    }

    public static float[] rotations(BlockPos pos) {
        double x = pos.getX() + 0.5 - mc.player.posX;
        double y = pos.getY() + 0.5 - (mc.player.posY + mc.player.getEyeHeight());
        double z = pos.getZ() + 0.5 - mc.player.posZ;

        double u = MathHelper.sqrt(x * x + z * z);

        float u2 = (float) (MathHelper.atan2(z, x) * (180D / Math.PI) - 90.0F);
        float u3 = (float) (-MathHelper.atan2(y, u) * (180D / Math.PI));

        return new float[]{u2, u3};
    }

    private static class Pair<L, R> {
        private final L left;
        private final R right;

        public Pair(L left, R right) {
            this.left = left;
            this.right = right;
        }

        public L getLeft() {
            return left;
        }

        public R getRight() {
            return right;
        }
    }
}
