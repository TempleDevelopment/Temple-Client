package xyz.templecheats.templeclient.impl.modules.combat;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.client.settings.KeyBinding;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.impl.modules.Module;
import xyz.templecheats.templeclient.impl.gui.setting.Setting;

public class AutoClicker extends Module {

    private long lastClick;
    private long hold;

    private double speed;
    private double holdLength;

    public AutoClicker() {
        super("AutoClicker","Automatically clicks the mouse while mouse is held down", Keyboard.KEY_NONE, Category.COMBAT);

        TempleClient.settingsManager.rSetting(new Setting("CPS", this, 10, 1, 100, true));
    }

    @Override
    public void onUpdate() {
        speed = TempleClient.settingsManager.getSettingByName(this.name, "CPS").getValDouble();

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
