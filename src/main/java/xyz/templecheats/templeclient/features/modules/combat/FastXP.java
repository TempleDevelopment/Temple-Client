package xyz.templecheats.templeclient.features.modules.combat;

import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.modules.Module;

public class FastXP extends Module {

    public FastXP() {
        super("FastXP", Keyboard.KEY_NONE, Category.COMBAT);
    }

    @Override
    public void onUpdate() {
        if (mc.player.getHeldItemMainhand().getItem().equals(Items.EXPERIENCE_BOTTLE) && mc.gameSettings.keyBindUseItem.isKeyDown()) {
            mc.getConnection().sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
        }
    }
}