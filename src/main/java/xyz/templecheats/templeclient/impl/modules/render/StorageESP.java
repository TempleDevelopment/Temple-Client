package xyz.templecheats.templeclient.impl.modules.render;

import net.minecraft.tileentity.*;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.api.setting.Setting;
import xyz.templecheats.templeclient.api.util.render.RenderUtil;
import xyz.templecheats.templeclient.impl.modules.Module;

public class StorageESP extends Module {
    private final Setting chests = new Setting("Chests", this, true);
    private final Setting eChests = new Setting("EChests", this, true);
    private final Setting shulkers = new Setting("Shulkers", this, true);
    private final Setting dispensers = new Setting("Dispensers", this, true);
    private final Setting furnaces = new Setting("Furnaces", this, true);

    public StorageESP() {
        super("StorageESP","Highlights different types of storage entities", Keyboard.KEY_NONE, Category.Render);
        TempleClient.settingsManager.rSetting(chests);
        TempleClient.settingsManager.rSetting(eChests);
        TempleClient.settingsManager.rSetting(shulkers);
        TempleClient.settingsManager.rSetting(dispensers);
        TempleClient.settingsManager.rSetting(furnaces);
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent e) {
        for (TileEntity tileEntity : mc.world.loadedTileEntityList) {
            if (chests.getValBoolean() && tileEntity instanceof TileEntityChest
                    || eChests.getValBoolean() && tileEntity instanceof TileEntityEnderChest
                    || shulkers.getValBoolean() && tileEntity instanceof TileEntityShulkerBox
                    || dispensers.getValBoolean() && tileEntity instanceof TileEntityDispenser
                    || furnaces.getValBoolean() && tileEntity instanceof TileEntityFurnace) {
                RenderUtil.blockESP(tileEntity.getPos());
            }
        }
    }
}