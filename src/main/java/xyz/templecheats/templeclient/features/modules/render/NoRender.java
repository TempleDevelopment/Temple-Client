package xyz.templecheats.templeclient.features.modules.render;

import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.features.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.gui.clickgui.setting.Setting;

public class NoRender extends Module {
    public NoRender() {
        super("NoRender", Keyboard.KEY_NONE, Category.RENDER);

        TempleClient.instance.settingsManager.rSetting(new Setting("Fire", this, false));
    }

    @SubscribeEvent
    public void onRenderFireOverlay(RenderBlockOverlayEvent event) {
        if (event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.FIRE &&
                TempleClient.instance.settingsManager.getSettingByName(this.name, "Fire").getValBoolean()) {
            event.setCanceled(true);
        }
    }

}
