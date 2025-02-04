package xyz.templecheats.templeclient.features.module.modules.combat;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;

import java.util.concurrent.TimeUnit;

public class AutoWeb extends Module {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    private final IntSetting delay = new IntSetting("Delay", this, 0, 10, 0);
    private final BooleanSetting toggled = new BooleanSetting("Toggle", this, false);
    private final EnumSetting<Target> target = new EnumSetting<>("Target", this, Target.Self);

    /****************************************************************
     *                      Variables
     ****************************************************************/
    BlockPos feet;
    int d;
    public static float yaw;
    public static float pitch;

    public AutoWeb() {
        super("AutoWeb", "Automatically places web", Keyboard.KEY_NONE, Category.Combat);
        registerSettings(toggled, delay, target);
    }

    public boolean isInBlockRange(Entity target) {
        return (target.getDistance(mc.player) <= 4.0F);
    }

    public static boolean canBeClicked(BlockPos pos) {
        return Minecraft.getMinecraft().world.getBlockState(pos).getBlock()
                .canCollideCheck(Minecraft.getMinecraft().world.getBlockState(pos), false);
    }

    private boolean isStackWeb(ItemStack stack) {
        return (stack != null && stack.getItem() == Item.getItemById(30));
    }

    private boolean doesHotbarHaveWeb() {
        for (int i = 36; i < 45; i++) {
            ItemStack stack = mc.player.inventoryContainer.getSlot(i).getStack();
            if (stack != null && isStackWeb(stack)) {
                return true;
            }
        }
        return false;
    }

    public static Block getBlock(BlockPos pos) {
        return getState(pos).getBlock();
    }

    public static IBlockState getState(BlockPos pos) {
        return Minecraft.getMinecraft().world.getBlockState(pos);
    }

    public static boolean placeBlockLegit(BlockPos pos) {
        Vec3d eyesPos = new Vec3d(Minecraft.getMinecraft().player.posX,
                Minecraft.getMinecraft().player.posY + Minecraft.getMinecraft().player.getEyeHeight(),
                Minecraft.getMinecraft().player.posZ);
        Vec3d posVec = (new Vec3d(pos)).add(0.5D, 0.5D, 0.5D);
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbor = pos.offset(side);
            if (canBeClicked(neighbor)) {
                Vec3d hitVec = posVec.add((new Vec3d(side.getDirectionVec())).scale(0.5D));
                if (eyesPos.squareDistanceTo(hitVec) <= 36.0D) {
                    Minecraft.getMinecraft().playerController.processRightClickBlock(Minecraft.getMinecraft().player,
                            Minecraft.getMinecraft().world, neighbor, side.getOpposite(), hitVec, EnumHand.MAIN_HAND);
                    Minecraft.getMinecraft().player.swingArm(EnumHand.MAIN_HAND);
                    try {
                        TimeUnit.MILLISECONDS.sleep(10L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc.player == null)
            return;
        if (mc.player.isHandActive()) {
            return;
        }
        trap(mc.player);
    }

    public static double roundToHalf(double d) {
        return Math.round(d * 2.0D) / 2.0D;
    }

    public void onEnable() {
        if (mc.player == null) {
            this.disable();
            return;
        }

        this.d = 0;
    }

    private void trap(EntityPlayer player) {
        switch (target.value()) {
            case Self:
                trapSelf(player);
                break;
            case Holes:
                trapHoles();
                break;
        }
    }

    private void trapSelf(EntityPlayer player) {
        if (player.moveForward == 0.0D && player.moveStrafing == 0.0D && player.moveForward == 0.0D) {
            this.d++;
        }
        if (player.moveForward != 0.0D || player.moveStrafing != 0.0D || player.moveForward != 0.0D) {
            this.d = 0;
        }
        if (!doesHotbarHaveWeb()) {
            this.d = 0;
        }
        if (this.d == this.delay.intValue() && doesHotbarHaveWeb()) {
            this.feet = new BlockPos(player.posX, player.posY, player.posZ);

            for (int i = 36; i < 45; i++) {
                ItemStack stack = mc.player.inventoryContainer.getSlot(i).getStack();
                if (stack != null && isStackWeb(stack)) {
                    int oldSlot = mc.player.inventory.currentItem;
                    if (mc.world.getBlockState(this.feet).getMaterial().isReplaceable()) {
                        mc.player.inventory.currentItem = i - 36;
                        if (mc.world.getBlockState(this.feet).getMaterial().isReplaceable()) {
                            placeBlockLegit(this.feet);
                        }

                        mc.player.inventory.currentItem = oldSlot;
                        this.d = 0;
                        if (toggled.booleanValue()) {
                            toggle();
                        }
                        break;
                    }
                    this.d = 0;
                }
                this.d = 0;
            }
        }
    }

    private void trapHoles() {
        for (BlockPos blockPos : BlockPos.getAllInBox(mc.player.getPosition().add(-5, -5, -5), mc.player.getPosition().add(5, 5, 5))) {
            if (isValidHole(blockPos) && doesHotbarHaveWeb()) {
                for (int i = 36; i < 45; i++) {
                    ItemStack stack = mc.player.inventoryContainer.getSlot(i).getStack();
                    if (stack != null && isStackWeb(stack)) {
                        int oldSlot = mc.player.inventory.currentItem;
                        mc.player.inventory.currentItem = i - 36;
                        placeBlockLegit(blockPos);
                        mc.player.inventory.currentItem = oldSlot;
                        if (toggled.booleanValue()) {
                            toggle();
                        }
                        break;
                    }
                }
            }
        }
    }

    private boolean isValidHole(BlockPos pos) {
        return mc.world.getBlockState(pos).getMaterial().isReplaceable() &&
                mc.world.getBlockState(pos.add(0, 1, 0)).getMaterial().isReplaceable() &&
                mc.world.getBlockState(pos.add(0, -1, 0)).getMaterial().isSolid() &&
                mc.world.getBlockState(pos.add(1, 0, 0)).getMaterial().isSolid() &&
                mc.world.getBlockState(pos.add(0, 0, 1)).getMaterial().isSolid() &&
                mc.world.getBlockState(pos.add(-1, 0, 0)).getMaterial().isSolid() &&
                mc.world.getBlockState(pos.add(0, 0, -1)).getMaterial().isSolid() &&
                mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos)).isEmpty();
    }

    public void onDisable() {
        this.d = 0;
        yaw = mc.player.rotationYaw;
        pitch = mc.player.rotationPitch;
    }

    public enum Target {
        Self,
        Holes
    }
}
