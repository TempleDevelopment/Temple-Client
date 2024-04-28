package xyz.templecheats.templeclient.features.module.modules.world;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.chunk.Chunk;
import org.lwjgl.input.Keyboard;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.event.events.network.PacketEvent;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.client.hud.notification.NotificationType;
import xyz.templecheats.templeclient.features.module.modules.client.hud.notification.Notifications;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;

import java.util.*;

public class BaseFinder extends Module {
    /*
     * Settings
     */
    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", this, Mode.Chat);
    private final IntSetting notifyDistance = new IntSetting("Distance", this, 1, 100, 20);
    private final BooleanSetting notifyBeds = new BooleanSetting("Beds", this, true);
    private final BooleanSetting notifyShulkers = new BooleanSetting("Shulkers", this, true);
    private final BooleanSetting notifyChests = new BooleanSetting("Chests", this, true);
    private final BooleanSetting notifyEnderChests = new BooleanSetting("EnderChests", this, true);
    private final BooleanSetting notifySkulls = new BooleanSetting("Skulls", this, true);
    private final BooleanSetting notifySpawners = new BooleanSetting("Spawners", this, true);
    private final BooleanSetting notifyHoppers = new BooleanSetting("Hoppers", this, true);
    private final BooleanSetting notifyDispensers = new BooleanSetting("Dispensers", this, true);

    /*
     * Variables
     */
    private final Set<ChunkPos> notifiedChunks = new HashSet<>();
    private long lastNotificationTime = 0L;
    private final long notificationCooldown = 5000L;

    public BaseFinder() {
        super("BaseFinder", "AFK module to find bases/stashes", Keyboard.KEY_NONE, Category.World);
        registerSettings(notifyBeds, notifyShulkers, notifyChests, notifyEnderChests, notifySkulls, notifySpawners, notifyHoppers, notifyDispensers, notifyDistance, mode);
    }

    @Override
    public void onEnable() {
        notifiedChunks.clear();
    }

    @Override
    public void onDisable() {
        notifiedChunks.clear();
    }

    @Listener
    public void onChunkLoad(PacketEvent.Receive event) {
        Chunk chunk = mc.world.getChunk(mc.player.getPosition());
        ChunkPos chunkPos = chunk.getPos();
        double centerX = chunkPos.getXStart() + 8;
        double centerZ = chunkPos.getZStart() + 8;
        double distanceX = Math.abs(mc.player.posX - centerX);
        double distanceZ = Math.abs(mc.player.posZ - centerZ);
        if ((distanceX > notifyDistance.intValue() || distanceZ > notifyDistance.intValue()) || notifiedChunks.contains(chunkPos)) {
            return;
        }
        Map<String, Integer> foundItems = new HashMap<>();
        for (TileEntity tileEntity : chunk.getTileEntityMap().values()) {
            BlockPos pos = tileEntity.getPos();
            if (tileEntity instanceof TileEntityChest && notifyChests.booleanValue()) {
                foundItems.put("Chest", foundItems.getOrDefault("Chest", 0) + 1);
            }
            if (tileEntity instanceof TileEntityShulkerBox && notifyShulkers.booleanValue()) {
                foundItems.put("Shulker", foundItems.getOrDefault("Shulker", 0) + 1);
            }
            if (tileEntity instanceof TileEntityEnderChest && notifyEnderChests.booleanValue()) {
                foundItems.put("Ender Chest", foundItems.getOrDefault("Ender Chest", 0) + 1);
            }
            if (tileEntity instanceof TileEntityDispenser && notifyDispensers.booleanValue()) {
                foundItems.put("Dispenser", foundItems.getOrDefault("Dispenser", 0) + 1);
            }
            if (tileEntity instanceof TileEntityHopper && notifyHoppers.booleanValue()) {
                foundItems.put("Hopper", foundItems.getOrDefault("Hopper", 0) + 1);
            }
            if (tileEntity instanceof TileEntitySkull && notifySkulls.booleanValue()) {
                foundItems.put("Skull", foundItems.getOrDefault("Skull", 0) + 1);
            }
            if (tileEntity instanceof TileEntityMobSpawner && notifySpawners.booleanValue()) {
                foundItems.put("Spawner", foundItems.getOrDefault("Spawner", 0) + 1);
            }
            if (tileEntity instanceof TileEntityBed && notifyBeds.booleanValue()) {
                TileEntityBed bed = (TileEntityBed) tileEntity;
                if (bed.isHeadPiece()) {
                    foundItems.put("Bed", foundItems.getOrDefault("Bed", 0) + 1);
                }
            }
        }
        if (!foundItems.isEmpty() && System.currentTimeMillis() - lastNotificationTime > notificationCooldown) {
            for (Map.Entry<String, Integer> entry : foundItems.entrySet()) {
                sendNotification(entry.getValue() + " " + entry.getKey() + "(s)", chunkPos);
            }
            notifiedChunks.add(chunkPos);
            lastNotificationTime = System.currentTimeMillis();
        }
    }

    private void sendNotification(String name, ChunkPos chunkPos) {
        String message = name + " found in your set distance!";
        if (mode.value() == Mode.Chat || mode.value() == Mode.Both) {
            mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString(TextFormatting.AQUA + "[Temple] " + TextFormatting.WHITE + message));
        }
        if (mode.value() == Mode.Bar || mode.value() == Mode.Both) {
            Notifications.addMessage("BaseFinder", message, NotificationType.INFO);
        }
    }

    enum Mode {
        Chat,
        Bar,
        Both
    }
}