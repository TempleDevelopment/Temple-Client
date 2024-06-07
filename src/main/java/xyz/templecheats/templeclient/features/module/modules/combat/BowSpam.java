package xyz.templecheats.templeclient.features.module.modules.combat;

import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;

public class BowSpam extends Module {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    private final IntSetting drawLength = new IntSetting("Draw Length", this, 3, 21, 3);

    /****************************************************************
     *                      Variables
     ****************************************************************/
    private int tickCounter = 0;

    public BowSpam() {
        super("BowSpam", "Spam arrows", Keyboard.KEY_NONE, Category.Combat);
        this.registerSettings(drawLength);
    }

    public void onUpdate() {
        if (mc.player.getHeldItemMainhand().getItem() instanceof ItemBow && mc.player.isHandActive() && mc.player.getItemInUseMaxCount() >= drawLength.intValue()) {
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(mc.player.getActiveHand()));
            mc.player.stopActiveHand();
        }
    }
}