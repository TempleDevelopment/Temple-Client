package xyz.templecheats.templeclient.features.module.modules.player;

import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;

public class FastUse extends Module {

    public FastUse() {
        super("FastUse", "Use items faster", Keyboard.KEY_NONE, Category.Player);
    }

    @Override
    public void onUpdate() {
        if (mc.player.getHeldItemMainhand().getItem().equals(Items.EXPERIENCE_BOTTLE) && mc.gameSettings.keyBindUseItem.isKeyDown()) {
            mc.getConnection().sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
        }
    }
}