package xyz.templecheats.templeclient.features.module.modules.movement;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;

public class Flight extends Module {
    /*
     * Settings
     */
    private final EnumSetting<FlightMode> mode = new EnumSetting<>("Mode", this, FlightMode.Creative);
    private final BooleanSetting antiKick = new BooleanSetting("Anti Kick", this, false);
    private final IntSetting speedSetting = new IntSetting("Speed", this, 1, 20, 5);

    /*
     * Variables
     */
    private int tickCounter = 0;

    public Flight() {
        super("Flight", "Fly like a bird", Keyboard.KEY_NONE, Category.Movement);
        registerSettings(antiKick, speedSetting, mode);
    }

    @Override
    public void onEnable() {
        if (mode.value() == FlightMode.Creative) {
            mc.player.capabilities.allowFlying = true;
            mc.player.capabilities.isFlying = true;
        }
    }

    @Override
    public void onDisable() {
        mc.player.capabilities.isFlying = false;
        mc.player.capabilities.allowFlying = false;
        mc.player.capabilities.setFlySpeed(0.05f);
        tickCounter = 0;
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player != mc.player) {
            return;
        }

        if (mode.value() == FlightMode.Normal) {
            mc.player.capabilities.isFlying = false;
            float speed = speedSetting.intValue() * 0.05f;
            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                mc.player.motionY = speed;
            } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                mc.player.motionY = -speed;
            } else if (!antiKick.booleanValue() || (antiKick.booleanValue() && tickCounter % 60 != 0)) {
                mc.player.motionY = 0;
            }

            double forward = mc.player.movementInput.moveForward;
            double strafe = mc.player.movementInput.moveStrafe;
            float yaw = mc.player.rotationYaw;

            if (forward != 0 || strafe != 0) {
                if (forward != 0) {
                    if (strafe > 0) {
                        yaw += (forward > 0 ? -45 : 45);
                    } else if (strafe < 0) {
                        yaw += (forward > 0 ? 45 : -45);
                    }
                    strafe = 0;
                    forward = forward > 0 ? 1 : -1;
                }

                double sin = Math.sin(Math.toRadians(yaw + 90));
                double cos = Math.cos(Math.toRadians(yaw + 90));
                mc.player.motionX = forward * speed * cos + strafe * speed * sin;
                mc.player.motionZ = forward * speed * sin - strafe * speed * cos;
            } else {
                mc.player.motionX = 0;
                mc.player.motionZ = 0;
            }
        }

        if (antiKick.booleanValue()) {
            if (tickCounter >= 60) {
                mc.player.motionY -= 0.5;
                tickCounter = 0;
            }
            tickCounter++;
        }
    }

    private enum FlightMode {
        Normal,
        Creative
    }
}