package xyz.templecheats.templeclient.features.module.modules.player;

import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;

public class FastUse extends Module {
    /*
     * Settings
     */
    private final IntSetting delay = new IntSetting("Delay", this, 1, 10, 1);

    /*
     * Variables
     */
    private int timer = 0;

    public FastUse() {
        super("FastUse", "Use items faster", Keyboard.KEY_NONE, Category.Player);
        this.registerSettings(delay);
    }
//TODO: modify mc.player itemusedelay instead of spamming packets
    @Override
    public void onUpdate() {
        if (++timer >= delay.intValue()) {
            timer = 0;
            if (mc.player.getHeldItemMainhand().getItem().equals(Items.EXPERIENCE_BOTTLE) && mc.gameSettings.keyBindUseItem.isKeyDown()) {
                mc.getConnection().sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
            }
        }
    }
}