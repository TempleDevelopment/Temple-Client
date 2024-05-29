package xyz.templecheats.templeclient.util.autocrystal.threads;

import net.minecraft.util.math.BlockPos;
import xyz.templecheats.templeclient.features.module.modules.combat.AutoCrystal;
import xyz.templecheats.templeclient.util.autocrystal.ACHelper;
import xyz.templecheats.templeclient.util.autocrystal.ACSettings;
import xyz.templecheats.templeclient.util.autocrystal.CrystalInfo;
import xyz.templecheats.templeclient.util.player.PlayerInfo;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ACCalculate implements Callable<List<CrystalInfo.PlaceInfo>> {

    private final ACSettings settings;
    private final List<PlayerInfo> targets;
    private final List<BlockPos> blocks;
    private final long globalTimeoutTime;

    /**
     * Constructor for ACCalculate.
     *
     * @param settings        The settings for AutoCrystal.
     * @param targets         The list of target player information.
     * @param blocks          The list of possible block positions for placing crystals.
     * @param globalTimeoutTime The global timeout time for the calculation.
     */
    public ACCalculate(ACSettings settings, List<PlayerInfo> targets, List<BlockPos> blocks, long globalTimeoutTime) {
        this.settings = settings;
        this.targets = targets;
        this.blocks = blocks;
        this.globalTimeoutTime = globalTimeoutTime;
    }

    /**
     * The call method executed by the thread to calculate the best crystal placements.
     *
     * @return A list of PlaceInfo results.
     */
    @Override
    public List<CrystalInfo.PlaceInfo> call() {
        return getPlayers(startThreads());
    }

    /**
     * Starts sub-threads for each target to calculate the best placements.
     *
     * @return A list of Future objects representing the results of the sub-threads.
     */
    @Nonnull
    private List<Future<CrystalInfo.PlaceInfo>> startThreads() {
        List<Future<CrystalInfo.PlaceInfo>> output = new ArrayList<>();
        for (PlayerInfo target : targets) {
            output.add(ACHelper.executor.submit(new ACSubThread(settings, blocks, target)));
        }
        return output;
    }

    /**
     * Retrieves and processes the results from the sub-threads.
     *
     * @param input A list of Future objects representing the results of the sub-threads.
     * @return A sorted list of PlaceInfo objects.
     */
    private List<CrystalInfo.PlaceInfo> getPlayers(List<Future<CrystalInfo.PlaceInfo>> input) {
        List<CrystalInfo.PlaceInfo> place = new ArrayList<>();
        for (Future<CrystalInfo.PlaceInfo> future : input) {
            while (!future.isDone() && !future.isCancelled()) {
                if (System.currentTimeMillis() > globalTimeoutTime) {
                    break;
                }
            }
            if (future.isDone()) {
                try {
                    CrystalInfo.PlaceInfo crystal = future.get();
                    if (crystal != null) {
                        place.add(crystal);
                    }
                } catch (InterruptedException | ExecutionException ignored) {
                }
            } else {
                future.cancel(true);
            }
        }

        // Sort results based on the priority setting
        if (settings.crystalPriority == AutoCrystal.Priority.Health) {
            place.sort(Comparator.comparingDouble(i -> -i.target.health));
        } else if (settings.crystalPriority == AutoCrystal.Priority.Closest) {
            place.sort(Comparator.comparingDouble(i -> -settings.player.entity.getDistanceSq(i.target.entity)));
        } else {
            place.sort(Comparator.comparingDouble(i -> i.damage));
        }

        return place;
    }
}
