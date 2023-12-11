package xyz.templecheats.templeclient.gui.menu;

import xyz.templecheats.templeclient.features.modules.client.Panic;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiEventsListener {
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onGuiOpenEvent(GuiOpenEvent e) {
        if (e.getGui() instanceof GuiMainMenu && !Panic.isPanic) {
            e.setGui(new CustomMainMenu());
        }
    }
}