/*
 * This HoleFiller was made by GameSense, and was modified.
 */
package xyz.templecheats.templeclient.features.module.modules.combat;

import net.minecraft.block.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.manager.InventoryManager;
import xyz.templecheats.templeclient.util.player.PlacementUtil;
import xyz.templecheats.templeclient.util.player.PlayerUtil;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.util.render.shader.impl.GradientShader;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;
import xyz.templecheats.templeclient.util.world.EntityUtil;
import xyz.templecheats.templeclient.util.world.HoleUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class HoleFiller extends Module {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    private final EnumSetting<Mode> mode = new EnumSetting<>("Type", this, Mode.Obby);
    private final IntSetting placeDelay = new IntSetting("Delay", this, 0, 10, 2);
    private final IntSetting retryDelay = new IntSetting("Retry Delay", this, 0, 50, 10);
    private final IntSetting bpc = new IntSetting("Block pre Cycle", this, 1, 5, 2);
    private final DoubleSetting range = new DoubleSetting("Range", this, 0.0, 10.0, 4.0);
    private final DoubleSetting playerRange = new DoubleSetting("Player Range", this, 1.0, 6.0, 3.0);
    private final BooleanSetting onlyPlayer = new BooleanSetting("Only Player", this, false);
    private final BooleanSetting rotate = new BooleanSetting("Rotate", this, true);
    private final BooleanSetting autoSwitch = new BooleanSetting("Switch", this, true);
    private final BooleanSetting offHandObby = new BooleanSetting("Off Hand Obby", this, false);
    private final BooleanSetting disableOnFinish = new BooleanSetting("Disable on Finish", this, true);
    private final BooleanSetting render = new BooleanSetting("Render", this, true);
    private final BooleanSetting fill = new BooleanSetting("Box Fill", this, true);
    private final BooleanSetting outline = new BooleanSetting("Box Outline", this, true);
    private final DoubleSetting opacity = new DoubleSetting("Opacity", this, 0.0, 1.0, 0.5);

    /****************************************************************
     *                      Variables
     ****************************************************************/
    private int delayTicks = 0;
    private int oldHandEnable = -1;
    private boolean activedOff;
    private int obbySlot;
    private BlockPos currentBlock;
    private boolean finished;
    private final HashMap<BlockPos, Integer> recentPlacements = new HashMap<>();

    public HoleFiller() {
        super("HoleFill", "Automatically places blocks in holes", 0, Category.Combat);
        this.registerSettings(
                onlyPlayer, rotate, autoSwitch, offHandObby, disableOnFinish,
                placeDelay, retryDelay, bpc, range, playerRange,
                mode,
                render, fill, outline, opacity);
    }

    @Override
    public void onEnable() {
        activedOff = false;
        PlacementUtil.onEnable();
        if (mc.player != null) {
            if (autoSwitch.booleanValue()) {
                oldHandEnable = mc.player.inventory.currentItem;
            }
            obbySlot = InventoryManager.findObsidianSlot(offHandObby.booleanValue(), activedOff);
            if (obbySlot == 9) {
                activedOff = true;
            }
        }
        finished = false;
    }

    @Override
    public void onDisable() {
        PlacementUtil.onDisable();
        if (mc.player != null && autoSwitch.booleanValue()) {
            mc.player.inventory.currentItem = oldHandEnable;
        }
        recentPlacements.clear();

        if (offHandObby.booleanValue() && AutoTotem.isActive()) {
            AutoTotem.removeObsidian();
            activedOff = false;
        }
        currentBlock = null;
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) {
            disable();
            return;
        }

        recentPlacements.replaceAll((blockPos, integer) -> integer + 1);
        recentPlacements.values().removeIf(integer -> integer > retryDelay.intValue() * 2);

        if (delayTicks <= placeDelay.intValue() * 2) {
            delayTicks++;
            return;
        }

        if (obbySlot == 9) {
            if (!(mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock
                    && ((ItemBlock) mc.player.getHeldItemOffhand().getItem()).getBlock() instanceof BlockObsidian)) {
                return;
            }
        }

        if (autoSwitch.booleanValue()) {
            int newHand = findRightBlock();
            if (newHand != -1) {
                mc.player.inventory.currentItem = newHand;
                mc.playerController.updateController();
            } else {
                return;
            }
        }

        List<BlockPos> holePos = new ArrayList<>(findHoles());
        holePos.removeAll(recentPlacements.keySet());

        AtomicInteger placements = new AtomicInteger();
        holePos = holePos.stream()
                .sorted(Comparator.comparing(blockPos -> blockPos.distanceSq(mc.player.posX, mc.player.posY, mc.player.posZ)))
                .collect(Collectors.toList());

        List<EntityPlayer> listPlayer = new ArrayList<>(mc.world.playerEntities);
        listPlayer.removeIf(player -> EntityUtil.basicChecksEntity(player) ||
                (!onlyPlayer.booleanValue() || mc.player.getDistance(player) > 6 + playerRange.doubleValue()));

        holePos.removeIf(placePos -> {
            if (placements.get() >= bpc.intValue()) {
                return false;
            }

            if (mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(placePos)).stream()
                    .anyMatch(entity -> entity instanceof EntityPlayer)) {
                return true;
            }

            boolean output = false;

            if (isHoldingRightBlock(mc.player.inventory.currentItem, mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem())
                    || offHandObby.booleanValue()) {
                boolean found = false;
                if (onlyPlayer.booleanValue()) {
                    for (EntityPlayer player : listPlayer) {
                        if (player.getDistanceSqToCenter(placePos) < playerRange.doubleValue() * playerRange.doubleValue()) {
                            found = true;
                            break;
                        }
                    }
                    if (!found)
                        return false;
                }
                if (placeBlock(placePos)) {
                    placements.getAndIncrement();
                    output = true;
                    delayTicks = 0;
                }
                recentPlacements.put(placePos, 0);
            }
            return output;
        });

        if (disableOnFinish.booleanValue() && holePos.size() == 0) {
            disable();
        }
    }

    private boolean placeBlock(BlockPos pos) {
        EnumHand handSwing = EnumHand.MAIN_HAND;
        if (offHandObby.booleanValue()) {
            int obsidianSlot = InventoryManager.findObsidianSlot(offHandObby.booleanValue(), activedOff);

            if (obsidianSlot == -1) {
                return false;
            }

            if (obsidianSlot == 9) {
                activedOff = true;
                if (mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock
                        && ((ItemBlock) mc.player.getHeldItemOffhand().getItem()).getBlock() instanceof BlockObsidian) {
                    handSwing = EnumHand.OFF_HAND;
                } else return false;
            }
        }

        boolean placed = PlacementUtil.place(pos, handSwing, rotate.booleanValue(), true);
        if (placed) {
            currentBlock = pos;
        }
        return placed;
    }

    private List<BlockPos> findHoles() {
        NonNullList<BlockPos> holes = NonNullList.create();
        List<BlockPos> blockPosList = EntityUtil.getSphere(PlayerUtil.getPlayerPos(), (float) range.doubleValue(), range.intValue(), false, true, 0);

        for (BlockPos blockPos : blockPosList) {
            if (HoleUtil.isHole(blockPos, true, true).getType() == HoleUtil.HoleType.SINGLE) {
                holes.add(blockPos);
            }
        }
        return holes;
    }

    private int findRightBlock() {
        switch (mode.value()) {
            case Both:
                int newHand = InventoryManager.findFirstBlockSlot(BlockObsidian.class, 0, 8);
                if (newHand == -1) return InventoryManager.findFirstBlockSlot(BlockEnderChest.class, 0, 8);
                else return newHand;
            case Obby:
                return InventoryManager.findFirstBlockSlot(BlockObsidian.class, 0, 8);
            case Echest:
                return InventoryManager.findFirstBlockSlot(BlockEnderChest.class, 0, 8);
            case Web:
                return InventoryManager.findFirstBlockSlot(BlockWeb.class, 0, 8);
            case Plate:
                return InventoryManager.findFirstBlockSlot(BlockPressurePlate.class, 0, 8);
            default:
                return -1;
        }
    }

    private Boolean isHoldingRightBlock(int hand, Item item) {
        if (hand == -1) return false;
        if (item instanceof ItemBlock) {
            Block block = ((ItemBlock) item).getBlock();
            switch (mode.value()) {
                case Both:
                    return block instanceof BlockObsidian || block instanceof BlockEnderChest;
                case Obby:
                    return block instanceof BlockObsidian;
                case Echest:
                    return block instanceof BlockEnderChest;
                case Web:
                    return block instanceof BlockWeb;
                case Plate:
                    return block instanceof BlockPressurePlate;
                default:
                    return false;
            }
        }
        return false;
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (render.booleanValue() && currentBlock != null && !finished) {
            GradientShader.setup((float) opacity.doubleValue());
            if (fill.booleanValue())
                RenderUtil.boxShader(currentBlock);
            if (outline.booleanValue())
                RenderUtil.outlineShader(currentBlock);
            GradientShader.finish();
        }
    }

    public enum Mode {
        Obby,
        Echest,
        Both,
        Web,
        Plate
    }
}
