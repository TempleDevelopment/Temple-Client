package xyz.templecheats.templeclient.util.autocrystal.threads;

import net.minecraft.util.math.BlockPos;
import xyz.templecheats.templeclient.util.autocrystal.ACSettings;
import xyz.templecheats.templeclient.util.autocrystal.ACUtil;
import xyz.templecheats.templeclient.util.autocrystal.CrystalInfo;
import xyz.templecheats.templeclient.util.player.PlayerInfo;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Sub-thread for calculating the best crystal placement for AutoCrystal.
 */
public class ACSubThread implements Callable<CrystalInfo.PlaceInfo> {

    private final ACSettings settings;
    private final List<BlockPos> possibleLocations;
    private final PlayerInfo target;

    /**
     * Constructor for ACSubThread.
     *
     * @param settings         The settings for AutoCrystal.
     * @param possibleLocations The list of possible locations for crystal placement.
     * @param target           The target player information.
     */
    public ACSubThread(ACSettings settings, List<BlockPos> possibleLocations, PlayerInfo target) {
        this.settings = settings;
        this.possibleLocations = possibleLocations;
        this.target = target;
    }

    /**
     * The call method executed by the thread to get the best crystal placement.
     *
     * @return The best crystal placement information or null if no valid placement is found.
     */
    @Override
    public CrystalInfo.PlaceInfo call() {
        return getPlacement();
    }

    /**
     * Calculates the best crystal placement.
     *
     * @return The best crystal placement information or null if no valid placement is found.
     */
    private CrystalInfo.PlaceInfo getPlacement() {
        if (possibleLocations == null) {
            return null;
        }
        return ACUtil.calculateBestPlacement(settings, target, possibleLocations);
    }
}
