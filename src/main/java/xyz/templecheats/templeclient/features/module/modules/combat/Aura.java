package xyz.templecheats.templeclient.features.module.modules.combat;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import org.lwjgl.input.Keyboard;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.event.events.player.MotionEvent;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.rotation.RotationUtil;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;

import java.util.Comparator;

public class Aura extends Module {
    private final BooleanSetting ignoreWalls = new BooleanSetting("Ignore Walls", this, true);
    private final BooleanSetting rotate = new BooleanSetting("Rotate", this, true);
    private final BooleanSetting onlyCritical = new BooleanSetting("Only Critical", this, false);
    private final BooleanSetting disableOnDeath = new BooleanSetting("Disable On Death", this, false);
    private final BooleanSetting players = new BooleanSetting("Players", this, true);
    private final BooleanSetting animals = new BooleanSetting("Animals", this, false);
    private final BooleanSetting mobs = new BooleanSetting("Mobs", this, false);
    private final IntSetting waitTick = new IntSetting("Wait Tick", this, 0, 20, 0);
    private final DoubleSetting range = new DoubleSetting("Range", this, 0.1d, 6d, 4.5d);
    private final EnumSetting<WaitMode> waitMode = new EnumSetting<>("Wait Mode", this, WaitMode.Dynamic);
    private int waitCounter;
    private EntityLivingBase target, renderTarget;

    public Aura() {
        super("Aura","Automatically aims and attacks enemies", Keyboard.KEY_NONE, Category.Combat);

        registerSettings(ignoreWalls, rotate, onlyCritical, disableOnDeath, players, animals, mobs, waitTick, range, waitMode);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        waitCounter = 0;
    }

    @Listener
    public void onMotion(MotionEvent event) {
        if (disableOnDeath.booleanValue() && mc.player.getHealth() <= 0) {
            this.disable();
            return;
        }

        switch (event.getStage()) {
            case PRE:
                if (waitMode.value() == WaitMode.Dynamic) {
                    if (mc.player.getCooledAttackStrength(0) < 1) {
                        return;
                    }
                } else if (waitTick.intValue() > 0) {
                    if (waitCounter < waitTick.intValue()) {
                        waitCounter++;
                        return;
                    } else {
                        waitCounter = 0;
                    }
                }

                this.target = mc.world.loadedEntityList.stream()
                        .filter(entity -> entity instanceof EntityLivingBase && !entity.equals(mc.player) && (!(entity instanceof EntityOtherPlayerMP) || !((EntityOtherPlayerMP) entity).getGameProfile().equals(mc.player.getGameProfile())))
                        .map(entity -> (EntityLivingBase) entity)
                        .filter(entity -> {
                            double distance = entity.getDistance(mc.player);
                            return distance <= range.doubleValue() && ((players.booleanValue() && entity instanceof EntityPlayer && !TempleClient.friendManager.isFriend(entity.getName()))
                                    || (animals.booleanValue() && entity instanceof EntityAnimal)
                                    || (mobs.booleanValue() && !(entity instanceof EntityPlayer) && !(entity instanceof EntityAnimal)))
                                    && (!ignoreWalls.booleanValue() || mc.player.canEntityBeSeen(entity));
                        })
                        .min(Comparator.comparing(entity -> entity.getDistance(mc.player)))
                        .orElse(null);

                this.renderTarget = this.target;

                if(this.target == null) {
                    return;
                }

                if(mc.player.onGround && onlyCritical.booleanValue()) {
                    mc.player.motionY = 0.15;
                }

                if(this.rotate.booleanValue()) {
                    final float[] rotations = RotationUtil.getRotations(this.target);
                    event.setYaw(rotations[0]);
                    event.setPitch(rotations[1]);
                }
                break;
            case POST:
                if(this.target != null) {
                    mc.playerController.attackEntity(mc.player, this.target);
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    this.target = null;
                }
                break;
        }
    }

    @Override
    public String getHudInfo() {
        if(this.renderTarget != null) {
            return this.renderTarget.getName();
        }

        return "";
    }

    private enum WaitMode {
        Dynamic,
        Static
    }
}