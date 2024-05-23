package xyz.templecheats.templeclient.features.module.modules.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.event.events.network.PacketEvent;
import xyz.templecheats.templeclient.features.module.Module;

public class NoMineAnimation extends Module {
    private boolean isMining;
    private BlockPos lastPos;
    private EnumFacing lastFacing;

    public NoMineAnimation() {
        super("NoMineAnimation", "Hides the mining progress of the blocks you mine", Keyboard.KEY_NONE, Category.World);
    }
    @Listener
    public void recp(PacketEvent.Receive event) {
        if (event.getPacket() instanceof CPacketPlayerDigging) {
            final CPacketPlayerDigging cPacketPlayerDigging = (CPacketPlayerDigging) event.getPacket();
            for (final Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity((Entity) null,
                    new AxisAlignedBB(cPacketPlayerDigging.getPosition()))) {
                if (entity instanceof EntityEnderCrystal) {
                    this.resetMining();
                    return;
                }
                if (entity instanceof EntityLivingBase) {
                    this.resetMining();
                    return;
                }
            }
            if (cPacketPlayerDigging.getAction().equals((Object) CPacketPlayerDigging.Action.START_DESTROY_BLOCK)) {
                this.isMining = true;
                this.setMiningInfo(cPacketPlayerDigging.getPosition(), cPacketPlayerDigging.getFacing());
            }
            if (cPacketPlayerDigging.getAction().equals((Object) CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK)) {
                this.resetMining();
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc == null || mc.player == null)
            return;

        if (!mc.gameSettings.keyBindAttack.isKeyDown()) {
            this.resetMining();
            return;
        }
        if (this.isMining && this.lastPos != null && this.lastFacing != null) {
            mc.player.connection
                    .sendPacket((Packet) new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK,
                            this.lastPos, this.lastFacing));
        }
    }

    private void setMiningInfo(final BlockPos lastPos, final EnumFacing lastFacing) {
        this.lastPos = lastPos;
        this.lastFacing = lastFacing;
    }

    public void resetMining() {
        this.isMining = false;
        this.lastPos = null;
        this.lastFacing = null;
    }

}