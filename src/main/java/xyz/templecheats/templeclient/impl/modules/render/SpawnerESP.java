package xyz.templecheats.templeclient.impl.modules.render;

import xyz.templecheats.templeclient.impl.modules.Module;
import xyz.templecheats.templeclient.api.util.render.RenderUtil;
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