package xyz.templecheats.templeclient.api.util.autocrystal.threads;

import xyz.templecheats.templeclient.api.util.autocrystal.ACSettings;
import xyz.templecheats.templeclient.api.util.autocrystal.ACUtil;
import xyz.templecheats.templeclient.api.util.autocrystal.CrystalInfo;
import xyz.templecheats.templeclient.api.util.autocrystal.PlayerInfo;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.concurrent.Callable;

public class ACSubThread implements Callable<CrystalInfo.PlaceInfo> {
    private final ACSettings settings;

    private final List<BlockPos> possibleLocations;
    private final PlayerInfo target;

    public ACSubThread(ACSettings setting, List<BlockPos> possibleLocations, PlayerInfo target) {
        this.settings = setting;

        this.possibleLocations = possibleLocations;
        this.target = target;
    }

    @Override
    public CrystalInfo.PlaceInfo call() {
        return getPlacement();
    }

    private CrystalInfo.PlaceInfo getPlacement() {
        if (possibleLocations == null) {
            return null;
        }

        return ACUtil.calculateBestPlacement(settings, target, possibleLocations);
    }
}
