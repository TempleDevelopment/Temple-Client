package com.example.examplemod.Module.WORLD;

import com.example.examplemod.Module.Module;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class Scaffold extends Module {
    public Scaffold() {
        super("Scaffold", Keyboard.KEY_NONE, Category.WORLD);
    }

    private BlockPos lastBlockPos = null;

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock) {
            // Check if you have a block in your hand

            // Calculate the position in front of the player
            double offsetX = Math.sin(Math.toRadians(mc.player.rotationYaw));
            double offsetZ = -Math.cos(Math.toRadians(mc.player.rotationYaw));

            BlockPos playerPos = new BlockPos(mc.player.posX + offsetX, mc.player.posY - 1, mc.player.posZ + offsetZ);
            if (mc.world.isAirBlock(playerPos) && !playerPos.equals(lastBlockPos)) {
                // Check if the block in front of you is air and it's not the same as the last block placed
                mc.playerController.processRightClickBlock(mc.player, mc.world, playerPos, EnumFacing.UP, new Vec3d(0.5, 0.5, 0.5), EnumHand.MAIN_HAND);
                lastBlockPos = playerPos;
            }
        } else {
            lastBlockPos = null; // Reset the lastBlockPos if you don't have a block in your hand
        }
    }
}
