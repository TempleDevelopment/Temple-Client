package xyz.templecheats.templeclient.features.module.modules.render;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;

public class FullBright extends Module {
    public final EnumSetting<Mode> mode = new EnumSetting<>("Mode", this, Mode.Gamma);

    public FullBright() {
        super("Fullbright", "Makes the world bright", Keyboard.KEY_NONE, Category.Render);
        registerSettings(mode);
    }

    private void brightModify() {
        if (mode.value() == Mode.Gamma) {
            mc.gameSettings.gammaSetting = 100f;
            if (mc.player.isPotionActive(Potion.getPotionById(16))) {
                mc.player.removePotionEffect(Potion.getPotionById(16));
            }
        } else if (mode.value() == Mode.Potion) {
            mc.gameSettings.gammaSetting = 0;
            mc.player.addPotionEffect(new PotionEffect(Potion.getPotionById(16), 9999, 1));
        }
    }

    @Override
    public void onUpdate() {
        brightModify();
    }

    @Override
    public void onEnable() {
        brightModify();
    }

    @Override
    public void onDisable() {
        mc.gameSettings.gammaSetting = 1.0f;
        if (mc.player.isPotionActive(Potion.getPotionById(16))) {
            mc.player.removePotionEffect(Potion.getPotionById(16));
        }
    }

    private enum Mode {
        Gamma,
        Potion
    }
}