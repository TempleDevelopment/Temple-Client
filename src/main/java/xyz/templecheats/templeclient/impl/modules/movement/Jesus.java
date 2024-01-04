package xyz.templecheats.templeclient.impl.modules.movement;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.impl.modules.Module;
import org.lwjgl.input.Keyboard;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import xyz.templecheats.templeclient.impl.gui.clickgui.setting.Setting;

import java.util.ArrayList;
import java.util.Objects;

public class Jesus extends Module {
    public Jesus() {
        super("Jesus", Keyboard.KEY_NONE, Category.MOVEMENT);

        ArrayList<String> options = new ArrayList<>();

        options.add("Matrix");
        options.add("Dolphin");
        options.add("Solid");

        TempleClient.settingsManager.rSetting(new Setting("Mode", this, options, "Mode"));
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        String Mode =  TempleClient.settingsManager.getSettingByName(this.name, "Mode").getValString();

        BlockPos blockPos = new BlockPos(mc.player.posX, mc.player.posY - 0.1, mc.player.posZ);
        Block block = mc.world.getBlockState(blockPos).getBlock();

        if (Objects.equals(Mode, "Matrix")) {
            if (Block.getIdFromBlock(block) == 9) {
                if (!mc.player.onGround) {
                    speed(2.5);

                    if (mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY + 0.0000001, mc.player.posZ)).getBlock() == Block.getBlockById(9)) {
                        mc.player.fallDistance = 0.0f;
                        mc.player.motionX = 0.0;
                        mc.player.motionY = 0.06f;
                        mc.player.jumpMovementFactor = 0.01f;
                        mc.player.motionZ = 0.0;
                    }
                }
            }
        } else if (Objects.equals(Mode, "Solid")) {
            if (Block.getIdFromBlock(block) == 9) {
                mc.player.onGround = true;
                mc.player.motionY = 0.0;
                mc.player.fallDistance = 0.0f;

                if (mc.player.movementInput.moveForward != 0.0f || mc.player.movementInput.moveStrafe != 0.0f) {
                    mc.player.motionX *= 1.0;
                    mc.player.motionZ *= 1.0;
                }
            }
        } else {
            if (mc.player.isInWater() || mc.player.isInLava()) {
                if (!mc.player.collidedHorizontally) {
                    mc.player.motionY = 0.1;
                }
            }
        }
    }

    public static void speed(double speed) {
        Minecraft mc = Minecraft.getMinecraft();

        double forward = mc.player.movementInput.moveForward;
        double strafe = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.rotationYaw;

        if (forward == 0.0 && strafe == 0.0) {
            mc.player.motionX = 0.0;
            mc.player.motionZ = 0.0;
        } else {
            if (forward != 0.0) {
                if (strafe < 0.0) {
                    yaw += (float) (forward > 0.0 ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += (float) (forward > 0.0 ? 45 : -45);
                }

                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }

                mc.player.motionX = forward * speed * Math.cos(Math.toRadians(yaw + 90.0)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0));
                mc.player.motionZ = forward * speed * Math.sin(Math.toRadians(yaw + 90.0)) + strafe * speed * Math.cos(Math.toRadians(yaw + 90.0));
            }
        }
    }
}
