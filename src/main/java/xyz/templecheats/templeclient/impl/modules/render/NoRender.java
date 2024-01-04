package xyz.templecheats.templeclient.impl.modules.render;

import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.impl.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.impl.gui.clickgui.setting.Setting;

public class NoRender extends Module {
    public NoRender() {
        super("NoRender", Keyboard.KEY_NONE, Category.RENDER);

        TempleClient.settingsManager.rSetting(new Setting("Fire", this, false));
    }

    @SubscribeEvent
    public void onRenderFireOverlay(RenderBlockOverlayEvent event) {
        if (event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.FIRE &&
                TempleClient.settingsManager.getSettingByName(this.name, "Fire").getValBoolean()) {
            event.setCanceled(true);
        }
    }

}
