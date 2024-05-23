package xyz.templecheats.templeclient.features.module.modules.render;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.play.client.CPacketPlayer;
import org.lwjgl.input.Keyboard;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.event.EventStageable;
import xyz.templecheats.templeclient.event.events.network.PacketEvent;
import xyz.templecheats.templeclient.event.events.player.MotionEvent;
import xyz.templecheats.templeclient.event.events.player.MoveEvent;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.movement.*;
import xyz.templecheats.templeclient.features.module.modules.movement.speed.*;
import xyz.templecheats.templeclient.features.module.modules.movement.speed.sub.Bhop;
import xyz.templecheats.templeclient.features.module.modules.movement.speed.sub.Strafe;
import xyz.templecheats.templeclient.manager.ModuleManager;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;

public class Freecam extends Module {
    /*
     * Settings
     */
    private final BooleanSetting disableMovement = new BooleanSetting("Disable Movement", this, true);
    private final DoubleSetting horizontalSpeed = new DoubleSetting("Horizontal Speed", this, 0.1, 5d, 1d);
    private final DoubleSetting verticalSpeed = new DoubleSetting("Vertical Speed", this, 0.1, 5d, 1d);

    /*
     * Variables
     */
    private EntityOtherPlayerMP fakePlayer;
    private float startYaw, startPitch;
    private boolean wasAutoWalkEnabled;
    private boolean wasReverseStepEnabled;
    private boolean wasRotationLockEnabled;
    private boolean wasStepEnabled;
    private boolean wasFlightEnabled;
    private boolean wasSpeedEnabled;
    private boolean wasStrafeEnabled;
    private boolean wasBhopEnabled;
    private boolean wasJesusEnabled;
    private boolean wasTunnelSpeedEnabled;

    public Freecam() {
        super("Freecam", "Out of body experience", Keyboard.KEY_NONE, Category.Render);
        this.registerSettings(disableMovement, horizontalSpeed, verticalSpeed);
    }

    @Listener
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer) {
            event.setCanceled(true);
        }
    }

    @Listener
    public void onMove(MoveEvent event) {
        mc.player.noClip = true;
        mc.player.onGround = false;
        mc.player.fallDistance = 0;

        final double hSpeed = this.horizontalSpeed.doubleValue() * 2;

        if (mc.gameSettings.keyBindForward.isKeyDown()) {
            mc.player.motionX = hSpeed * Math.cos(Math.toRadians(mc.player.rotationYaw + 90));
            mc.player.motionZ = hSpeed * Math.sin(Math.toRadians(mc.player.rotationYaw + 90));
        } else if (mc.gameSettings.keyBindBack.isKeyDown()) {
            mc.player.motionX = -hSpeed * Math.cos(Math.toRadians(mc.player.rotationYaw + 90));
            mc.player.motionZ = -hSpeed * Math.sin(Math.toRadians(mc.player.rotationYaw + 90));
        } else {
            mc.player.motionX = 0;
            mc.player.motionZ = 0;
        }

        if (mc.gameSettings.keyBindLeft.isKeyDown()) {
            mc.player.motionX += hSpeed * Math.cos(Math.toRadians(mc.player.rotationYaw));
            mc.player.motionZ += hSpeed * Math.sin(Math.toRadians(mc.player.rotationYaw));
        } else if (mc.gameSettings.keyBindRight.isKeyDown()) {
            mc.player.motionX -= hSpeed * Math.cos(Math.toRadians(mc.player.rotationYaw));
            mc.player.motionZ -= hSpeed * Math.sin(Math.toRadians(mc.player.rotationYaw));
        }
    }

    @Listener
    public void onMotion(MotionEvent event) {
        if (event.getStage() != EventStageable.EventStage.POST) {
            return;
        }

        final double vSpeed = this.verticalSpeed.doubleValue() * 2;

        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.player.motionY = vSpeed;
        } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            mc.player.motionY = -vSpeed;
        } else {
            mc.player.motionY = 0;
        }

        if (this.fakePlayer != null) {
            this.fakePlayer.rotationYaw = this.fakePlayer.rotationYawHead = mc.player.rotationYaw;
            this.fakePlayer.rotationPitch = mc.player.rotationPitch;
            this.fakePlayer.inventory.copyInventory(mc.player.inventory);
            this.fakePlayer.inventory.currentItem = mc.player.inventory.currentItem;
        }
    }

    @Override
    public void onEnable() {
        mc.player.noClip = true;

        this.fakePlayer = new EntityOtherPlayerMP(mc.world, mc.getSession().getProfile());
        this.fakePlayer.copyLocationAndAnglesFrom(mc.player);
        this.startYaw = mc.player.rotationYaw;
        this.startPitch = mc.player.rotationPitch;
        mc.world.addEntityToWorld(696984837, this.fakePlayer);

        if (disableMovement.booleanValue()) {
            disableMovementModules();
        }
    }

    @Override
    public void onDisable() {
        mc.player.noClip = false;

        mc.player.copyLocationAndAnglesFrom(this.fakePlayer);
        mc.player.rotationYaw = this.startYaw;
        mc.player.rotationPitch = this.startPitch;
        mc.world.removeEntity(this.fakePlayer);
        this.fakePlayer = null;

        if (disableMovement.booleanValue()) {
            enableMovementModules();
        }
    }

    private void disableMovementModules() {
        AutoWalk autoWalk = (AutoWalk) ModuleManager.getModuleByName("AutoWalk");
        if (autoWalk != null && autoWalk.isEnabled()) {
            autoWalk.disable();
            this.wasAutoWalkEnabled = true;
        }

        RotationLock rotationLock = (RotationLock) ModuleManager.getModuleByName("RotationLock");
        if (rotationLock != null && rotationLock.isEnabled()) {
            rotationLock.disable();
            this.wasRotationLockEnabled = true;
        }

        Step step = (Step) ModuleManager.getModuleByName("Step");
        if (step != null && step.isEnabled()) {
            step.disable();
            this.wasStepEnabled = true;
        }

        Flight flight = (Flight) ModuleManager.getModuleByName("Flight");
        if (flight != null && flight.isEnabled()) {
            flight.disable();
            this.wasFlightEnabled = true;
        }

        Speed speed = (Speed) ModuleManager.getModuleByName("Speed");
        if (speed != null && speed.isEnabled()) {
            speed.disable();
            this.wasSpeedEnabled = true;
        }

        Strafe strafe = (Strafe) ModuleManager.getModuleByName("Strafe");
        if (strafe != null && strafe.isEnabled()) {
            strafe.disable();
            this.wasStrafeEnabled = true;
        }

        Bhop bhop = (Bhop) ModuleManager.getModuleByName("Bhop");
        if (bhop != null && bhop.isEnabled()) {
            bhop.disable();
            this.wasBhopEnabled = true;
        }

        Jesus jesus = (Jesus) ModuleManager.getModuleByName("Jesus");
        if (jesus != null && jesus.isEnabled()) {
            jesus.disable();
            this.wasJesusEnabled = true;
        }

        TunnelSpeed tunnelSpeed = (TunnelSpeed) ModuleManager.getModuleByName("TunnelSpeed");
        if (tunnelSpeed != null && tunnelSpeed.isEnabled()) {
            tunnelSpeed.disable();
            this.wasTunnelSpeedEnabled = true;
        }

        ReverseStep reverseStep = (ReverseStep) ModuleManager.getModuleByName("ReverseStep");
        if (reverseStep != null && reverseStep.isEnabled()) {
            reverseStep.disable();
            this.wasReverseStepEnabled = true;
        }
    }

    private void enableMovementModules() {
        if (this.wasAutoWalkEnabled) {
            AutoWalk autoWalk = (AutoWalk) ModuleManager.getModuleByName("AutoWalk");
            if (autoWalk != null) {
                autoWalk.enable();
            }
            this.wasAutoWalkEnabled = false;
        }

        if (this.wasRotationLockEnabled) {
            RotationLock rotationLock = (RotationLock) ModuleManager.getModuleByName("RotationLock");
            if (rotationLock != null) {
                rotationLock.enable();
            }
            this.wasRotationLockEnabled = false;
        }

        if (this.wasReverseStepEnabled) {
            Step step = (Step) ModuleManager.getModuleByName("Step");
            if (step != null) {
                step.enable();
            }
            this.wasReverseStepEnabled = false;
        }

        if (this.wasFlightEnabled) {
            Flight flight = (Flight) ModuleManager.getModuleByName("Flight");
            if (flight != null) {
                flight.enable();
            }
            this.wasFlightEnabled = false;
        }

        if (this.wasSpeedEnabled) {
            Speed speed = (Speed) ModuleManager.getModuleByName("Speed");
            if (speed != null) {
                speed.enable();
            }
            this.wasSpeedEnabled = false;
        }

        if (this.wasStrafeEnabled) {
            Strafe strafe = (Strafe) ModuleManager.getModuleByName("Strafe");
            if (strafe != null) {
                strafe.enable();
            }
            this.wasStrafeEnabled = false;
        }

        if (this.wasBhopEnabled) {
            Bhop bhop = (Bhop) ModuleManager.getModuleByName("Bhop");
            if (bhop != null) {
                bhop.enable();
            }
            this.wasBhopEnabled = false;
        }

        if (this.wasJesusEnabled) {
            Jesus jesus = (Jesus) ModuleManager.getModuleByName("Jesus");
            if (jesus != null) {
                jesus.enable();
            }
            this.wasJesusEnabled = false;
        }

        if (this.wasTunnelSpeedEnabled) {
            TunnelSpeed tunnelSpeed = (TunnelSpeed) ModuleManager.getModuleByName("TunnelSpeed");
            if (tunnelSpeed != null) {
                tunnelSpeed.enable();
            }
            this.wasTunnelSpeedEnabled = false;
        }

        if (this.wasStepEnabled) {
            ReverseStep reverseStep = (ReverseStep) ModuleManager.getModuleByName("ReverseStep");
            if (reverseStep != null) {
                reverseStep.enable();
            }
            this.wasStepEnabled = false;
        }
    }
}
