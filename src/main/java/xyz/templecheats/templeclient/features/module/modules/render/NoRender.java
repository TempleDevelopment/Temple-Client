package xyz.templecheats.templeclient.features.module.modules.render;

import net.minecraftforge.client.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.manager.ModuleManager;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;

import java.util.function.BooleanSupplier;

public class NoRender extends Module {
    public static NoRender INSTANCE;
    /*
     * Settings
     */

    // Overlay
    private final BooleanSetting fire = new BooleanSetting("Fire", this, false);
    private final BooleanSetting water = new BooleanSetting("Water", this, false);
    private final BooleanSetting block = new BooleanSetting("Blocks", this, false);
    private final BooleanSetting bossInfo = new BooleanSetting("BossInfo", this, false);
    private final BooleanSetting pumpkin = new BooleanSetting("Pumpkin", this, false);
    private final BooleanSetting portal = new BooleanSetting("Portal", this, false);
    private final BooleanSetting vignette = new BooleanSetting("Vignette", this, false);
    private final BooleanSetting totem = new BooleanSetting("Totem", this, false);

    // Effect
    private final BooleanSetting nauseaEffect = new BooleanSetting("Nausea", this, false);
    private final BooleanSetting blindnessEffect = new BooleanSetting("Blindness", this, false);
    private final BooleanSetting hurtCam = new BooleanSetting("NoHurtCam", this, false);
    private final BooleanSetting bobbing = new BooleanSetting("NoBob", this, false);

    // Environment
    public final BooleanSetting fog = new BooleanSetting("Fog", this, false);
    private final BooleanSetting weather = new BooleanSetting("Weather", this, false);

    public NoRender() {
        super("NoRender", "Prevents rendering of certain things", Keyboard.KEY_NONE, Category.Render);
        INSTANCE = this;
        registerSettings(fire, water, block, bossInfo, pumpkin, portal, vignette, totem, nauseaEffect, blindnessEffect, hurtCam, bobbing, fog, weather);
    }

    @SubscribeEvent
    public void onRenderGameOverlayPre(RenderGameOverlayEvent.Pre event ) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.BOSSINFO) {
            event.setCanceled(bossInfo.booleanValue());
        }
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

    public static boolean checkEffectEnabled(BooleanSupplier condition) {
        NoRender noRender = ModuleManager.getModule(NoRender.class);
        return noRender != null && noRender.isEnabled() && condition.getAsBoolean();
    }

    public static boolean preventFog() {
        return checkEffectEnabled(() -> NoRender.INSTANCE.fog.booleanValue());
    }

    public static boolean preventBlindness() {
        return checkEffectEnabled(() -> NoRender.INSTANCE.blindnessEffect.booleanValue());
    }

    public static boolean preventNausea() {
        return checkEffectEnabled(() -> NoRender.INSTANCE.nauseaEffect.booleanValue());
    }

    public static boolean preventHurtCam() {
        return checkEffectEnabled(() -> NoRender.INSTANCE.hurtCam.booleanValue());
    }

    public static boolean preventBobbing() {
        return checkEffectEnabled(() -> NoRender.INSTANCE.bobbing.booleanValue());
    }

    public static boolean preventWeather() {
        return checkEffectEnabled(() -> NoRender.INSTANCE.weather.booleanValue());
    }

    public static boolean preventTotem() {
        return checkEffectEnabled(() -> NoRender.INSTANCE.totem.booleanValue());
    }
}