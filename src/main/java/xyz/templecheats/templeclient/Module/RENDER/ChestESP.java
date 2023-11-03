package xyz.templecheats.templeclient.Module.RENDER;

import xyz.templecheats.templeclient.Module.Module;
import xyz.templecheats.templeclient.Utils.RenderUtils;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class ChestESP extends Module {
    public ChestESP() {
        super("ChestESP", Keyboard.KEY_NONE, Category.RENDER);
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent e) {
        for (Object c : mc.world.loadedTileEntityList) {
            if (c instanceof TileEntityChest) {
                RenderUtils.blockESP(((TileEntityChest) c).getPos());
            }
        }
    }
}