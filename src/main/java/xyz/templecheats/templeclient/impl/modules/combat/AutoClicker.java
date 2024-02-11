package xyz.templecheats.templeclient.impl.modules.combat;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.client.settings.KeyBinding;
import xyz.templecheats.templeclient.TempleClient;
import xyz.templecheats.templeclient.impl.modules.Module;
import xyz.templecheats.templeclient.api.setting.Setting;

import java.util.ArrayList;

public class AutoClicker extends Module {

    private long lastClick;
    private long hold;

    private double speed;
    private double holdLength;
    private Setting clickTypeSetting;

    public AutoClicker() {
        super("AutoClicker","Automatically clicks the mouse while mouse is held down", Keyboard.KEY_NONE, Category.Combat);

        ArrayList<String> clickOptions = new ArrayList<>();
        clickOptions.add("Left Click");
        clickOptions.add("Right Click");

        TempleClient.settingsManager.rSetting(clickTypeSetting = new Setting("Click Type", this, clickOptions, "Left Click"));
        TempleClient.settingsManager.rSetting(new Setting("CPS", this, 10, 1, 100, true));
    }

    @Override
    public void onUpdate() {
        speed = TempleClient.settingsManager.getSettingByName(this.getName(), "CPS").getValDouble();
        String clickType = TempleClient.settingsManager.getSettingByName(this.getName(), "Click Type").getValString();

        int mouseButton = clickType.equals("Left Click") ? 0 : 1;

        if (Mouse.isButtonDown(mouseButton)) {
            if (System.currentTimeMillis() - lastClick > (1.0 / speed) * 1000) {
                lastClick = System.currentTimeMillis();
                if (hold < lastClick) {
                    hold = lastClick;
                }
                int key = clickType.equals("Left Click") ? mc.gameSettings.keyBindAttack.getKeyCode() : mc.gameSettings.keyBindUseItem.getKeyCode();
                KeyBinding.setKeyBindState(key, true);
                KeyBinding.onTick(key);
            } else if (System.currentTimeMillis() - hold > holdLength * 1000) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
            }
        }
    }
}