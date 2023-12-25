package xyz.templecheats.templeclient.features.modules.combat;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.client.settings.KeyBinding;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.features.modules.Module;
import xyz.templecheats.templeclient.gui.clickgui.setting.Setting;

public class AutoClicker extends Module {

    private long lastClick;
    private long hold;

    private double speed;
    private double holdLength;

    public AutoClicker() {
        super("AutoClicker", Keyboard.KEY_NONE, Category.COMBAT);

        TempleClient.instance.settingsManager.rSetting(new Setting("CPS", this, 10, 1, 100, true));
    }

    @Override
    public void onUpdate() {
        speed = TempleClient.instance.settingsManager.getSettingByName(this.name, "CPS").getValDouble();

        if (Mouse.isButtonDown(0)) {
            if (System.currentTimeMillis() - lastClick > (1.0 / speed) * 1000) {
                lastClick = System.currentTimeMillis();
                if (hold < lastClick) {
                    hold = lastClick;
                }
                int key = mc.gameSettings.keyBindAttack.getKeyCode();
                KeyBinding.setKeyBindState(key, true);
                KeyBinding.onTick(key);
            } else if (System.currentTimeMillis() - hold > holdLength * 1000) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
            }
        }
    }
}
