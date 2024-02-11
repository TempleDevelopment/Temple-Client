package xyz.templecheats.templeclient.impl.modules.combat;

import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.impl.modules.Module;

public class FastXP extends Module {

    public FastXP() {
        super("FastXP","Increases XP throwing speed", Keyboard.KEY_NONE, Category.Combat);
    }

    @Override
    public void onUpdate() {
        if (mc.player.getHeldItemMainhand().getItem().equals(Items.EXPERIENCE_BOTTLE) && mc.gameSettings.keyBindUseItem.isKeyDown()) {
            mc.getConnection().sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
        }
    }
}