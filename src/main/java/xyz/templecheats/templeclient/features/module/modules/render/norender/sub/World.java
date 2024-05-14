package xyz.templecheats.templeclient.features.module.modules.render.norender.sub;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.render.norender.NoRender;
import xyz.templecheats.templeclient.manager.ModuleManager;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;

import java.util.function.BooleanSupplier;

public class World extends Module {
    public static World INSTANCE;
    /*
     * Settings
     */
    public final BooleanSetting fog = new BooleanSetting("Fog", this, false);
    private final BooleanSetting weather = new BooleanSetting("Weather", this, false);

    public World() {
        super("World", "World related render settings", Keyboard.KEY_NONE, Category.Render, true);
        INSTANCE = this;
        registerSettings(fog, weather);
    }

    public static boolean checkEffectEnabled(BooleanSupplier condition) {
        NoRender noRender = ModuleManager.getModule(NoRender.class);
        return noRender != null && noRender.isEnabled() && condition.getAsBoolean();
    }

    public static boolean preventFog() {
        return checkEffectEnabled(() -> INSTANCE.fog.booleanValue());
    }

    public static boolean preventWeather() {
        return checkEffectEnabled(() -> INSTANCE.weather.booleanValue());
    }
}