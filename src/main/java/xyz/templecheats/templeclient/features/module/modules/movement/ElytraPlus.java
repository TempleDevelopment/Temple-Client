package xyz.templecheats.templeclient.features.module.modules.movement;

import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.event.events.player.MoveEvent;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;
import xyz.templecheats.templeclient.util.math.MathUtil;

public final class ElytraPlus extends Module {
    /*
     * Settings
     */
    public final EnumSetting < Mode > mode = new EnumSetting < > ("Mode", this, Mode.Control);
    public final DoubleSetting speed = new DoubleSetting("Speed", this, 1.0f, 5.0f, 0.1f);
    public final DoubleSetting speedX = new DoubleSetting("SpeedX", this, 1.0f, 5.0f, 0.1f);
    public final DoubleSetting speedYUp = new DoubleSetting("SpeedYUp", this, 1.0f, 5.0f, 0.1f);
    public final DoubleSetting speedYDown = new DoubleSetting("SpeedYDown", this, 1.0f, 5.0f, 0.1f);
    public final DoubleSetting speedZ = new DoubleSetting("SpeedZ", this, 1.0f, 5.0f, 0.1f);
    public final BooleanSetting noKick = new BooleanSetting("No Kick", this, true);

    public ElytraPlus() {
        super("ElytraPlus", "Allows for better elytra control", Keyboard.KEY_NONE, Category.Movement);
        this.registerSettings(noKick, speed, speedX, speedYUp, speedYDown, speedZ, mode);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (Minecraft.getMinecraft().player != null) {
            Minecraft.getMinecraft().player.capabilities.isFlying = false;
        }
    }
    @Listener
    public void move(MoveEvent event) {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc.player.isElytraFlying()) {
            final double[] directionSpeed = MathUtil.directionSpeed(this.speed.doubleValue());

            if (this.mode.value() == Mode.Control) {
                mc.player.motionY = 0;
                mc.player.motionX = 0;
                mc.player.motionZ = 0;

                if (mc.player.movementInput.jump) {
                    mc.player.motionY = (this.speed.doubleValue() / 2) * this.speedYUp.doubleValue();
                } else if (mc.player.movementInput.sneak) {
                    mc.player.motionY = -(this.speed.doubleValue() / 2) * this.speedYDown.doubleValue();
                }
                if (mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0) {
                    mc.player.motionX = directionSpeed[0] * this.speedX.doubleValue();
                    mc.player.motionZ = directionSpeed[1] * this.speedZ.doubleValue();
                }

                event.setX(mc.player.motionX);
                event.setY(mc.player.motionY);
                event.setZ(mc.player.motionZ);
            } else if (this.mode.value() == Mode.Look) {
                mc.player.motionX = 0;
                mc.player.motionY = 0;
                mc.player.motionZ = 0;
                if (mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown()) {
                    mc.player.motionY = (this.speed.doubleValue() * (-1 * (Math.sin(MathUtil.degToRad(mc.player.rotationPitch))))) * mc.player.movementInput.moveForward;
                }
                if (mc.player.movementInput.jump) {
                    mc.player.motionY = (this.speed.doubleValue() / 2) * this.speedYUp.doubleValue();
                } else if (mc.player.movementInput.sneak) {
                    mc.player.motionY = -(this.speed.doubleValue() / 2) * this.speedYDown.doubleValue();
                }
                if (mc.player.movementInput.moveStrafe != 0 && mc.player.movementInput.moveForward == 0) {
                    mc.player.motionX = directionSpeed[0] * this.speedX.doubleValue();
                    mc.player.motionZ = directionSpeed[1] * this.speedZ.doubleValue();
                } else if (mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0) {
                    mc.player.motionX = directionSpeed[0] * (Math.cos(Math.abs(MathUtil.degToRad(mc.player.rotationPitch)))) * this.speedX.doubleValue();
                    mc.player.motionZ = directionSpeed[1] * (Math.cos(Math.abs(MathUtil.degToRad(mc.player.rotationPitch)))) * this.speedZ.doubleValue();
                }
                event.setX(mc.player.motionX);
                event.setY(mc.player.motionY);
                event.setZ(mc.player.motionZ);
            }
            if (mc.player.isElytraFlying()) {
                if (this.mode.value() == Mode.Packet) {
                    this.freezePlayer(mc.player);
                    this.runNoKick(mc.player);

                    final double[] directionSpeedPacket = MathUtil.directionSpeed(this.speed.doubleValue());

                    if (mc.player.movementInput.jump) {
                        mc.player.motionY = this.speed.doubleValue() * this.speedYUp.doubleValue();
                    }

                    if (mc.player.movementInput.sneak) {
                        mc.player.motionY = -this.speed.doubleValue() * this.speedYDown.doubleValue();
                    }

                    if (mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0) {
                        mc.player.motionX = directionSpeedPacket[0] * this.speedX.doubleValue();
                        mc.player.motionZ = directionSpeedPacket[1] * this.speedZ.doubleValue();
                    }

                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
                }
            }
        } else if (this.mode.value() == Mode.Vanilla) {
            final float speedScaled = (float) (this.speed.doubleValue() * 0.05f);
            final double[] directionSpeedVanilla = MathUtil.directionSpeed(speedScaled);
            if (mc.player.movementInput.jump) {
                mc.player.motionY = this.speed.doubleValue() * this.speedYUp.doubleValue();
            }

            if (mc.player.movementInput.sneak) {
                mc.player.motionY = -this.speed.doubleValue() * this.speedYDown.doubleValue();
            }
            if (mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0) {
                mc.player.motionX += directionSpeedVanilla[0] * this.speedX.doubleValue();
                mc.player.motionZ += directionSpeedVanilla[1] * this.speedZ.doubleValue();
            }
        }
    }

    private void freezePlayer(EntityPlayer player) {
        player.motionX = 0;
        player.motionY = 0;
        player.motionZ = 0;
    }

    private void runNoKick(EntityPlayer player) {
        if (this.noKick.booleanValue() && !player.isElytraFlying()) {
            if (player.ticksExisted % 4 == 0) {
                player.motionY = -0.04f;
            }
        }
    }

    private enum Mode {
        Look,
        Control,
        Packet,
        Vanilla
    }
}