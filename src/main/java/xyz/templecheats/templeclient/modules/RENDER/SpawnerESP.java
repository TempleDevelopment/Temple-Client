package xyz.templecheats.templeclient.modules.RENDER;

import xyz.templecheats.templeclient.modules.Module;
import xyz.templecheats.templeclient.utils.render.RenderUtil;
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