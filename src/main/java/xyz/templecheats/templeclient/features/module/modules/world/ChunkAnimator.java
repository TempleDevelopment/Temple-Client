package xyz.templecheats.templeclient.features.module.modules.world;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.event.events.render.RenderChunkContainerEvent;
import xyz.templecheats.templeclient.event.events.render.RenderChunkEvent;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.render.animation.Easing;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;

import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ChunkAnimator extends Module {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    public final IntSetting length = new IntSetting("Length", this, 250, 5000, 1000);
    public final EnumSetting<Easing> easing = new EnumSetting<>("Easing", this, Easing.Linear);

    /****************************************************************
     *                      Variables
     ****************************************************************/
    private final WeakHashMap<RenderChunk, AtomicLong> lifespans = new WeakHashMap<>();

    public ChunkAnimator() {
        super("ChunkAnimator", "Add animation to chunk loading", Keyboard.KEY_NONE, Category.World);
        registerSettings(length, easing);
    }

    @SubscribeEvent
    public void onChunkRender(RenderChunkEvent event) {
        if (mc.player != null) {
            if (!lifespans.containsKey(event.RenderChunk)) {
                lifespans.put(event.RenderChunk, new AtomicLong(-1L));
            }
        }
    }

    @SubscribeEvent
    public void onChunkRenderContainer(RenderChunkContainerEvent event) {
        if (lifespans.containsKey(event.RenderChunk)) {
            AtomicLong timeAlive = lifespans.get(event.RenderChunk);
            long timeClone = timeAlive.get();
            if (timeClone == -1L) {
                timeClone = System.currentTimeMillis();
                timeAlive.set(timeClone);
            }

            long timeDifference = System.currentTimeMillis() - timeClone;
            if (timeDifference <= length.intValue()) {
                double chunkY = event.RenderChunk.getPosition().getY();
                //double offsetY = chunkY / length.intValue() * timeDifference;
                double offsetY = chunkY * easing.value().inc((double) timeDifference / length.intValue());
                GlStateManager.translate(0.0, -chunkY + offsetY, 0.0);
            }
        }
    }
}
