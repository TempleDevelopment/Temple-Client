package xyz.templecheats.templeclient.util.autocrystal;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.util.autocrystal.threads.ACCalculate;
import xyz.templecheats.templeclient.util.player.DamageUtil;
import xyz.templecheats.templeclient.util.player.PlayerInfo;
import xyz.templecheats.templeclient.util.world.EntityUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public enum ACHelper {
    INSTANCE;

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final List<CrystalInfo.PlaceInfo> EMPTY_LIST = new ArrayList<>();

    public static final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    private static final ExecutorService mainExecutors = Executors.newSingleThreadExecutor();
    private Future<List<CrystalInfo.PlaceInfo>> mainThreadOutput;

    private ACSettings settings = null;
    private List<BlockPos> possiblePlacements = new ArrayList<>();
    private List<EntityEnderCrystal> targetableCrystals = new ArrayList<>();
    private final List<PlayerInfo> targetsInfo = new ArrayList<>();
    private List<BlockPos> threadPlacements = new ArrayList<>();

    /**
     * Starts the calculation process for AutoCrystal with a given timeout.
     *
     * @param timeout the timeout for the calculation thread.
     */
    public void startCalculations(long timeout) {
        if (mainThreadOutput != null) {
            mainThreadOutput.cancel(true);
        }
        mainThreadOutput = mainExecutors.submit(new ACCalculate(settings, targetsInfo, threadPlacements, timeout));
    }

    /**
     * Retrieves the output from the main calculation thread.
     *
     * @param wait whether to wait for the thread to finish.
     * @return a list of PlaceInfo results.
     */
    public List<CrystalInfo.PlaceInfo> getOutput(boolean wait) {
        if (mainThreadOutput == null) {
            return EMPTY_LIST;
        }

        if (wait) {
            while (!(mainThreadOutput.isDone() || mainThreadOutput.isCancelled())) {
                // Waiting for the thread to finish
            }
        } else {
            if (!mainThreadOutput.isDone()) {
                return null;
            }
            if (mainThreadOutput.isCancelled()) {
                return EMPTY_LIST;
            }
        }

        List<CrystalInfo.PlaceInfo> output = EMPTY_LIST;
        try {
            output = mainThreadOutput.get();
        } catch (InterruptedException | ExecutionException ignored) {
        }

        mainThreadOutput = null;
        return output;
    }

    /**
     * Recalculates the values for possible placements and targetable crystals.
     *
     * @param settings        the settings for AutoCrystal.
     * @param self            the player's own information.
     * @param armourPercent   the threshold for low armour.
     * @param enemyDistance   the maximum distance to consider enemies.
     */
    public void recalculateValues(ACSettings settings, PlayerInfo self, float armourPercent, double enemyDistance) {
        this.settings = settings;
        final double entityRangeSq = enemyDistance * enemyDistance;

        List<EntityPlayer> targets = mc.world.playerEntities.stream()
                .filter(entity -> self.entity.getDistanceSq(entity) <= entityRangeSq)
                .filter(entity -> !EntityUtil.basicChecksEntity(entity))
                .filter(entity -> entity.getHealth() > 0.0f)
                .collect(Collectors.toList());

        targetableCrystals = mc.world.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityEnderCrystal)
                .map(entity -> (EntityEnderCrystal) entity)
                .filter(crystal -> self.entity.getDistanceSq(crystal) < settings.breakRangeSq)
                .filter(crystal -> {
                    float damage = DamageUtil.calculateDamageThreaded(crystal.posX, crystal.posY, crystal.posZ, self);
                    return damage < settings.maxSelfDamage && damage < self.health;
                }).collect(Collectors.toList());

        possiblePlacements = CrystalUtil.findCrystalBlocks(settings.placeRange, settings.server);
        filterInvalidPlacements(settings, self);

        threadPlacements = CrystalUtil.findCrystalBlocksExcludingCrystals(settings.placeRange, settings.server);

        targetsInfo.clear();
        for (EntityPlayer target : targets) {
            targetsInfo.add(new PlayerInfo(target, armourPercent));
        }
    }

    /**
     * Filters out invalid placements based on damage and ray tracing settings.
     *
     * @param settings the settings for AutoCrystal.
     * @param self     the player's own information.
     */
    private void filterInvalidPlacements(ACSettings settings, PlayerInfo self) {
        possiblePlacements.removeIf(pos -> {
            float damage = DamageUtil.calculateDamageThreaded((double) pos.getX() + 0.5d, (double) pos.getY() + 1.0d, (double) pos.getZ() + 0.5d, settings.player);

            if (damage > settings.maxSelfDamage || damage > settings.player.health) {
                return true;
            }

            EnumFacing validFace = mc.player.posY + mc.player.getEyeHeight() > pos.getY() + 0.5 || !mc.world.isAirBlock(pos.down()) ? EnumFacing.UP : EnumFacing.DOWN;

            if (settings.raytrace) {
                validFace = null;
                final Vec3d vec = new Vec3d(pos.getX() + 0.5, pos.getY() - 0.5, pos.getZ() + 0.5);
                final RayTraceResult result = mc.world.rayTraceBlocks(mc.player.getPositionEyes(1.0F), vec);

                if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK && mc.world.isAirBlock(pos.offset(result.sideHit))) {
                    validFace = result.sideHit;
                }
            }

            return validFace == null;
        });
    }

    /**
     * Registers this class to the TempleClient event bus.
     */
    public void onEnable() {
        TempleClient.eventBus.addEventListener(this);
    }

    /**
     * Unregisters this class from the TempleClient event bus and cancels ongoing tasks.
     */
    public void onDisable() {
        TempleClient.eventBus.removeEventListener(this);

        if (mainThreadOutput != null) {
            mainThreadOutput.cancel(true);
        }
    }

    // Getters for settings and targets
    public ACSettings getSettings() {
        return settings;
    }

    public List<BlockPos> getPossiblePlacements() {
        return possiblePlacements;
    }

    public List<EntityEnderCrystal> getTargetableCrystals() {
        return targetableCrystals;
    }
}
