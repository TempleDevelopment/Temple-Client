package xyz.templecheats.templeclient.features.module.modules.player;

import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.EnumHand;
import org.lwjgl.input.Keyboard;
import team.stiff.pomelo.impl.annotated.handler.annotation.Listener;
import xyz.templecheats.templeclient.event.events.network.PacketEvent;
import xyz.templecheats.templeclient.features.module.Module;
import xyz.templecheats.templeclient.manager.InventoryManager;

public class AutoFish extends Module {
    /****************************************************************
     *                      Variables
     ****************************************************************/
    public AutoFish() {
        super("AutoFish", "Automatically reels your fishing rod", Keyboard.KEY_NONE, Category.Player);
    }

    //TODO: add a delay to the rod going back to water. only if users noticed it's getting flagged by anti-cheat
    @Listener
    public void onPacketRecieve(PacketEvent.Receive event) {
        if (InventoryManager.getHeldItem(Items.FISHING_ROD) && event.getPacket() instanceof SPacketSoundEffect
                && SoundEvents.ENTITY_BOBBER_SPLASH.equals(((SPacketSoundEffect) event.getPacket()).getSound())) {
            mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND); //better than right-clicking since it cannot be interrupted + we don't have to reset it to false
            mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND); //this line is for putting the rod back in the water and the one above is for catching the fish
        }
    }
}