package xyz.templecheats.templeclient.features.module.modules.misc;

import net.minecraft.util.EnumHand;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;

//TODO: Add Move & Sneak, i was running into issues with the player not moving and sneaking when i added it.
public class AntiAFK extends Module {
    /*
     * Settings
     */
    private final IntSetting delay = new IntSetting("Delay", this, 10, 60, 15);
    private final BooleanSetting jump = new BooleanSetting("Jump", this, true);
    private final BooleanSetting swing = new BooleanSetting("Swing", this, true);
    private final BooleanSetting rotate = new BooleanSetting("Rotate", this, true);
    private final BooleanSetting chat = new BooleanSetting("Chat", this, true);

    /*
     * Variables
     */
    private int tickCount = 0;

    public AntiAFK() {
        super("AntiAFK", "Prevents AFK kick", Keyboard.KEY_NONE, Category.Misc);
        registerSettings(chat, jump, swing, rotate, delay);
    }

    @Override
    public void onUpdate() {
        if (tickCount >= delay.intValue() * 20) {
            if (jump.booleanValue()) {
                mc.player.jump();
            }
            if (swing.booleanValue()) {
                mc.player.swingArm(EnumHand.MAIN_HAND);
            }
            if (rotate.booleanValue()) {
                mc.player.rotationYaw = (mc.player.rotationYaw + 180) % 360;
            }
            tickCount = 0;
            if (chat.booleanValue())
                mc.player.sendChatMessage("Hello World!");
        } else {
            tickCount++;
        }
    }
}
