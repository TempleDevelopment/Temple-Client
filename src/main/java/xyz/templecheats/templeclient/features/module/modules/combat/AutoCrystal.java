/*
 * This AutoCrystal was made by GameSense, and was modified.
 */
package xyz.templecheats.templeclient.features.module.modules.combat;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.mixins.accessor.ICPacketUseEntity;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.event.events.network.PacketEvent;
import xyz.templecheats.templeclient.event.events.player.MotionEvent;
import xyz.templecheats.templeclient.event.events.world.EntityEvent;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.render.shader.impl.GradientShader;
import xyz.templecheats.templeclient.util.autocrystal.*;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.util.rotation.RotationUtil;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;
import xyz.templecheats.templeclient.util.time.TimerUtil;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static net.minecraft.network.play.client.CPacketUseEntity.Action.ATTACK;

public class AutoCrystal extends Module {
    public static AutoCrystal INSTANCE;
    /*
     * Settings
     */
    private final BooleanSetting antiWeakness = new BooleanSetting("Anti Weakness", this, true);
    private final BooleanSetting autoSwitch = new BooleanSetting("Auto Switch", this, true);
    private final BooleanSetting fill = new BooleanSetting("Box Fill", this, true);
    private final BooleanSetting inhibit = new BooleanSetting("Inhibit", this, true);
    private final BooleanSetting predict = new BooleanSetting("Predict", this, false);

    private final BooleanSetting instant = new BooleanSetting("Instant", this, false);

    private final BooleanSetting setDead = new BooleanSetting("Set Dead", this, false);

    private final BooleanSetting noGapSwitch = new BooleanSetting("No Gap Switch", this, false);
    private final BooleanSetting outline = new BooleanSetting("Box Outline", this, true);
    private final BooleanSetting raytrace = new BooleanSetting("Raytrace", this, false);
    private final BooleanSetting rotate = new BooleanSetting("Rotate", this, true);
    private final BooleanSetting wait = new BooleanSetting("Force Wait", this, true);
    private final DoubleSetting enemyRange = new DoubleSetting("Enemy Range", this, 0.0, 16.0, 6.0);
    private final DoubleSetting maxSelfDmg = new DoubleSetting("Max Self Dmg", this, 1.0, 36.0, 10.0);
    private final DoubleSetting minDmg = new DoubleSetting("Min Dmg", this, 0.0, 36.0, 5.0);
    private final DoubleSetting minFacePlaceDmg = new DoubleSetting("Min FacePlace Dmg", this, 0.0, 10.0, 2.0);
    private final DoubleSetting defaultOpacityVal = new DoubleSetting("DefaultOpacity", this, 0.0, 1, 0.5);
    private final DoubleSetting opacity = new DoubleSetting("Opacity", this, 0.0, 1, 0.5);

    private final DoubleSetting range = new DoubleSetting("Range", this, 0.0, 6.0, 4.5);
    private final DoubleSetting wallRange = new DoubleSetting("Wall Range", this, 0.0, 6.0, 3.5);
    private final IntSetting armourFacePlace = new IntSetting("Armour Dura %", this, 0, 100, 20);
    private final IntSetting attackSpeed = new IntSetting("Attack Speed", this, 0, 20, 20);
    private final IntSetting facePlaceValue = new IntSetting("FacePlace HP", this, 0, 36, 8);
    private final IntSetting maxTargets = new IntSetting("Max Targets", this, 1, 5, 2);
    private final IntSetting timeout = new IntSetting("Timeout", this, 1, 50, 10);
    private final EnumSetting < Priority > crystalPriority = new EnumSetting < > ("Prioritise", this, Priority.Damage);
    private final EnumSetting < Server > server = new EnumSetting < > ("Server", this, Server.OneTwelve);
    public static boolean rendering;
    /*
     * Variables
     */
    private final TimerUtil timer = new TimerUtil();
    private List < CrystalInfo.PlaceInfo > targets = new ArrayList <> ();
    private final IntSet attackedCrystals = new IntOpenHashSet();
    private final ObjectSet<BlockPos> placedPos = new ObjectOpenHashSet<>();
    private boolean switchCooldown, isAttacking, rotating, finished;
    private Vec3d lastHitVec = Vec3d.ZERO;
    private BlockPos render;
    private EntityEnderCrystal curCrystal, lastCrystal;
    private BlockPos curPos, lastPos, lastRenderPos;
    boolean offhand = false;


    public AutoCrystal() {
        super("AutoCrystal", "Automatically places and explodes end crystals for crystal pvp", Keyboard.KEY_NONE, Category.Combat);
        INSTANCE = this;
        this.registerSettings(antiWeakness, autoSwitch, instant, inhibit, noGapSwitch, predict, raytrace, rotate, setDead, wait,
                attackSpeed, armourFacePlace, facePlaceValue, maxTargets, timeout,
                enemyRange, defaultOpacityVal, maxSelfDmg, minDmg, minFacePlaceDmg, range, wallRange, fill, outline, opacity, crystalPriority, server);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.player == null || mc.world == null || mc.player.isDead) {
            return;
        }

        PlayerInfo player = new PlayerInfo(mc.player, false);

        ACSettings settings = new ACSettings(enemyRange.doubleValue(), range.doubleValue(), wallRange.doubleValue(), minDmg.doubleValue(), minFacePlaceDmg.doubleValue(), maxSelfDmg.doubleValue(), facePlaceValue.intValue(), raytrace.booleanValue(), server.value(), crystalPriority.value(), player, mc.player.getPositionVector());
        float armourPercent = armourFacePlace.intValue() / 100.0f;
        double enemyDistance = enemyRange.doubleValue() + range.doubleValue();
        ACHelper.INSTANCE.recalculateValues(settings, player, armourPercent, enemyDistance);

        if (event.phase == TickEvent.Phase.START) {
            collectTargetFinder();
        } else {
            if (finished) {
                startTargetFinder();
                finished = false;
            }
        }

        targets.removeIf(placeInfo -> placeInfo.target.entity.isDead || placeInfo.target.entity.getHealth() == 0 || TempleClient.friendManager.isFriend(placeInfo.target.entity.getName()));
        if (!breakCrystal(settings)) {
            if (!placeCrystal(settings)) {
                rotating = false;
                isAttacking = false;
                render = null;
            }
        }
    }

    @Listener
    public void onMotion(MotionEvent event) {
        switch (event.getStage()) {
            case PRE:
                if (this.rotating) {
                    final Vec2f rotation = RotationUtil.getRotationTo(lastHitVec);
                    event.setYaw(rotation.x);
                    event.setPitch(rotation.y);
                }
            case POST:
                this.curCrystal = null;
                this.curPos = null;
        }
    }

    @Listener
    public void onEntityAdd(EntityEvent.Add event) {
        if (this.instant.booleanValue() && this.lastPos != null && this.curCrystal == null && event.getEntity() instanceof EntityEnderCrystal) {
            final EntityEnderCrystal crystal = (EntityEnderCrystal) event.getEntity();

            if (crystal != null && crystal.getPosition().down().equals(this.lastPos)) {
                final float selfDamage = DamageUtil.calculateDamageThreaded(crystal, new PlayerInfo(mc.player, false));

                if (selfDamage > this.maxSelfDmg.doubleValue()) {
                    return;
                }

                if (selfDamage >= mc.player.getHealth() + mc.player.getAbsorptionAmount()) {
                    return;
                }
                if (inhibit.booleanValue() && attackedCrystals.contains(crystal.getEntityId())) return;

                this.breakCrystalInternal(this.curCrystal = crystal, false);
                this.lastPos = null;
            }
        }
    }
    private boolean crystalPlacedThisTick = false;
    @Listener
    public void onEntityDelete(EntityEvent.Delete event) {
        if (this.instant.booleanValue() && this.lastCrystal != null && this.curPos == null && event.getEntity() instanceof EntityEnderCrystal) {
            final EntityEnderCrystal crystal = (EntityEnderCrystal) event.getEntity();

            if (crystal.equals(this.lastCrystal)) {
                final BlockPos pos = crystal.getPosition().down();

                if (CrystalUtil.canPlaceCrystal(pos, this.server.value(), true)) {
                    final float selfDamage = DamageUtil.calculateDamageThreaded(pos, new PlayerInfo(mc.player, false));

                    if (selfDamage > this.maxSelfDmg.doubleValue()) {
                        return;
                    }

                    if (selfDamage >= mc.player.getHealth() + mc.player.getAbsorptionAmount()) {
                        return;
                    }

                    if (!crystalPlacedThisTick) {
                        this.placeCrystalInternal(pos);
                        this.lastCrystal = null;
                        crystalPlacedThisTick = true;
                    }
                }
            }
        }
    }


    @Listener
    public void onPacketRecieve(PacketEvent.Receive event) {
        Packet<?> packet = event.getPacket();
        if (event.getPacket() instanceof SPacketSpawnObject && predict.booleanValue()) {
            final SPacketSpawnObject spawnPacket = (SPacketSpawnObject) event.getPacket();
            if (spawnPacket.getType() != 51) {
                return;
            }
            if (inhibit.booleanValue() && attackedCrystals.contains(spawnPacket.getEntityID())) return;
            final BlockPos pos = new BlockPos(spawnPacket.getX(), spawnPacket.getY() - 1, spawnPacket.getZ());
            if (placedPos.remove(pos)) {
                final ICPacketUseEntity packetUseEntity = (ICPacketUseEntity) new CPacketUseEntity();
                packetUseEntity.setEntityId(spawnPacket.getEntityID());
                packetUseEntity.setAction(ATTACK);
                mc.getConnection().sendPacket((Packet<?>) packetUseEntity);
                if (lastPos != null && lastPos.equals(placedPos)) {
                    mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(lastPos, EnumFacing.UP, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 1.0f, 0.5f));
                }
                mc.player.swingArm(offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
            }
            if (setDead.booleanValue()) {
                mc.world.removeEntityFromWorld(spawnPacket.getEntityID());
            }
        }
        if (packet instanceof SPacketSoundEffect) {
            final SPacketSoundEffect packetSoundEffect = (SPacketSoundEffect) packet;
            if (packetSoundEffect.getCategory() == SoundCategory.BLOCKS && packetSoundEffect.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                for (Entity entity : new ArrayList<>(mc.world.loadedEntityList)) {
                    if (entity instanceof EntityEnderCrystal) {
                        if (entity.getDistanceSq(packetSoundEffect.getX(), packetSoundEffect.getY(), packetSoundEffect.getZ()) <= 36.0f) {
                            entity.setDead();
                        }
                    }
                }
            }
        }
    }


    @Override
    public void onRenderWorld(float partialTicks) {
        if (render != null) {
            opacity.setDoubleValue(defaultOpacityVal.doubleValue());
        rendering = false;
        GradientShader.setup(opacity.floatValue());
        rendering = true;
        if (fill.booleanValue())
            RenderUtil.boxShader(render);
        if (outline.booleanValue()) {
            RenderUtil.outlineShader(render);
            RenderUtil.outlineShader(render);
            RenderUtil.outlineShader(render);
        }
        GradientShader.finish();
        lastRenderPos = render;
        rendering = false;
    } else {
            if (opacity.floatValue() > 0.0F) {
                opacity.setDoubleValue(opacity.doubleValue() - 0.01F * partialTicks);
                GradientShader.setup(opacity.floatValue());
                if (fill.booleanValue())
                    RenderUtil.boxShader(lastRenderPos);
                if (outline.booleanValue()) {
                    RenderUtil.outlineShader(lastRenderPos);
                    RenderUtil.outlineShader(lastRenderPos);
                    RenderUtil.outlineShader(lastRenderPos);
                }
                GradientShader.finish();
            }
        }
    }

    @Override
    public void onEnable() {
        ACHelper.INSTANCE.onEnable();
    }

    @Override
    public void onDisable() {
        ACHelper.INSTANCE.onDisable();

        render = null;
        rotating = false;
        targets.clear();
        attackedCrystals.clear();
    }

    public boolean breakCrystal(ACSettings settings) {
        if (!targets.isEmpty()) {
            List < CrystalInfo.PlaceInfo > currentTargets;
            if (targets.size() < maxTargets.intValue()) {
                currentTargets = new ArrayList < > (targets);
            } else {
                currentTargets = targets.subList(0, maxTargets.intValue());
            }
            List < EntityEnderCrystal > crystals = ACHelper.INSTANCE.getTargetableCrystals();

            TreeSet < CrystalInfo.BreakInfo > possibleCrystals = new TreeSet < > (crystalPriority.value().breakComparator);

            for (CrystalInfo.PlaceInfo currentTarget: currentTargets) {
                CrystalInfo.BreakInfo breakInfo = ACUtil.calculateBestBreakable(settings, new PlayerInfo(currentTarget.target.entity, currentTarget.target.lowArmour), crystals);
                if (breakInfo != null) {
                    possibleCrystals.add(breakInfo);
                }
            }

            if (!possibleCrystals.isEmpty()) {
                this.curCrystal = possibleCrystals.last().crystal;

                if (this.breakCrystalInternal(this.curCrystal, true)) {
                    this.lastCrystal = this.curCrystal;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean breakCrystalInternal(EntityEnderCrystal crystal, boolean checkDelay) {
        if (mc.player.canEntityBeSeen(crystal) || mc.player.getDistance(crystal) < wallRange.doubleValue()) {
            if (antiWeakness.booleanValue() && mc.player.isPotionActive(MobEffects.WEAKNESS) && (!mc.player.isPotionActive(MobEffects.STRENGTH) || mc.player.getActivePotionEffect(MobEffects.STRENGTH)
                    .getAmplifier() < 2)) {
                if (!isAttacking) {
                    isAttacking = true;
                }
                // search for sword and tools in hotbar
                int newSlot = InventoryUtil.findFirstItemSlot(ItemSword.class, 0, 8);
                if (newSlot == -1) {
                    InventoryUtil.findFirstItemSlot(ItemTool.class, 0, 8);
                }
                // check if any swords or tools were found
                if (newSlot != -1) {
                    mc.player.inventory.currentItem = newSlot;
                    switchCooldown = true;
                }
            }

            if (!checkDelay || timer.getTimePassed() / 50L >= 20 - attackSpeed.intValue()) {
                timer.reset();

                rotating = rotate.booleanValue();
                lastHitVec = crystal.getPositionVector();
                if (!inhibit.booleanValue() || inhibit.booleanValue() && attackedCrystals.contains(crystal.getEntityId())) {
                        mc.playerController.attackEntity(mc.player, crystal);
                    if (setDead.booleanValue()) mc.world.removeEntityFromWorld(crystal.getEntityId());
                }
                attackedCrystals.add(crystal.getEntityId());
                if (setDead.booleanValue() && !inhibit.booleanValue()) {
                    mc.world.removeEntity(crystal);
                    crystal.setDead();
                }
                if (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL)
                    mc.player.swingArm(EnumHand.OFF_HAND);
                else
                    mc.player.swingArm(EnumHand.MAIN_HAND);
            }
            return true;
        }
        return false;
    }

    private boolean placeCrystal(ACSettings settings) {
        List < CrystalInfo.PlaceInfo > currentTargets;
        if (targets.size() < maxTargets.intValue()) {
            currentTargets = new ArrayList < > (targets);
        } else {
            currentTargets = targets.subList(0, maxTargets.intValue());
        }
        List < BlockPos > placements = ACHelper.INSTANCE.getPossiblePlacements();

        TreeSet < CrystalInfo.PlaceInfo > possiblePlacements = new TreeSet < > (crystalPriority.value().placeComparator);

        for (CrystalInfo.PlaceInfo currentTarget: currentTargets) {
            CrystalInfo.PlaceInfo placeInfo = ACUtil.calculateBestPlacement(settings, new PlayerInfo(currentTarget.target.entity, currentTarget.target.lowArmour), placements);
            if (placeInfo != null) {
                placedPos.add(placeInfo.pos);
                possiblePlacements.add(placeInfo);
            }
        }

        if (possiblePlacements.isEmpty()) {
            return false;
        }

        this.curPos = possiblePlacements.last().pos;

        if (this.placeCrystalInternal(this.curPos)) {
            this.lastPos = this.curPos;
            return true;
        }

        return false;
    }

    private boolean placeCrystalInternal(BlockPos pos) {
        // check to see if we are holding crystals or not
        int crystalSlot = mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL ? mc.player.inventory.currentItem : -1;
        if (crystalSlot == -1) {
            crystalSlot = InventoryUtil.findFirstItemSlot(ItemEndCrystal.class, 0, 8);
        }
        offhand = false;
        if (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            offhand = true;
        } else if (crystalSlot == -1) {
            return false;
        }
        placedPos.add(pos);
        this.render = pos;

        // autoSwitch stuff
        if (!offhand && mc.player.inventory.currentItem != crystalSlot) {
            if (this.autoSwitch.booleanValue()) {
                if (!noGapSwitch.booleanValue() || !(mc.player.getHeldItemMainhand().getItem() == Items.GOLDEN_APPLE)) {
                    mc.player.inventory.currentItem = crystalSlot;
                    rotating = false;
                    this.switchCooldown = true;
                }
            }
            return false;
        }

        if (this.switchCooldown) {
            this.switchCooldown = false;
            return false;
        }

        EnumFacing validFace = mc.player.posY + mc.player.getEyeHeight() > pos.getY() + 0.5 || !mc.world.isAirBlock(pos.down()) ? EnumFacing.UP : EnumFacing.DOWN;

        if (raytrace.booleanValue()) {
            RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ), new Vec3d((double) pos.getX() + 0.5, (double) pos.getY() - 0.5, (double) pos.getZ() + 0.5));
            if (result == null || result.sideHit == null) {
                render = null;
                return false;
            } else {
                validFace = result.sideHit;
            }
        }

        rotating = rotate.booleanValue();
        lastHitVec = new Vec3d(pos).add(0.5, 0.5, 0.5);

        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, validFace, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
        mc.player.connection.sendPacket(new CPacketAnimation(offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND));

        return true;
    }

    private void startTargetFinder() {
        long timeoutTime = System.currentTimeMillis() + timeout.intValue();
        ACHelper.INSTANCE.startCalculations(timeoutTime);
    }

    private void collectTargetFinder() {
        List < CrystalInfo.PlaceInfo > output = ACHelper.INSTANCE.getOutput(wait.booleanValue());
        if (output != null) {
            finished = true;
            if (!output.isEmpty()) {
                targets = output;
            }
        } else {
            finished = false;
        }
    }

    public EntityLivingBase getTarget() {
        AtomicReference <EntityLivingBase> target = new AtomicReference<>();
        targets.forEach(it -> {
            target.set(it.target.entity);
        });
        return target.get();
    }

    public enum Priority {
        Damage(Comparator.comparingDouble(o -> -o.target.health), Comparator.comparingDouble(o -> -o.target.health)),
        Closest(Comparator.comparingDouble(o -> -mc.player.getDistanceSq(o.target.entity)), Comparator.comparingDouble(o -> -mc.player.getDistanceSq(o.target.entity))),
        Health(Comparator.comparingDouble(o -> o.damage), Comparator.comparingDouble(o -> o.damage));

        public final Comparator < CrystalInfo.PlaceInfo > placeComparator;
        public final Comparator < CrystalInfo.BreakInfo > breakComparator;

        Priority(Comparator < CrystalInfo.PlaceInfo > placeComparator, Comparator < CrystalInfo.BreakInfo > breakComparator) {
            this.placeComparator = placeComparator;
            this.breakComparator = breakComparator;
        }
    }

    public enum Server {
        OneTwelve("1.12"),
        OneThirteen("1.13+"),
        Crystalpvp_cc("crystalpvp.cc");

        private final String name;

        Server(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
