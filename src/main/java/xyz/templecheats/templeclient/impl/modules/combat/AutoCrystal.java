/**
 * This AutoCrystal was made by GameSense, and was modified.
 */

package xyz.templecheats.templeclient.impl.modules.combat;

import net.minecraft.entity.Entity;
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
import net.minecraft.network.play.server.SPacketSoundEffect;
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
import xyz.templecheats.templeclient.api.event.events.network.PacketEvent;
import xyz.templecheats.templeclient.api.event.events.player.MotionEvent;
import xyz.templecheats.templeclient.api.event.events.world.EntityEvent;
import xyz.templecheats.templeclient.api.setting.Setting;
import xyz.templecheats.templeclient.api.util.autocrystal.*;
import xyz.templecheats.templeclient.api.util.render.RenderUtil;
import xyz.templecheats.templeclient.api.util.time.TimerUtil;
import xyz.templecheats.templeclient.impl.modules.Module;

import java.util.*;

public class AutoCrystal extends Module {
    /**
     * Settings
     */
    private final Setting server = new Setting("Server", this, new ArrayList<>(Arrays.asList("1.12", "1.13+", "crystalpvp.cc")), "1.12");
    private final Setting crystalPriority = new Setting("Prioritise", this, new ArrayList<>(Arrays.asList("Damage", "Closest", "Health")), "Damage");
    private final Setting instant = new Setting("Instant", this, false);
    private final Setting attackSpeed = new Setting("Attack Speed", this, 20, 0, 20, true);
    private final Setting range = new Setting("Range", this, 4.5, 0.0, 6.0, false);
    private final Setting wallRange = new Setting("Wall Range", this, 3.5, 0.0, 6.0, false);
    private final Setting enemyRange = new Setting("Enemy Range", this, 6.0, 0.0, 16.0, false);
    private final Setting antiWeakness = new Setting("Anti Weakness", this, true);
    private final Setting autoSwitch = new Setting("Auto Switch", this, true);
    private final Setting noGapSwitch = new Setting("No Gap Switch", this, false);
    private final Setting minDmg = new Setting("Min Damage", this, 5.0, 0.0, 36.0, false);
    private final Setting maxSelfDmg = new Setting("Max Self Dmg", this, 10.0, 1.0, 36.0, false);
    private final Setting facePlaceValue = new Setting("FacePlace HP", this, 8, 0, 36, true);
    private final Setting armourFacePlace = new Setting("Armour Durability %", this, 20, 0, 100, true);
    private final Setting minFacePlaceDmg = new Setting("Min FacePlace Damage", this, 2.0, 0.0, 10.0, false);
    private final Setting rotate = new Setting("Rotate", this, true);
    private final Setting raytrace = new Setting("Raytrace", this, false);
    private final Setting wait = new Setting("Force Wait", this, true);
    private final Setting timeout = new Setting("Timeout", this, 10, 1, 50, true);
    private final Setting maxTargets = new Setting("Max Targets", this, 2, 1, 5, true);
    private final Setting fill = new Setting("Box Fill", this, true);
    private final Setting outline = new Setting("Box Outline", this, true);
    private final Setting red = new Setting("Box Red", this, 255, 0, 255, true);
    private final Setting green = new Setting("Box Green", this, 0, 0, 255, true);
    private final Setting blue = new Setting("Box Blue", this, 0, 0, 255, true);
    
    /**
     * Variables
     */
    private final TimerUtil timer = new TimerUtil();
    private List<CrystalInfo.PlaceInfo> targets = new ArrayList<>();
    private boolean switchCooldown, isAttacking, rotating, finished;
    private Vec3d lastHitVec = Vec3d.ZERO;
    private BlockPos render;

    private EntityEnderCrystal curCrystal, lastCrystal;
    private BlockPos curPos, lastPos;
    
    public AutoCrystal() {
        super("AutoCrystal", "Automatically places / breaks crystals near enemies and detonates them", Keyboard.KEY_NONE, Category.Combat);
        this.registerSettings(server, crystalPriority, instant, attackSpeed, range, wallRange, enemyRange, antiWeakness, autoSwitch, noGapSwitch, minDmg, maxSelfDmg, facePlaceValue, armourFacePlace, minFacePlaceDmg, rotate, raytrace, wait, timeout, maxTargets, fill, outline, red, green, blue);
    }
    
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if(mc.player == null || mc.world == null || mc.player.isDead) {
            return;
        }
        
        PlayerInfo player = new PlayerInfo(mc.player, false);
        
        ACSettings settings = new ACSettings(enemyRange.getValDouble(), range.getValDouble(), wallRange.getValDouble(), minDmg.getValDouble(), minFacePlaceDmg.getValDouble(), maxSelfDmg.getValDouble(), facePlaceValue.getValInt(), raytrace.getValBoolean(), server.getValString(), crystalPriority.getValString(), player, mc.player.getPositionVector());
        float armourPercent = armourFacePlace.getValInt() / 100.0f;
        double enemyDistance = enemyRange.getValDouble() + range.getValDouble();
        ACHelper.INSTANCE.recalculateValues(settings, player, armourPercent, enemyDistance);
        
        if(event.phase == TickEvent.Phase.START) {
            collectTargetFinder();
        } else {
            if(finished) {
                startTargetFinder();
                finished = false;
            }
        }
        
        // no longer target dead players
        targets.removeIf(placeInfo -> placeInfo.target.entity.isDead || placeInfo.target.entity.getHealth() == 0);
        if(!breakCrystal(settings)) {
            if(!placeCrystal(settings)) {
                rotating = false;
                isAttacking = false;
                render = null;
            }
        }
    }
    
    @Listener
    public void onMotion(MotionEvent event) {
        switch(event.getStage()) {
            case PRE:
                if(this.rotating) {
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
        if(this.instant.getValBoolean() && this.lastPos != null && this.curCrystal == null && event.getEntity() instanceof EntityEnderCrystal) {
            final EntityEnderCrystal crystal = (EntityEnderCrystal) event.getEntity();
            
            if(crystal != null && crystal.getPosition().down().equals(this.lastPos)) {
                final float selfDamage = DamageUtil.calculateDamageThreaded(crystal, new PlayerInfo(mc.player, false));
                
                if(selfDamage > this.maxSelfDmg.getValDouble()) {
                    return;
                }
                
                if(selfDamage >= mc.player.getHealth() + mc.player.getAbsorptionAmount()) {
                    return;
                }
                
                this.breakCrystalInternal(this.curCrystal = crystal, false);
                this.lastPos = null;
            }
        }
    }
    
    @Listener
    public void onEntityDelete(EntityEvent.Delete event) {
        if(this.instant.getValBoolean() && this.lastCrystal != null && this.curPos == null && event.getEntity() instanceof EntityEnderCrystal) {
            final EntityEnderCrystal crystal = (EntityEnderCrystal) event.getEntity();
            
            if(crystal.equals(this.lastCrystal)) {
                final BlockPos pos = crystal.getPosition().down();
                
                if(CrystalUtil.canPlaceCrystal(pos, this.server.getValString(), true)) {
                    final float selfDamage = DamageUtil.calculateDamageThreaded(pos, new PlayerInfo(mc.player, false));
                    
                    if(selfDamage > this.maxSelfDmg.getValDouble()) {
                        return;
                    }
                    
                    if(selfDamage >= mc.player.getHealth() + mc.player.getAbsorptionAmount()) {
                        return;
                    }
                    
                    this.placeCrystalInternal(this.curPos = pos);
                    this.lastCrystal = null;
                }
            }
        }
    }
    
    @Listener
    public void onPacketRecieve(PacketEvent.Receive event) {
        Packet<?> packet = event.getPacket();
        if(packet instanceof SPacketSoundEffect) {
            final SPacketSoundEffect packetSoundEffect = (SPacketSoundEffect) packet;
            if(packetSoundEffect.getCategory() == SoundCategory.BLOCKS && packetSoundEffect.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                for(Entity entity : new ArrayList<>(mc.world.loadedEntityList)) {
                    if(entity instanceof EntityEnderCrystal) {
                        if(entity.getDistanceSq(packetSoundEffect.getX(), packetSoundEffect.getY(), packetSoundEffect.getZ()) <= 36.0f) {
                            entity.setDead();
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void onRenderWorld(float partialTicks) {
        if(this.render != null) {
            RenderUtil.blockESP(this.render, this.fill.getValBoolean(), this.outline.getValBoolean(), true, red.getValInt() / 255F, green.getValInt() / 255F, blue.getValInt() / 255F);
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
    }
    
    public boolean breakCrystal(ACSettings settings) {
        if(targets.size() > 0) {
            List<CrystalInfo.PlaceInfo> currentTargets;
            if(targets.size() < maxTargets.getValInt()) {
                currentTargets = new ArrayList<>(targets);
            } else {
                currentTargets = new ArrayList<>(targets.subList(0, maxTargets.getValInt()));
            }
            List<EntityEnderCrystal> crystals = ACHelper.INSTANCE.getTargetableCrystals();
            
            TreeSet<CrystalInfo.BreakInfo> possibleCrystals;
            String crystalPriorityValue = crystalPriority.getValString();
            if(crystalPriorityValue.equalsIgnoreCase("Health")) {
                possibleCrystals = new TreeSet<>(Comparator.comparingDouble((i) -> -i.target.health));
            } else if(crystalPriorityValue.equalsIgnoreCase("Closest")) {
                possibleCrystals = new TreeSet<>(Comparator.comparingDouble((i) -> -mc.player.getDistanceSq(i.target.entity)));
            } else {
                possibleCrystals = new TreeSet<>(Comparator.comparingDouble((i) -> i.damage));
            }
            
            for(CrystalInfo.PlaceInfo currentTarget : currentTargets) {
                CrystalInfo.BreakInfo breakInfo = ACUtil.calculateBestBreakable(settings, new PlayerInfo(currentTarget.target.entity, currentTarget.target.lowArmour), crystals);
                if(breakInfo != null) {
                    possibleCrystals.add(breakInfo);
                }
            }
            if(possibleCrystals.size() != 0) {
                this.curCrystal = possibleCrystals.last().crystal;
                
                if(this.breakCrystalInternal(this.curCrystal, true)) {
                    this.lastCrystal = this.curCrystal;
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean breakCrystalInternal(EntityEnderCrystal crystal, boolean checkDelay) {
        if(mc.player.canEntityBeSeen(crystal) || mc.player.getDistance(crystal) < wallRange.getValDouble()) {
            if(antiWeakness.getValBoolean() && mc.player.isPotionActive(MobEffects.WEAKNESS) && (!mc.player.isPotionActive(MobEffects.STRENGTH) || mc.player.getActivePotionEffect(MobEffects.STRENGTH)
                                                                                                                                                            .getAmplifier() < 2)) {
                if(!isAttacking) {
                    isAttacking = true;
                }
                // search for sword and tools in hotbar
                int newSlot = InventoryUtil.findFirstItemSlot(ItemSword.class, 0, 8);
                if(newSlot == -1) {
                    InventoryUtil.findFirstItemSlot(ItemTool.class, 0, 8);
                }
                // check if any swords or tools were found
                if(newSlot != -1) {
                    mc.player.inventory.currentItem = newSlot;
                    switchCooldown = true;
                }
            }
            
            if(!checkDelay || timer.getTimePassed() / 50L >= 20 - attackSpeed.getValInt()) {
                timer.reset();
                
                rotating = rotate.getValBoolean();
                lastHitVec = crystal.getPositionVector();
                
                mc.playerController.attackEntity(mc.player, crystal);
                mc.player.swingArm(EnumHand.MAIN_HAND);
            }
            return true;
        }
        
        return false;
    }
    
    private boolean placeCrystal(ACSettings settings) {
        List<CrystalInfo.PlaceInfo> currentTargets;
        if(targets.size() < maxTargets.getValInt()) {
            currentTargets = new ArrayList<>(targets);
        } else {
            currentTargets = targets.subList(0, maxTargets.getValInt());
        }
        List<BlockPos> placements = ACHelper.INSTANCE.getPossiblePlacements();
        
        TreeSet<CrystalInfo.PlaceInfo> possiblePlacements;
        String crystalPriorityValue = crystalPriority.getValString();
        if(crystalPriorityValue.equalsIgnoreCase("Health")) {
            possiblePlacements = new TreeSet<>(Comparator.comparingDouble((i) -> -i.target.health));
        } else if(crystalPriorityValue.equalsIgnoreCase("Closest")) {
            possiblePlacements = new TreeSet<>(Comparator.comparingDouble((i) -> -mc.player.getDistanceSq(i.target.entity)));
        } else {
            possiblePlacements = new TreeSet<>(Comparator.comparingDouble((i) -> i.damage));
        }
        
        for(CrystalInfo.PlaceInfo currentTarget : currentTargets) {
            CrystalInfo.PlaceInfo placeInfo = ACUtil.calculateBestPlacement(settings, new PlayerInfo(currentTarget.target.entity, currentTarget.target.lowArmour), placements);
            if(placeInfo != null) {
                possiblePlacements.add(placeInfo);
            }
        }
        if(possiblePlacements.size() == 0) {
            return false;
        }
        
        this.curPos = possiblePlacements.last().pos;
        
        if(this.placeCrystalInternal(this.curPos)) {
            this.lastPos = this.curPos;
            return true;
        }
        
        return false;
    }
    
    private boolean placeCrystalInternal(BlockPos pos) {
        // check to see if we are holding crystals or not
        int crystalSlot = mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL ? mc.player.inventory.currentItem : -1;
        if(crystalSlot == -1) {
            crystalSlot = InventoryUtil.findFirstItemSlot(ItemEndCrystal.class, 0, 8);
        }
        boolean offhand = false;
        if(mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            offhand = true;
        } else if(crystalSlot == -1) {
            return false;
        }
        
        this.render = pos;
        
        // autoSwitch stuff
        if(!offhand && mc.player.inventory.currentItem != crystalSlot) {
            if(this.autoSwitch.getValBoolean()) {
                if(!noGapSwitch.getValBoolean() || !(mc.player.getHeldItemMainhand().getItem() == Items.GOLDEN_APPLE)) {
                    mc.player.inventory.currentItem = crystalSlot;
                    rotating = false;
                    this.switchCooldown = true;
                }
            }
            return false;
        }
        
        if(this.switchCooldown) {
            this.switchCooldown = false;
            return false;
        }
        
        EnumFacing validFace = mc.player.posY + mc.player.getEyeHeight() > pos.getY() + 0.5 || !mc.world.isAirBlock(pos.down()) ? EnumFacing.UP : EnumFacing.DOWN;
        
        if(raytrace.getValBoolean()) {
            RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ), new Vec3d((double) pos.getX() + 0.5, (double) pos.getY() - 0.5, (double) pos.getZ() + 0.5));
            if(result == null || result.sideHit == null) {
                render = null;
                return false;
            } else {
                validFace = result.sideHit;
            }
        }
        
        rotating = rotate.getValBoolean();
        lastHitVec = new Vec3d(pos).add(0.5, 0.5, 0.5);
        
        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, validFace, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
        mc.player.connection.sendPacket(new CPacketAnimation(offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND));
        
        return true;
    }
    
    private void startTargetFinder() {
        long timeoutTime = System.currentTimeMillis() + timeout.getValInt();
        ACHelper.INSTANCE.startCalculations(timeoutTime);
    }
    
    private void collectTargetFinder() {
        List<CrystalInfo.PlaceInfo> output = ACHelper.INSTANCE.getOutput(wait.getValBoolean());
        if(output != null) {
            finished = true;
            if(output.size() > 0) {
                targets = output;
            }
        } else {
            finished = false;
        }
    }
}