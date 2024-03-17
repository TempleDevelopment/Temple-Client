package xyz.templecheats.templeclient.features.module.modules.render;

import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.render.RenderUtil;

public class SpawnerESP extends Module {
    public SpawnerESP() {
        super("SpawnerESP","Highlights mob spawners", Keyboard.KEY_NONE, Category.Render);
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