package xyz.templecheats.templeclient.features.module.modules.render;

import net.minecraft.tileentity.*;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.render.RenderUtil;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;

public class StorageESP extends Module {
    private final BooleanSetting chests = new BooleanSetting("Chests", this, true);
    private final BooleanSetting eChests = new BooleanSetting("EChests", this, true);
    private final BooleanSetting shulkers = new BooleanSetting("Shulkers", this, true);
    private final BooleanSetting dispensers = new BooleanSetting("Dispensers", this, true);
    private final BooleanSetting furnaces = new BooleanSetting("Furnaces", this, true);

    public StorageESP() {
        super("StorageESP","Highlights different types of storage entities", Keyboard.KEY_NONE, Category.Render);

        registerSettings(chests, eChests, shulkers, dispensers, furnaces);
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent e) {
        for (TileEntity tileEntity : mc.world.loadedTileEntityList) {
            if (chests.booleanValue() && tileEntity instanceof TileEntityChest
                    || eChests.booleanValue() && tileEntity instanceof TileEntityEnderChest
                    || shulkers.booleanValue() && tileEntity instanceof TileEntityShulkerBox
                    || dispensers.booleanValue() && tileEntity instanceof TileEntityDispenser
                    || furnaces.booleanValue() && tileEntity instanceof TileEntityFurnace) {
                RenderUtil.blockESP(tileEntity.getPos());
            }
        }
    }
}