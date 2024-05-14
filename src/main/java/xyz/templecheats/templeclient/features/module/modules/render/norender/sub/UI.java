package xyz.templecheats.templeclient.features.module.modules.render.norender.sub;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;

public class UI extends Module {
    public static UI INSTANCE;
    /*
     * Settings
     */
    private final BooleanSetting bossInfo = new BooleanSetting("Boss Info", this, false);
    public final BooleanSetting potionOverlay = new BooleanSetting("Potion Overlay", this, true);

    public UI() {
        super("UI", "UI related render settings", Keyboard.KEY_NONE, Category.Render, true);
        INSTANCE = this;
        registerSettings(bossInfo, potionOverlay);
    }
    @SubscribeEvent
    public void onRenderGameOverlayPre(RenderGameOverlayEvent.Pre event ) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.BOSSINFO) {
            event.setCanceled(bossInfo.booleanValue());
        }
        if (event.getType() == RenderGameOverlayEvent.ElementType.POTION_ICONS) {
            event.setCanceled(potionOverlay.booleanValue());
        }
    }
}