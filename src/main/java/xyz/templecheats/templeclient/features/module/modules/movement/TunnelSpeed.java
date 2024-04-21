package xyz.templecheats.templeclient.features.module.modules.movement;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import org.lwjgl.input.Keyboard;

import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.movement.speed.sub.Strafe;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;

public class TunnelSpeed extends Module {
    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", this, Mode.Strict);

    public TunnelSpeed() {
        super("TunnelSpeed", "Makes you go fast in tunnels", Keyboard.KEY_NONE, Category.Movement);
        registerSettings(mode);
    }

    @Override
    public void onUpdate() {
        BlockPos pos = new BlockPos(mc.player.posX, mc.player.posY + 2.0, mc.player.posZ);
        BlockPos pos2 = new BlockPos(mc.player.posX, mc.player.posY - 1.0, mc.player.posZ);
        if (mc.world.getBlockState(pos).getBlock() != Blocks.AIR
                && mc.world.getBlockState(pos).getBlock() != Blocks.PORTAL
                && mc.world.getBlockState(pos).getBlock() != Blocks.END_PORTAL
                && mc.world.getBlockState(pos).getBlock() != Blocks.WATER
                && mc.world.getBlockState(pos).getBlock() != Blocks.FLOWING_WATER
                && mc.world.getBlockState(pos).getBlock() != Blocks.LAVA
                && mc.world.getBlockState(pos).getBlock() != Blocks.FLOWING_LAVA
                && mc.world.getBlockState(pos2).getBlock() != Blocks.ICE
                && mc.world.getBlockState(pos2).getBlock() != Blocks.FROSTED_ICE
                && mc.world.getBlockState(pos2).getBlock() != Blocks.PACKED_ICE
                && !mc.player.isInWater()) {
            float yaw = (float) Math.toRadians(mc.player.rotationYaw);
            if (mode.value() == Mode.Normal) {
                if (mc.gameSettings.keyBindForward.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown() && mc.player.onGround) {
                    mc.player.motionX -= Math.sin(yaw) * 0.15;
                    mc.player.motionZ += Math.cos(yaw) * 0.15;
                }
            } else {
                if (Strafe.INSTANCE.isEnabled() || Strafe.INSTANCE.isToggled()) return;
                Strafe.INSTANCE.enable();
                if (Strafe.INSTANCE.jump.booleanValue()) return;
                Strafe.INSTANCE.jump.setBooleanValue(true);
            }
        } else {
            if (mode.value() == Mode.Strict) {
                Strafe.INSTANCE.setToggled(false);
            }
        }
    }
    private enum Mode {
        Strict,
        Normal
    }
}
