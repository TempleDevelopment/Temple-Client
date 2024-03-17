package xyz.templecheats.templeclient.features.module.modules.render;

import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.manager.ModuleManager;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;

public class NoRender extends Module {
    private final BooleanSetting fireOverlay = new BooleanSetting("Fire", this, false);
    private final BooleanSetting waterOverlay = new BooleanSetting("Water", this, false);
    private final BooleanSetting blindnessEffect = new BooleanSetting("Blindness", this, false);
    private final BooleanSetting nauseaEffect = new BooleanSetting("Nausea", this, false);
    private final BooleanSetting fog = new BooleanSetting("Fog", this, false);
    private final BooleanSetting noHurtCam = new BooleanSetting("NoHurtCam", this, false);

    public NoRender() {
        super("NoRender","Disables some rendering", Keyboard.KEY_NONE, Category.Render);

        registerSettings(fireOverlay, waterOverlay, blindnessEffect, nauseaEffect, fog, noHurtCam);
    }

    @SubscribeEvent
    public void onRenderFireOverlay(RenderBlockOverlayEvent event) {
        if (event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.FIRE) {
            if (fireOverlay.booleanValue()) event.setCanceled(true);
        } else if (event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.WATER) {
            if (waterOverlay.booleanValue()) event.setCanceled(true);
        }
    }

    public static boolean preventFog() {
        NoRender noRender = ModuleManager.getModule(NoRender.class);
        return noRender != null && noRender.isEnabled() && noRender.fog.booleanValue();
    }

    public static boolean preventBlindness() {
        NoRender noRender = ModuleManager.getModule(NoRender.class);
        return noRender != null && noRender.isEnabled() && noRender.blindnessEffect.booleanValue();
    }

    public static boolean preventNausea() {
        NoRender noRender = ModuleManager.getModule(NoRender.class);
        return noRender != null && noRender.isEnabled() && noRender.nauseaEffect.booleanValue();
    }

    public static boolean preventHurtCam() {
        NoRender noRender = ModuleManager.getModule(NoRender.class);
        return noRender != null && noRender.isEnabled() && noRender.noHurtCam.booleanValue();
    }
}