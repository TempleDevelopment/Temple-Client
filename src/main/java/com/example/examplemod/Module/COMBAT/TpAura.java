package com.example.examplemod.Module.COMBAT;

import com.example.examplemod.Module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.util.Comparator;
import java.util.Random;

public class TpAura extends Module {
    private final Random random = new Random();

    public TpAura() {
        super("TpAura[X]", Keyboard.KEY_X, Category.COMBAT);
    }

    @SubscribeEvent
    public void onUpdate(RenderWorldLastEvent e) {
        EntityPlayer player = Minecraft.getMinecraft().player;

        EntityPlayer closestPlayer = findClosestPlayer(3.0);

        if (closestPlayer != null) {
            double distance = player.getDistance(closestPlayer);

            if (distance <= 3.0) {
                teleportAndAttackClosestPlayer(closestPlayer, 2.0);
            }
        }
    }

    private EntityPlayer findClosestPlayer(double range) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        return Minecraft.getMinecraft().world.playerEntities.stream()
                .filter(entityPlayer -> entityPlayer != player)
                .filter(entityPlayer -> entityPlayer.getDistance(player) <= range)
                .min(Comparator.comparing(entityPlayer -> entityPlayer.getDistance(player)))
                .orElse(null);
    }

    private void teleportAndAttackClosestPlayer(EntityPlayer closestPlayer, double radius) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        double angle = random.nextDouble() * 2 * Math.PI;

        double offsetX = Math.cos(angle) * radius;
        double offsetZ = Math.sin(angle) * radius;

        double x = closestPlayer.posX + offsetX;
        double y = closestPlayer.posY;
        double z = closestPlayer.posZ + offsetZ;

        player.setPositionAndUpdate(x, y, z);

        // Attack the closest player
        player.rotationYaw = rotations(closestPlayer)[0];
        player.rotationPitch = rotations(closestPlayer)[1];

        if (player.getCooledAttackStrength(0) == 1) {
            Minecraft.getMinecraft().playerController.attackEntity(player, closestPlayer);
            player.swingArm(EnumHand.MAIN_HAND);
            player.resetCooldown();
        }
    }

    public float[] rotations(EntityPlayer entity) {
        double x = entity.posX - Minecraft.getMinecraft().player.posX;
        double y = entity.posY - (Minecraft.getMinecraft().player.posY + Minecraft.getMinecraft().player.getEyeHeight());
        double z = entity.posZ - Minecraft.getMinecraft().player.posZ;

        double u = MathHelper.sqrt(x * x + z * z);

        float u2 = (float) (MathHelper.atan2(z, x) * (180D / Math.PI) - 90.0F);
        float u3 = (float) (-MathHelper.atan2(y, u) * (180D / Math.PI));

        return new float[]{u2, u3};
    }
}
