package xyz.templecheats.templeclient.impl.modules.render;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.impl.modules.Module;
import xyz.templecheats.templeclient.api.util.render.RenderUtil;

public class StorageESP extends Module {
    public StorageESP() {
        super("StorageESP", Keyboard.KEY_NONE, Category.RENDER);
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent e) {
        for (TileEntity tileEntity : mc.world.loadedTileEntityList) {
            if (tileEntity instanceof TileEntityChest || tileEntity instanceof TileEntityEnderChest
                    || tileEntity instanceof TileEntityShulkerBox || tileEntity instanceof TileEntityDispenser) {
                RenderUtil.blockESP(tileEntity.getPos());
            }
        }
    }
}
