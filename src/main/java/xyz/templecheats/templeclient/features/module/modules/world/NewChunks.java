package xyz.templecheats.templeclient.features.module.modules.world;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.event.events.network.PacketEvent;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.color.impl.GradientShader;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;

import java.util.ArrayList;
import java.util.List;

public class NewChunks extends Module {
    /*
     * Settings
     */
    IntSetting yLevel = new IntSetting("Y Offset", this, 0, 255, 0);
    DoubleSetting distance = new DoubleSetting("MaxDistance", this, 0.0, 50000.0, 1000.0);
    /*
     * Variables
     */
    private final List<ChunkData> chunkDataList = new ArrayList<>();

    public NewChunks() {
        super("NewChunks", "Highlights new chunks", Keyboard.KEY_NONE, Category.World);
        registerSettings(yLevel, distance);
    }

    @Override
    public void onEnable() {
        chunkDataList.clear();
    }

    @Override
    public void onDisable() {
        chunkDataList.clear();
    }

    @Listener
    public void onReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketChunkData) {
            SPacketChunkData packet = (SPacketChunkData) event.getPacket();
            if (!packet.isFullChunk()) {
                ChunkData chunk = new ChunkData(packet.getChunkX() * 16, packet.getChunkZ() * 16);
                if (!contains(chunk)) {
                    chunkDataList.add(chunk);
                }
            }
        }
    }

    @SubscribeEvent
    public void onRender3d(RenderWorldLastEvent event) {
        if (mc.getRenderViewEntity() == null) return;

        final List<ChunkData> found = new ArrayList<>();

        ICamera camera = new Frustum();
        camera.setPosition(mc.getRenderViewEntity().posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);

        for (int i = this.chunkDataList.size() - 1; i >= 0; --i) {
            ChunkData chunkData = this.chunkDataList.get(i);
            if (chunkData == null) continue;

            if (mc.player.getDistanceSq(chunkData.x , mc.player.posY, chunkData.z) > distance.doubleValue()) {
                found.add(chunkData);
            }

            AxisAlignedBB bb = new AxisAlignedBB(chunkData.x, yLevel.intValue(), chunkData.z, (chunkData.x + 16), yLevel.intValue() + 0.05, (chunkData.z + 16));
            if (!camera.isBoundingBoxInFrustum(bb)) continue;

            GradientShader.setup(0.5f);
            RenderUtil.outlineShader(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
            RenderUtil.boxShader(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
            GradientShader.finish();

            this.chunkDataList.removeAll(found);
        }
    }

    private boolean contains(ChunkData chunkData) {
        return chunkDataList.stream().anyMatch(data -> data.getX() == chunkData.getX() && data.getZ() == chunkData.getZ());
    }

    public static class ChunkData {
        private final int x;
        private final int z;

        public ChunkData(int x, int z) {
            this.x = x;
            this.z = z;
        }

        public int getX() {
            return x;
        }

        public int getZ() {
            return z;
        }
    }
}