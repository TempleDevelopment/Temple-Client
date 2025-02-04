package xyz.templecheats.templeclient.features.module.modules.render.norender.sub;

import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.features.module.modules.render.norender.NoRender;
import xyz.templecheats.templeclient.manager.ModuleManager;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;

import java.util.function.BooleanSupplier;

public class Player extends Module {
    public static Player INSTANCE;
    /****************************************************************
     *                      Settings
     ****************************************************************/
    private final BooleanSetting blindnessEffect = new BooleanSetting("Blindness", this, false);
    private final BooleanSetting bobbing = new BooleanSetting("NoBob", this, false);
    private final BooleanSetting hurtCam = new BooleanSetting("NoHurtCam", this, false);
    private final BooleanSetting nauseaEffect = new BooleanSetting("Nausea", this, false);
    private final BooleanSetting totem = new BooleanSetting("Totem", this, false);

    public Player() {
        super("Player", "Player related render settings", Keyboard.KEY_NONE, Category.Render, true);
        INSTANCE = this;
        registerSettings(blindnessEffect, bobbing, hurtCam, nauseaEffect, totem);
    }

    public static boolean checkEffectEnabled(BooleanSupplier condition) {
        NoRender noRender = ModuleManager.getModule(NoRender.class);
        return noRender != null && noRender.isEnabled() && condition.getAsBoolean();
    }

    public static boolean preventBlindness() {
        return checkEffectEnabled(() -> INSTANCE.blindnessEffect.booleanValue());
    }

    public static boolean preventNausea() {
        return checkEffectEnabled(() -> INSTANCE.nauseaEffect.booleanValue());
    }

    public static boolean preventHurtCam() {
        return checkEffectEnabled(() -> INSTANCE.hurtCam.booleanValue());
    }

    public static boolean preventBobbing() {
        return checkEffectEnabled(() -> INSTANCE.bobbing.booleanValue());
    }

    public static boolean preventTotem() {
        return checkEffectEnabled(() -> INSTANCE.totem.booleanValue());
    }
}