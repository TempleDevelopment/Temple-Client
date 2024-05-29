package xyz.templecheats.templeclient.features.module.modules.combat;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.util.setting.impl.IntSetting;

public class BowSpam extends Module {
    /****************************************************************
     *                      Settings
     ****************************************************************/
    private final IntSetting ticks = new IntSetting("Ticks", this, 1, 10, 5);

    /****************************************************************
     *                      Variables
     ****************************************************************/
    private int tickCounter = 0;

    public BowSpam() {
        super("BowSpam", "Spam arrows", Keyboard.KEY_NONE, Category.Combat);
        this.registerSettings(ticks);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.player == null || mc.world == null) {
            return;
        }

        ItemStack itemInHand = mc.player.getHeldItemMainhand();
        if (itemInHand.getItem() == Items.BOW && mc.player.isHandActive()) {
            if (tickCounter >= ticks.intValue()) {
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
                mc.player.stopActiveHand();
                mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(mc.player.getActiveHand()));
                tickCounter = 0;
            } else {
                tickCounter++;
            }
        }
    }
}