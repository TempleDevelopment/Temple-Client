package xyz.templecheats.templeclient.manager;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.util.Globals;
import xyz.templecheats.templeclient.util.world.BlockUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class HoleManager implements Globals {

    /****************************************************************
     *                      Constants
     ****************************************************************/

    // Common block positions relative to a hole to check for surrounding block types
    private static final Vec3i[] COMMON = {
            new Vec3i(1, 0, 0),
            new Vec3i(0, 0, 1),
            new Vec3i(0, -1, 0)
    };

    // Block positions for a single hole
    private static final Vec3i[] HOLE = {
            new Vec3i(-1, 0, 0),
            new Vec3i(0, 0, -1)
    };

    // Block positions for a double hole facing north
    private static final Vec3i[] DOUBLE_HOLE_NORTH = {
            new Vec3i(0, 0, -2),
            new Vec3i(-1, 0, -1),
            new Vec3i(1, 0, -1),
            new Vec3i(0, -1, -1),
            new Vec3i(-1, 0, 0)
    };

    // Block positions for a double hole facing west
    private static final Vec3i[] DOUBLE_HOLE_WEST = {
            new Vec3i(-2, 0, 0),
            new Vec3i(-1, 0, 1),
            new Vec3i(-1, 0, -1),
            new Vec3i(-1, -1, 0),
            new Vec3i(0, 0, -1)
    };

    public String time;

    private ArrayList<HolePos> holes = new ArrayList<>();

    /****************************************************************
     *                      Hole Loading Methods
     ****************************************************************/

    public void loadHoles(final int range) {
        if (mc.world == null || mc.player == null) {
            return;
        }
        final long sys = System.currentTimeMillis();
        holes = findHoles(range);
        time = System.currentTimeMillis() - sys + "ms";
    }

    private ArrayList<HolePos> findHoles(final int range) {
        final ArrayList<HolePos> holes = new ArrayList<>();
        if (mc.player == null || mc.world == null) {
            return holes;
        }
        for (final BlockPos pos : BlockUtil.getBlocksInRadius(range)) {
            final HolePos holePos = getHolePos(pos);
            if (holePos != null) {
                holes.add(holePos);
            }
        }
        return holes;
    }

    /****************************************************************
     *                      Hole Checking Methods
     ****************************************************************/

    public boolean holeManagerContains(final BlockPos pos) {
        return TempleClient.holeManager.getHoles().stream().anyMatch(holePos -> holePos.getPos().equals(pos));
    }

    @Nullable
    public HolePos getHolePos(final BlockPos pos) {
        if (mc.player == null || mc.world == null) return null;

        if (mc.world.getBlockState(pos).getBlock() != Blocks.AIR
                || mc.world.getBlockState(pos.up()).getBlock() != Blocks.AIR
                || mc.world.getBlockState(pos.up().up()).getBlock() != Blocks.AIR) {
            return null;
        }

        boolean obsidian = false;

        for (Vec3i vec3i : COMMON) {
            Block block = mc.world.getBlockState(pos.add(vec3i)).getBlock();
            if (block == Blocks.OBSIDIAN) obsidian = true;
            else if (block != Blocks.BEDROCK) return null;
        }

        HolePos holePos = testHole(pos, obsidian, HOLE, Type.Obsidian, Type.Bedrock);
        if (holePos == null)
            holePos = testHole(pos, obsidian, DOUBLE_HOLE_NORTH, Type.DoubleObsidianNorth, Type.DoubleBedrockNorth);
        if (holePos == null)
            holePos = testHole(pos, obsidian, DOUBLE_HOLE_WEST, Type.DoubleObsidianWest, Type.DoubleBedrockWest);

        return holePos;
    }

    @Nullable
    private static HolePos testHole(BlockPos pos, boolean obsidian, Vec3i[] blocks, Type obsidianType, Type bedrockType) {
        for (Vec3i vec3i : blocks) {
            Block block = mc.world.getBlockState(pos.add(vec3i)).getBlock();
            if (block == Blocks.OBSIDIAN) obsidian = true;
            else if (block != Blocks.BEDROCK) return null;
        }
        return new HolePos(pos, obsidian ? obsidianType : bedrockType);
    }

    /****************************************************************
     *                      Getter Methods
     ****************************************************************/

    public List<HolePos> getHoles() {
        return holes;
    }

    /****************************************************************
     *                      Hole Types and Positions
     ****************************************************************/

    public enum Type {
        Bedrock,
        Obsidian,
        DoubleBedrockNorth,
        DoubleBedrockWest,
        DoubleObsidianNorth,
        DoubleObsidianWest
    }

    public static class HolePos {
        private final BlockPos pos;
        private final Type holeType;

        public HolePos(BlockPos pos, Type holeType) {
            this.pos = pos;
            this.holeType = holeType;
        }

        public BlockPos getPos() {
            return pos;
        }

        public Type getHoleType() {
            return holeType;
        }

        public boolean isBedrock() {
            return holeType.equals(Type.Bedrock) || holeType.equals(Type.DoubleBedrockWest) || holeType.equals(Type.DoubleBedrockNorth);
        }

        public boolean isWestDouble() {
            return holeType.equals(Type.DoubleBedrockWest) || holeType.equals(Type.DoubleObsidianWest);
        }

        public boolean isDouble() {
            return holeType.equals(Type.DoubleBedrockNorth) || holeType.equals(Type.DoubleBedrockWest) || holeType.equals(Type.DoubleObsidianNorth) || holeType.equals(Type.DoubleObsidianWest);
        }
    }
}
