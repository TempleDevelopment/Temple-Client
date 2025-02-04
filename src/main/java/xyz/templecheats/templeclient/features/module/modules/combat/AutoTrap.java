package xyz.templecheats.templeclient.features.module.modules.combat;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.render.Freecam;
import xyz.templecheats.templeclient.util.misc.Offsets;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.util.render.shader.impl.GradientShader;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AutoTrap extends Module {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    private final IntSetting blocksPerTick = new IntSetting("Blocks Per Tick", this, 1, 10, 1);
    private final BooleanSetting autoDisable = new BooleanSetting("Auto Disable", this, true);
    private final IntSetting range = new IntSetting("Range", this, 1, 6, 4);
    private final BooleanSetting render = new BooleanSetting("Render", this, true);
    private final BooleanSetting fill = new BooleanSetting("Box Fill", this, true);
    private final BooleanSetting outline = new BooleanSetting("Box Outline", this, true);
    private final DoubleSetting opacity = new DoubleSetting("Opacity", this, 0.0, 1, 0.5);

    /****************************************************************
     *                      Variables
     ****************************************************************/
    private final List<Vec3d> positions = new ArrayList<>(Arrays.asList(
            Offsets.AUTO_TRAP));
    private boolean finished;
    private BlockPos currentBlock;


    public AutoTrap() {
        super("AutoTrap", "Automatically traps enemies with obsidian", Keyboard.KEY_NONE, Category.Combat);
        registerSettings(autoDisable, blocksPerTick, range, render, fill, outline, opacity);
    }

    @Override
    public void onEnable() {
        finished = false;
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

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (Freecam.isFreecamActive()) {
            return;
        }

        if (Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().world == null)
            return;

        if (finished && autoDisable.booleanValue())
            disable();

        int blocksPlaced = 0;

        for (Vec3d position : positions) {
            EntityPlayer closestPlayer = getClosestPlayer();
            if (closestPlayer != null && Minecraft.getMinecraft().player.getDistance(closestPlayer) <= range.intValue()) {
                BlockPos pos = new BlockPos(position.add(getClosestPlayer().getPositionVector()));

                Block block = Minecraft.getMinecraft().world.getBlockState(pos).getBlock();
                if (block.isReplaceable(Minecraft.getMinecraft().world, pos) || block.equals(Blocks.AIR)) {
                    int oldSlot = Minecraft.getMinecraft().player.inventory.currentItem;
                    Minecraft.getMinecraft().player.inventory.currentItem = getSlot(Blocks.OBSIDIAN);
                    placeBlock(pos);
                    Minecraft.getMinecraft().player.inventory.currentItem = oldSlot;
                    blocksPlaced++;

                    currentBlock = pos;

                    if (blocksPlaced == blocksPerTick.intValue())
                        return;
                }
            }
        }
        if (blocksPlaced == 0)
            finished = true;
    }

    @SubscribeEvent
    public void onBlockBreak(PlayerInteractEvent.LeftClickBlock event) {
        if (!isEnabled())
            return;

        BlockPos pos = event.getPos();
        Block block = event.getWorld().getBlockState(pos).getBlock();
        if (block != Blocks.AIR && block != Blocks.OBSIDIAN)
            placeBlock(pos);
    }

    private EntityPlayer getClosestPlayer() {
        EntityPlayer closestPlayer = null;
        double range = 1000;
        for (EntityPlayer playerEntity : Minecraft.getMinecraft().world.playerEntities) {
            if (!playerEntity.equals(Minecraft.getMinecraft().player) && !TempleClient.friendManager.isFriend(playerEntity.getName())) {
                double distance = Minecraft.getMinecraft().player.getDistance(playerEntity);
                if (distance < range) {
                    closestPlayer = playerEntity;
                    range = distance;
                }
            }
        }
        return closestPlayer;
    }

    private static boolean isIntercepted(BlockPos pos) {
        for (Entity entity : Minecraft.getMinecraft().world.loadedEntityList) {
            if (new AxisAlignedBB(pos).intersects(entity.getEntityBoundingBox()))
                return true;
        }
        return false;
    }

    private static int getSlot(Block block) {
        for (int i = 0; i < 9; i++) {
            Item item = Minecraft.getMinecraft().player.inventory.getStackInSlot(i).getItem();
            if (item instanceof ItemBlock && ((ItemBlock) item).getBlock().equals(block)) {
                return i;
            }
        }
        return -1;
    }

    public static void placeBlock(BlockPos pos) {
        for (EnumFacing enumFacing : EnumFacing.values()) {
            if (!Minecraft.getMinecraft().world.getBlockState(pos.offset(enumFacing)).getBlock().equals(Blocks.AIR)
                    && !isIntercepted(pos)) {
                Vec3d vec = new Vec3d(pos.getX() + 0.5D + (double) enumFacing.getXOffset() * 0.5D,
                        pos.getY() + 0.5D + (double) enumFacing.getYOffset() * 0.5D,
                        pos.getZ() + 0.5D + (double) enumFacing.getZOffset() * 0.5D);

                float[] old = new float[]{Minecraft.getMinecraft().player.rotationYaw,
                        Minecraft.getMinecraft().player.rotationPitch};

                Minecraft.getMinecraft().player.connection.sendPacket(new CPacketPlayer.Rotation(
                        (float) Math.toDegrees(Math.atan2((vec.z - Minecraft.getMinecraft().player.posZ),
                                (vec.x - Minecraft.getMinecraft().player.posX))) - 90.0F,
                        (float) (-Math.toDegrees(Math.atan2(
                                (vec.y - (Minecraft.getMinecraft().player.posY
                                        + (double) Minecraft.getMinecraft().player.getEyeHeight())),
                                (Math.sqrt((vec.x - Minecraft.getMinecraft().player.posX)
                                        * (vec.x - Minecraft.getMinecraft().player.posX)
                                        + (vec.z - Minecraft.getMinecraft().player.posZ)
                                        * (vec.z - Minecraft.getMinecraft().player.posZ)))))),
                        Minecraft.getMinecraft().player.onGround));
                Minecraft.getMinecraft().player.connection.sendPacket(new CPacketEntityAction(
                        Minecraft.getMinecraft().player, CPacketEntityAction.Action.START_SNEAKING));
                Minecraft.getMinecraft().playerController.processRightClickBlock(Minecraft.getMinecraft().player,
                        Minecraft.getMinecraft().world, pos.offset(enumFacing), enumFacing.getOpposite(),
                        new Vec3d(pos), EnumHand.MAIN_HAND);
                Minecraft.getMinecraft().player.swingArm(EnumHand.MAIN_HAND);
                Minecraft.getMinecraft().player.connection.sendPacket(new CPacketEntityAction(
                        Minecraft.getMinecraft().player, CPacketEntityAction.Action.STOP_SNEAKING));
                Minecraft.getMinecraft().player.connection.sendPacket(
                        new CPacketPlayer.Rotation(old[0], old[1], Minecraft.getMinecraft().player.onGround));

                return;
            }
        }
    }
}