/**
 * This Jesus was made by Sepukku, and was modified.
 */

package xyz.templecheats.templeclient.features.module.modules.movement;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.event.EventStageable;
import xyz.templecheats.templeclient.event.events.network.PacketEvent;
import xyz.templecheats.templeclient.event.events.player.MotionEvent;
import xyz.templecheats.templeclient.event.events.world.LiquidCollisionEvent;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.mixins.IMixinCPacketPlayer;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;

public class Jesus extends Module {
    
    /**
     * Settings
     */
    private final EnumSetting<Mode> mode = new EnumSetting<>("Mode", this, Mode.NCP);
    
    /**
     * Constants
     */
    private final static double OFFSET = 0.05;
    
    public Jesus() {
        super("Jesus", "Allows you to walk on water (amen)", Keyboard.KEY_NONE, Category.Movement);
        this.registerSettings(mode);
    }
    
    @Listener
    public void onLiquidCollision(LiquidCollisionEvent event) {
        if(this.checkCollide() && mc.player.motionY < 0.1 && event.getBlockPos().getY() < mc.player.posY - OFFSET) {
            final double offset = mc.player.getRidingEntity() != null ? OFFSET : this.mode.value() == Mode.Bounce ? 0.1 : 0;
            event.setBoundingBox(new AxisAlignedBB(0, 0, 0, 1, 1 - offset, 1));
        }
    }
    
    @Listener
    public void onMotion(MotionEvent event) {
        if(event.getStage() != EventStageable.EventStage.PRE) {
            return;
        }
        
        if(!mc.player.isSneaking() && !mc.player.noClip && !mc.gameSettings.keyBindJump.isKeyDown() && isInLiquid()) {
            mc.player.motionY = 0.1;
        }
    }
    
    @Listener
    public void onPacketSend(PacketEvent.Send event) {
        if (mode.value() != Mode.Vanilla) return;
        
        if(!(event.getPacket() instanceof CPacketPlayer)) {
            return;
        }
        
        final CPacketPlayer packet = (CPacketPlayer) event.getPacket();
        final IMixinCPacketPlayer accessor = (IMixinCPacketPlayer) packet;
        
        if(!accessor.isMoving()) {
            return;
        }
        
        if(mc.player.getRidingEntity() == null && !mc.gameSettings.keyBindJump.isKeyDown() && !isInLiquid() && isOnLiquid(OFFSET) && checkCollide() && mc.player.ticksExisted % 3 == 0) {
            accessor.setY(packet.getY(OFFSET) - OFFSET);
        }
    }
    
    @Override
    public String getHudInfo() {
        return this.mode.value().toString();
    }
    
    public static boolean isInLiquid() {
        return isOnLiquid(0);
    }
    
    public static boolean isOnLiquid(double offset) {
        if(getMountOrPlayer().fallDistance >= 3.0F) {
            return false;
        }
        
        final AxisAlignedBB bb = getMountOrPlayer().getEntityBoundingBox().offset(0, -offset, 0);
        
        for(int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX + 1); x++) {
            for(int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ + 1); z++) {
                final Block block = mc.world.getBlockState(new BlockPos(x, (int) bb.minY, z)).getBlock();
                
                if(block != Blocks.AIR) {
                    return block instanceof BlockLiquid;
                }
            }
        }
        
        return false;
    }
    
    private boolean checkCollide() {
        if(mc.player.isSneaking()) {
            return false;
        }
        
        return getMountOrPlayer().fallDistance < 3.0F;
    }
    
    private static Entity getMountOrPlayer() {
        return mc.player.getRidingEntity() != null ? mc.player.getRidingEntity() : mc.player;
    }

    private enum Mode {
        NCP,
        Vanilla,
        Bounce
    }
}
