package xyz.templecheats.templeclient.features.module.modules.combat;

import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.EnumSetting;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;

import java.util.function.IntSupplier;

public class AutoClicker extends Module {
    private final IntSetting cps = new IntSetting("CPS", this, 1, 100, 10);
    private final EnumSetting<ClickType> clickType = new EnumSetting<>("Click Type", this, ClickType.LeftClick);

    private long lastClick;
    private long hold;

    private double holdLength;

    public AutoClicker() {
        super("AutoClicker","Automatically clicks the mouse while mouse is held down", Keyboard.KEY_NONE, Category.Combat);

        registerSettings(cps, clickType);
    }

    @Override
    public void onUpdate() {
        if (Mouse.isButtonDown(clickType.value().button)) {
            if (System.currentTimeMillis() - lastClick > (1d / cps.intValue()) * 1000) {
                lastClick = System.currentTimeMillis();
                if (hold < lastClick) {
                    hold = lastClick;
                }
                int key = clickType.value().key.getAsInt();
                KeyBinding.setKeyBindState(key, true);
                KeyBinding.onTick(key);
            } else if (System.currentTimeMillis() - hold > holdLength * 1000) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
            }
        }
    }

    private enum ClickType {
        LeftClick(0, () -> mc.gameSettings.keyBindAttack.getKeyCode()),
        RightClick(1, () -> mc.gameSettings.keyBindUseItem.getKeyCode());

        public final int button;
        public final IntSupplier key;

        ClickType(int button, IntSupplier key) {
            this.button = button;
            this.key = key;
        }
    }
}