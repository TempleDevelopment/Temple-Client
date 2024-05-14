package xyz.templecheats.templeclient.features.module.modules.render.norender.sub;

import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.render.norender.NoRender;
import xyz.templecheats.templeclient.manager.ModuleManager;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;

import java.util.function.BooleanSupplier;

public class Overlays extends Module {
    public static Overlays INSTANCE;
    /*
     * Settings
     */
    private final BooleanSetting fire = new BooleanSetting("Fire", this, false);
    private final BooleanSetting water = new BooleanSetting("Water", this, false);
    private final BooleanSetting block = new BooleanSetting("Blocks", this, false);
    private final BooleanSetting pumpkin = new BooleanSetting("Pumpkin", this, false);
    private final BooleanSetting portal = new BooleanSetting("Portal", this, false);
    private final BooleanSetting vignette = new BooleanSetting("Vignette", this, false);

    public Overlays() {
        super("Overlays", "Overlay related render settings", Keyboard.KEY_NONE, Category.Render, true);
        INSTANCE = this;
        registerSettings(fire, water, block, pumpkin, portal, vignette);
    }
    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {
        if (!event.isCancelable()) {
            return;
        }
        switch (event.getType()) {
            case HELMET: {
                event.setCanceled(pumpkin.booleanValue());
                break;
            }
            case PORTAL: {
                event.setCanceled(portal.booleanValue());
                break;
            }
            case VIGNETTE: {
                event.setCanceled(vignette.booleanValue());
                break;
            }
        }
    }

    @SubscribeEvent
    public void onRenderBlockOverlay(RenderBlockOverlayEvent event) {
        RenderBlockOverlayEvent.OverlayType overlayType = event.getOverlayType();
        switch (overlayType) {
            case FIRE:
                event.setCanceled(fire.booleanValue());
                break;
            case WATER:
                event.setCanceled(water.booleanValue());
                break;
            case BLOCK:
                event.setCanceled(block.booleanValue());
                break;
        }
    }
}