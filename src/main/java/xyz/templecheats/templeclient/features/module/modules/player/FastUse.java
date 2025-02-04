package xyz.templecheats.templeclient.features.module.modules.player;

import net.minecraft.init.Items;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.mixins.accessor.IMixinMinecraft;
import xyz.templecheats.templeclient.util.setting.impl.BooleanSetting;

public class FastUse extends Module {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    private final BooleanSetting exp = new BooleanSetting("Exp", this, false);

    public FastUse() {
        super("FastUse", "Use items faster", Keyboard.KEY_NONE, Category.Player);
        registerSettings(exp);
    }

    @Override
    public void onUpdate() {
        if (exp.booleanValue() && (mc.player.getHeldItemMainhand().getItem() == Items.EXPERIENCE_BOTTLE || mc.player.getHeldItemOffhand().getItem() == Items.EXPERIENCE_BOTTLE)) {
            ((IMixinMinecraft) mc).setRightClickDelayTimer(0);
        }
    }
}
