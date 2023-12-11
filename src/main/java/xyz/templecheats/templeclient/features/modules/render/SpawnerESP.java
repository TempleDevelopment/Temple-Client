package xyz.templecheats.templeclient.features.modules.render;

import xyz.templecheats.templeclient.features.modules.Module;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class SpawnerESP extends Module {
    public SpawnerESP() {
        super("SpawnerESP", Keyboard.KEY_NONE, Category.RENDER);
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent e) {
        for (Object c : mc.world.loadedTileEntityList) {
            if (c instanceof TileEntityMobSpawner) {
                RenderUtil.blockESP(((TileEntityMobSpawner) c).getPos());
            }
        }
    }
}