package xyz.templecheats.templeclient.impl.modules.world;

import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.api.event.events.player.MotionEvent;
import xyz.templecheats.templeclient.api.util.time.TimerUtil;
import xyz.templecheats.templeclient.impl.gui.setting.Setting;
import xyz.templecheats.templeclient.impl.modules.Module;
import xyz.templecheats.templeclient.impl.modules.combat.AutoCrystal;

public class Scaffold extends Module {
    private final Setting tower = new Setting("Tower", this, true);

    private final TimerUtil timer = new TimerUtil();
    private BlockPos placePos;
    private EnumFacing placeFace;

    public Scaffold() {
        super("Scaffold","Automatically places blocks under your feet", Keyboard.KEY_NONE, Category.WORLD);
        TempleClient.settingsManager.rSetting(tower);
    }

    @Listener
    public void onMotion(MotionEvent event) {
        final EnumHand placeHand;
        if(mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemBlock) {
            placeHand = EnumHand.MAIN_HAND;
        } else if(mc.player.getHeldItem(EnumHand.OFF_HAND).getItem() instanceof ItemBlock) {
            placeHand = EnumHand.MAIN_HAND;
        } else {
            return;
        }

        switch(event.getStage()) {
            case PRE:
                Entity p = mc.player;
                BlockPos bp = new BlockPos(p.posX, p.getEntityBoundingBox().minY - 1, p.posZ);

                if(isValid(bp)) {
                    return;
                }

                if(isValid(bp.down())) {
                    place(bp.down(), EnumFacing.UP);
                } else if(isValid(bp.add(-1, 0, 0))) {
                    place(bp.add(-1, 0, 0), EnumFacing.EAST);
                } else if(isValid(bp.add(1, 0, 0))) {
                    place(bp.add(1, 0, 0), EnumFacing.WEST);
                } else if(isValid(bp.add(0, 0, -1))) {
                    place(bp.add(0, 0, -1), EnumFacing.SOUTH);
                } else if(isValid(bp.add(0, 0, 1))) {
                    place(bp.add(0, 0, 1), EnumFacing.NORTH);
                } else if(isValid(bp.add(1, 0, 1))) {
                    if(isValid(bp.add(0, 0, 1))) {
                        place(bp.add(0, 0, 1), EnumFacing.NORTH);
                    } else {
                        place(bp.add(1, 0, 1), EnumFacing.EAST);
                    }
                } else if(isValid(bp.add(-1, 0, 1))) {
                    if(isValid(bp.add(-1, 0, 0))) {
                        place(bp.add(-1, 0, 0), EnumFacing.WEST);
                    } else {
                        place(bp.add(-1, 0, 1), EnumFacing.SOUTH);
                    }
                } else if(isValid(bp.add(-1, 0, -1))) {
                    if(isValid(bp.add(0, 0, -1))) {
                        place(bp.add(0, 0, -1), EnumFacing.SOUTH);
                    } else {
                        place(bp.add(-1, 0, -1), EnumFacing.WEST);
                    }
                } else if(isValid(bp.add(1, 0, -1))) {
                    if(isValid(bp.add(1, 0, 0))) {
                        place(bp.add(1, 0, 0), EnumFacing.EAST);
                    } else {
                        place(bp.add(1, 0, -1), EnumFacing.NORTH);
                    }
                }

                if(this.placePos != null) {
                    final float[] rotations = AutoCrystal.rotations(this.placePos);
                    event.setYaw(rotations[0]);
                    event.setPitch(rotations[1]);
                }
                break;
            case POST:
                if(this.placePos != null) {
                    if(this.tower.getValBoolean() && mc.player.movementInput.jump && mc.player.movementInput.moveForward == 0.0 && mc.player.movementInput.moveStrafe == 0.0) {
                        mc.player.motionX = 0;
                        mc.player.motionZ = 0;
                        mc.player.jump();

                        if(this.timer.hasReached(1000L)) {
                            mc.player.motionY = -0.3;
                            this.timer.reset();
                        }
                    }

                    mc.playerController.processRightClickBlock(mc.player, mc.world, this.placePos, this.placeFace, Vec3d.ZERO, placeHand);
                    mc.player.swingArm(placeHand);

                    this.placePos = null;
                    this.placeFace = null;
                }
                break;
        }
    }

    private void place(BlockPos pos, EnumFacing face) {
        this.placePos = pos;
        this.placeFace = face;
    }

    private boolean isValid(BlockPos pos) {
        return !(mc.world.getBlockState(pos).getBlock() instanceof BlockLiquid) && !mc.world.isAirBlock(pos);
    }
}