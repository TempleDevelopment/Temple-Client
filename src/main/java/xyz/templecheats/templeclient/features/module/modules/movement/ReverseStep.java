package xyz.templecheats.templeclient.features.module.modules.movement;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.event.events.network.PacketEvent;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;
import xyz.templecheats.templeclient.util.time.TimerUtil;

public class ReverseStep extends Module {
    /*
     * Settings
     */
    public final IntSetting fallSpeed = new IntSetting("Fall Speed", this, 1, 50, 2);
    private final IntSetting height =new IntSetting("Height", this, 1, 20, 10);
    private final TimerUtil flagTime = new TimerUtil();

    public ReverseStep() {
        super("ReverseStep", "Allows you to go down blocks fast", Keyboard.KEY_NONE, Module.Category.Movement);
        registerSettings(fallSpeed, height);
    }

    private boolean environmentCheck() {
        return mc.player.isElytraFlying() ||
                mc.player.isOnLadder() ||
                mc.player.capabilities.isFlying ||
                mc.player.motionY > 0.0 ||
                mc.player.isEntityInsideOpaqueBlock() ||
                mc.player.isInWater() ||
                mc.player.isInLava() ||
                mc.player.isOnLadder();
    }

    private void reset() {
        flagTime.reset();
    }

    @Override
    public void onEnable() {
        reset();
    }

    @Override
    public void onDisable() {
        reset();
    }

    @Override
    public void onUpdate() {
        if (this.height.intValue() > 0 && this.traceDown() > this.height.intValue() ||
                environmentCheck() ||
                !flagTime.passedMs(1000) ||
                mc.player == null ||
                mc.world == null
        ) {
            return;
        }
        if (this.traceDown() != 0 && this.traceDown() <= this.height.intValue() && this.trace() && mc.player.onGround) {
            mc.player.motionX *= 0.05f;
            mc.player.motionZ *= 0.05f;
        }
        if (mc.player.onGround)
            mc.player.motionY = -fallSpeed.intValue();
    }

    @Listener
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            reset();
        }
    }

    private int traceDown() {
        int ret = 0;
        int y = (int)Math.round(mc.player.posY) - 1;
        for (int tracey = y; tracey >= 0; tracey--) {
            RayTraceResult trace = mc.world.rayTraceBlocks(mc.player.getPositionVector(), new Vec3d(mc.player.posX, tracey, mc.player.posZ), false);
            if (trace != null && trace.typeOfHit == RayTraceResult.Type.BLOCK)
                return ret;
            ret++;
        }
        return ret;
    }

    private boolean trace() {
        AxisAlignedBB bbox = mc.player.getEntityBoundingBox();
        Vec3d base = bbox.getCenter();

        double minX = bbox.minX;
        double minZ = bbox.minZ;
        double maxX = bbox.maxX;
        double maxZ = bbox.maxZ;

        Vec3d[] checkPoints = {
                base,
                new Vec3d(minX, base.y, minZ),
                new Vec3d(maxX, base.y, minZ),
                new Vec3d(minX, base.y, maxZ),
                new Vec3d(maxX, base.y, maxZ)
        };

        for (Vec3d point : checkPoints) {
            Vec3d target = new Vec3d(point.x, point.y - 1, point.z);
            RayTraceResult result = mc.world.rayTraceBlocks(point, target, true);
            if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
                return false;
            }
        }

        IBlockState state = mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ));
        return state == null || state.getBlock() == Blocks.AIR;
    }
}
