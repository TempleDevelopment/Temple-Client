package xyz.templecheats.templeclient.features.module.modules.render;

import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.client.Colors;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.DoubleSetting;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;

import java.awt.*;

public class Ambience extends Module {
    public static Ambience INSTANCE;
    /*
     * Settings
     */

    // LightMap
    public final BooleanSetting LightMapState = new BooleanSetting("LightMapState", this, false);

    // Fog
    private final BooleanSetting fogState = new BooleanSetting("FogColorState", this, false);

    // Time
    // TODO: Change to better name
    private final BooleanSetting timeState = new BooleanSetting("TimeState", this, false);
    private final EnumSetting < Time > time = new EnumSetting < > ("Time", timeState.parent, Time.Midnight);
    private final DoubleSetting timeCustom = new DoubleSetting("TimeCustom", timeState.parent, 4.0, 24000.0, 600.0);
    private final BooleanSetting speedUp = new BooleanSetting("Speed Up", timeState.parent, false);
    private final DoubleSetting speed = new DoubleSetting("Speed", timeState.parent, 0.0, 100.0, 1.0);

    /*
     * Variables
     */
    double counter = 0.0;

    public Ambience() {
        super("Ambience", "Modify game environment", Keyboard.KEY_NONE, Category.Render);
        INSTANCE = this;
        registerSettings(
                fogState, LightMapState, timeState, speedUp, speed, timeCustom, time
        );
    }

    @SubscribeEvent
    public void customTime(TickEvent.RenderTickEvent event) {
        if (mc.world == null || mc.player == null) return;

        if (timeState.booleanValue()) {
            long t = 0L;
            switch (time.value()) {
                case Day:
                    t = 1000L;
                    break;
                case Sunset:
                    t = 12000;
                    break;
                case Dawn:
                    t = 23000;
                    break;
                case Night:
                    t = 13000;
                    break;
                case Midnight:
                    t = 18000L;
                    break;
                case Noon:
                    t = 6000L;
                    break;
                case Custom:
                    counter += speed.doubleValue();

                    if (counter > 24000.0 || !speedUp.booleanValue()) {
                        counter = 0.0;
                    }

                    t = (long)(timeCustom.doubleValue() + counter);
                    break;
            }
            mc.world.setWorldTime(t);
        }
    }

    @SubscribeEvent
    public void fogColor(EntityViewRenderEvent.FogColors event) {
        if (fogState.booleanValue()) {
            Color fogColor = Colors.INSTANCE.getFogColor();
            event.setRed(fogColor.getRed() / 255F);
            event.setGreen(fogColor.getGreen() / 255F);
            event.setBlue(fogColor.getBlue() / 255F);
        }
    }

    enum Time {
        Day,
        Sunset,
        Dawn,
        Night,
        Midnight,
        Noon,
        Custom
    }
}