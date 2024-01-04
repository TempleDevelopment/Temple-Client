package xyz.templecheats.templeclient.impl.modules.misc;

import xyz.templecheats.templeclient.impl.modules.Module;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class Particles extends Module {
    public Particles() {
        super("Particles", Keyboard.KEY_NONE, Category.MISC);
    }

    @SubscribeEvent
    public void onAttack(AttackEntityEvent e) {
        for (int i = 12; i >= 0; i = i - 1) {
            mc.player.onCriticalHit(e.getTarget());
        }
    }
}